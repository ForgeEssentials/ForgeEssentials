package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerException.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandSell extends ForgeEssentialsCommandBuilder
{

    public CommandSell(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "sell";
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
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleEconomy.PERM_COMMAND + ".sell.noconfirm", DefaultPermissionLevel.NONE,
                "Do not confirm selling items to the server.");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "sellamount"))))
                .executes(CommandContext -> execute(CommandContext, "sell"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        final boolean holdingItem;
        final ItemStack itemStack;
        final int amount;
        if (params.equals("sell"))
        {
            holdingItem = true;
            itemStack = getServerPlayer(ctx.getSource()).getMainHandItem();
            if (itemStack == ItemStack.EMPTY)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "You need to hold an item first!");
                return Command.SINGLE_SUCCESS;
            }
            amount = itemStack.getCount();
        }
        else
        {
            holdingItem = false;
            // Parse item, and amount
            Item item = ItemArgument.getItem(ctx, "item").getItem();

            // Parse amount
            amount = IntegerArgumentType.getInteger(ctx, "amount");

            itemStack = new ItemStack(item, amount);
        }

        final Long price = ModuleEconomy.getItemPrice(itemStack, getIdent(ctx.getSource()));
        if (price == null || price <= 0)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "This item cannot be sold");
            return Command.SINGLE_SUCCESS;
        }

        final Wallet wallet = APIRegistry.economy.getWallet(getIdent(ctx.getSource()));

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Sale request timed out");
                    return;
                }
                else if (!response)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Sale canceled");
                    return;
                }

                int removedAmount = 0;
                if (holdingItem)
                {
                    ItemStack currentItemStack = getServerPlayer(ctx.getSource()).getMainHandItem();
                    if (currentItemStack.equals(itemStack))
                    {
                        removedAmount = Math.min(currentItemStack.getCount(), amount);
                        currentItemStack.setCount(currentItemStack.getCount() - removedAmount);
                        if (currentItemStack.getCount() <= 0)
                            getServerPlayer(ctx.getSource()).getInventory().items
                                    .set(getServerPlayer(ctx.getSource()).getInventory().selected, ItemStack.EMPTY);
                    }
                }
                if (removedAmount < amount)
                    removedAmount += ModuleEconomy.tryRemoveItems(getServerPlayer(ctx.getSource()), itemStack,
                            amount - removedAmount);

                wallet.add(removedAmount * price);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("You have sold %d %s to the server for %s", //
                                removedAmount, itemStack.getDisplayName().getString(),
                                APIRegistry.economy.toString(removedAmount * price)));
                ModuleEconomy.confirmNewWalletAmount(getIdent(ctx.getSource()), wallet);
            }
        };
        String message = Translator.format("Sell %d x %s each for %s (total: %s)?", amount,
                itemStack.getDisplayName().getString(), APIRegistry.economy.toString(price),
                APIRegistry.economy.toString(amount * price));
        if (APIRegistry.perms.checkPermission(getServerPlayer(ctx.getSource()), ModuleEconomy.PERM_COMMAND + ".sell.noconfirm"))
        {
            handler.respond(true);
            return Command.SINGLE_SUCCESS;
        }
        try
        {
            Questioner.addChecked(getServerPlayer(ctx.getSource()), message, handler, 20);
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
