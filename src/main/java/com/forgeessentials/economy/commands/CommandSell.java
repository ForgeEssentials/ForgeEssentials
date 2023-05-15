package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
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

public class CommandSell extends ForgeEssentialsCommandBuilder
{

    public CommandSell(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "sell";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".sell";
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
        APIRegistry.perms.registerPermission(getPermissionNode() + ".noconfirm", DefaultPermissionLevel.NONE, "Do not confirm selling items to the server.");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "sellamount")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "sell")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        final boolean holdingItem;
        final ItemStack itemStack;
        final int amount;
        if (params.equals("sell"))
        {
            holdingItem = true;
            itemStack = getServerPlayer(ctx.getSource()).getMainHandItem();
            if (itemStack == ItemStack.EMPTY)
                throw new TranslatedCommandException("You need to hold an item first!");
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
            throw new TranslatedCommandException("This item cannot be sold");

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
                else if (response == false)
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
                            getServerPlayer(ctx.getSource()).inventory.items.set(getServerPlayer(ctx.getSource()).inventory.selected, ItemStack.EMPTY);
                    }
                }
                if (removedAmount < amount)
                    removedAmount += ModuleEconomy.tryRemoveItems(getServerPlayer(ctx.getSource()), itemStack, amount - removedAmount);

                wallet.add(removedAmount * price);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You have sold %d %s to the server for %s", //
                        removedAmount, itemStack.getDisplayName(), APIRegistry.economy.toString(removedAmount * price)));
                ModuleEconomy.confirmNewWalletAmount(getIdent(ctx.getSource()), wallet);
            }
        };
        String message = Translator.format("Sell %d x %s each for %s (total: %s)?", amount, itemStack.getDisplayName(), APIRegistry.economy.toString(price),
                APIRegistry.economy.toString(amount * price));
        if (APIRegistry.perms.checkPermission(getServerPlayer(ctx.getSource()), getPermissionNode() + ".noconfirm"))
        {
            handler.respond(true);
            return Command.SINGLE_SUCCESS;
        }
        try {
			Questioner.addChecked(ctx.getSource(), message, handler, 20);
		} catch (QuestionerStillActiveException e) {
			ChatOutputHandler.chatError(ctx.getSource(), "Cannot run command because player is still answering a question. Please wait a moment");
        	return Command.SINGLE_SUCCESS;
		}
        return Command.SINGLE_SUCCESS;
    }
}
