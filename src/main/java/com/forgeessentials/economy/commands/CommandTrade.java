package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerException.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTrade extends ForgeEssentialsCommandBuilder
{

    public CommandTrade(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "trade";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("price", LongArgumentType.longArg())
                                .executes(CommandContext -> execute(CommandContext, "tradeP")))
                        .executes(CommandContext -> execute(CommandContext, "getdef")))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/trade <player> [price-per-item]:");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "  Trade the item you are holding in your main hand");
            return Command.SINGLE_SUCCESS;
        }

        final UserIdent buyer = getIdent(EntityArgument.getPlayer(ctx, "player"));

        final ItemStack itemStack = getServerPlayer(ctx.getSource()).getMainHandItem();
        if (itemStack == ItemStack.EMPTY)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You need to hold an item first!");
            return Command.SINGLE_SUCCESS;
        }

        final long price;
        if (params.equals("getdef"))
        {
            Long p = ModuleEconomy.getItemPrice(itemStack, getIdent(ctx.getSource()));
            if (p == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "No default price available. Please specify price.");
                return Command.SINGLE_SUCCESS;
            }
            price = p;
        }
        else
            price = LongArgumentType.getLong(ctx, "price");

        final Wallet sellerWallet = APIRegistry.economy.getWallet(getIdent(ctx.getSource()));
        final Wallet buyerWallet = APIRegistry.economy.getWallet(buyer);

        if (!buyerWallet.covers(price * itemStack.getCount()))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "%s can't affort that!", buyer.getUsernameOrUuid());
            return Command.SINGLE_SUCCESS;
        }

        QuestionerCallback sellerHandler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response) throws CommandRuntimeException
            {
                if (response == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Trade request timed out");
                    return;
                }
                else if (!response)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Canceled");
                    return;
                }
                QuestionerCallback buyerHandler = new QuestionerCallback() {
                    @Override
                    public void respond(Boolean response)
                    {
                        if (response == null)
                        {
                            ChatOutputHandler.chatError(ctx.getSource(), "Trade request timed out");
                            return;
                        }
                        else if (!response)
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Trade declined"));
                            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Player %s declined the trade",
                                    buyer.getUsernameOrUuid());
                            return;
                        }

                        ItemStack currentItemStack = getServerPlayer(ctx.getSource()).getMainHandItem();
                        if (!ItemStack.isSame(currentItemStack, itemStack)
                                || !ItemStack.isSame(currentItemStack, itemStack))
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(),
                                    Translator.translate("Error in transaction"));
                            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                                    .translate("You need to keep the item equipped until trade is finished!"));
                            return;
                        }

                        if (!buyerWallet.withdraw(price * itemStack.getCount()))
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(),
                                    Translator.translate("You can't afford that"));
                            return;
                        }
                        sellerWallet.add(price * itemStack.getCount());

                        Inventory inventory = getServerPlayer(ctx.getSource()).getInventory();
                        inventory.items.set(inventory.selected, ItemStack.EMPTY);
                        PlayerUtil.give(buyer.getPlayerMP(), currentItemStack);

                        String priceStr = APIRegistry.economy.toString(price);
                        String totalPriceStr = APIRegistry.economy.toString(price * itemStack.getCount());
                        String buyerMsg = Translator.format("Bought %d x %s from %s for %s each (%s)", //
                                itemStack.getCount(), itemStack.getDisplayName().getString(), //
                                getIdent(ctx.getSource()).getUsernameOrUuid(), //
                                priceStr, totalPriceStr);
                        String sellerMsg = Translator.format("Sold %d x %s to %s for %s each (%s)", //
                                itemStack.getCount(), itemStack.getDisplayName().getString(), //
                                buyer.getUsernameOrUuid(), //
                                priceStr, totalPriceStr);
                        ChatOutputHandler.chatNotification(ctx.getSource(), sellerMsg);
                        ChatOutputHandler.chatNotification(buyer.getPlayerMP(), buyerMsg);
                    }
                };
                try
                {
                    String message;
                    if (itemStack.getCount() == 1)
                        message = Translator.format("Buy one %s for %s from %s?",
                                itemStack.getDisplayName().getString(), APIRegistry.economy.toString(price),
                                getServerPlayer(ctx.getSource()).getDisplayName().getString());
                    else
                        message = Translator.format("Buy %d x %s each for %s (total: %s) from %s?",
                                itemStack.getCount(), itemStack.getDisplayName().getString(),
                                APIRegistry.economy.toString(price),
                                APIRegistry.economy.toString(price * itemStack.getCount()),
                                getServerPlayer(ctx.getSource()).getDisplayName().getString());
                    Questioner.addChecked(buyer.getPlayerMP(), message, buyerHandler, 60);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(),
                            Translator.format("Waiting on %s...", buyer.getUsernameOrUuid()));
                }
                catch (QuestionerStillActiveException e)
                {
                    ChatOutputHandler.chatError(ctx.getSource(),
                            "Cannot run command because player is still answering a question. Please wait a moment");
                    return;
                }
            }
        };
        String message;
        if (itemStack.getCount() == 1)
            message = Translator.format("Sell one %s for %s to %s?", itemStack.getDisplayName().getString(),
                    APIRegistry.economy.toString(price), buyer.getUsernameOrUuid());
        else
            message = Translator.format("Sell %d x %s each for %s (total: %s) to %s?", itemStack.getCount(),
                    itemStack.getDisplayName().getString(), APIRegistry.economy.toString(price),
                    APIRegistry.economy.toString(price * itemStack.getCount()), buyer.getUsernameOrUuid());
        try
        {
            Questioner.addChecked(getServerPlayer(ctx.getSource()), message, sellerHandler, 20);
        }
        catch (QuestionerStillActiveException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Cannot run command because player is still answering a question. Please wait a moment");
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
