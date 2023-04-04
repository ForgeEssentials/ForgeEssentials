package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandTrade extends ForgeEssentialsCommandBuilder
{

    public CommandTrade(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "trade";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".trade";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("price", LongArgumentType.longArg())
                                .executes(CommandContext -> execute(CommandContext, "tradeP")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "getdef")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
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
            throw new TranslatedCommandException("You need to hold an item first!");

        final long price;
        if (params.equals("getdef"))
        {
            Long p = ModuleEconomy.getItemPrice(itemStack, getIdent(ctx.getSource()));
            if (p == null)
                throw new TranslatedCommandException("No default price available. Please specify price.");
            price = p;
        }
        else
            price = LongArgumentType.getLong(ctx, "price");

        final Wallet sellerWallet = APIRegistry.economy.getWallet(getIdent(ctx.getSource()));
        final Wallet buyerWallet = APIRegistry.economy.getWallet(buyer);

        if (!buyerWallet.covers(price * itemStack.getCount()))
            throw new TranslatedCommandException("%s can't affort that!", buyer.getUsernameOrUuid());

        QuestionerCallback sellerHandler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response) throws CommandException
            {
                if (response == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Trade request timed out");
                    return;
                }
                else if (response == false)
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
                        else if (response == false)
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Trade declined"));
                            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Player %s declined the trade", buyer.getUsernameOrUuid());
                            return;
                        }

                        ItemStack currentItemStack = getServerPlayer(ctx.getSource()).getMainHandItem();
                        if (!ItemStack.isSame(currentItemStack, itemStack) || !ItemStack.isSame(currentItemStack, itemStack))
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Error in transaction"));
                            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate("You need to keep the item equipped until trade is finished!"));
                            return;
                        }

                        if (!buyerWallet.withdraw(price * itemStack.getCount()))
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("You can't afford that"));
                            return;
                        }
                        sellerWallet.add(price * itemStack.getCount());

                        PlayerInventory inventory = getServerPlayer(ctx.getSource()).inventory;
                        inventory.items.set(inventory.selected, ItemStack.EMPTY);
                        PlayerUtil.give(buyer.getPlayerMP(), currentItemStack);

                        String priceStr = APIRegistry.economy.toString(price);
                        String totalPriceStr = APIRegistry.economy.toString(price * itemStack.getCount());
                        String buyerMsg = Translator.format("Bought %d x %s from %s for %s each (%s)", //
                                itemStack.getCount(), itemStack.getDisplayName(), //
                                getIdent(ctx.getSource()).getUsernameOrUuid(), //
                                priceStr, totalPriceStr);
                        String sellerMsg = Translator.format("Sold %d x %s to %s for %s each (%s)", //
                                itemStack.getCount(), itemStack.getDisplayName(), //
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
                        message = Translator.format("Buy one %s for %s from %s?", itemStack.getDisplayName(), APIRegistry.economy.toString(price),
                                getServerPlayer(ctx.getSource()).getDisplayName().getString());
                    else
                        message = Translator.format("Buy %d x %s each for %s (total: %s) from %s?", itemStack.getCount(), itemStack.getDisplayName(),
                                APIRegistry.economy.toString(price), APIRegistry.economy.toString(price * itemStack.getCount()),
                                getServerPlayer(ctx.getSource()).getDisplayName().getString());
                    Questioner.addChecked(buyer.getPlayerMP().createCommandSourceStack(), message, buyerHandler, 60);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Waiting on %s...", buyer.getUsernameOrUuid()));
                }
                catch (QuestionerStillActiveException.CommandException e)
                {
                    throw new QuestionerStillActiveException.CommandException();
                }
            }
        };
        String message;
        if (itemStack.getCount() == 1)
            message = Translator
                    .format("Sell one %s for %s to %s?", itemStack.getDisplayName(), APIRegistry.economy.toString(price), buyer.getUsernameOrUuid());
        else
            message = Translator.format("Sell %d x %s each for %s (total: %s) to %s?", itemStack.getCount(), itemStack.getDisplayName(),
                    APIRegistry.economy.toString(price), APIRegistry.economy.toString(price * itemStack.getCount()), buyer.getUsernameOrUuid());
        Questioner.addChecked(ctx.getSource(), message, sellerHandler, 20);
        return Command.SINGLE_SUCCESS;
    }
}
