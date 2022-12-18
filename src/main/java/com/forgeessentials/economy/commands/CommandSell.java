package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandSell extends BaseCommand
{

    public CommandSell(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        final boolean holdingItem;
        final ItemStack itemStack;
        final int amount;
        final int meta;
        if (arguments.isEmpty() || arguments.peek().equalsIgnoreCase("yes") || arguments.peek().equalsIgnoreCase("y"))
        {
            holdingItem = true;
            itemStack = arguments.senderPlayer.getMainHandItem();
            if (itemStack == ItemStack.EMPTY)
                throw new TranslatedCommandException("You need to hold an item first!");
            amount = itemStack.getCount();
            meta = itemStack.getDamageValue();
        }
        else
        {
            holdingItem = false;
            // Parse item, amount and meta
            Item item = arguments.parseItem();

            // Parse amount
            try
            {
                amount = Integer.parseInt(arguments.remove());
            }
            catch (NumberFormatException e)
            {
                throw new TranslatedCommandException("Invalid number");
            }

            // Parse optional meta
            if (!arguments.isEmpty())
            {
                try
                {
                    meta = Integer.parseInt(arguments.remove());
                }
                catch (NumberFormatException e)
                {
                    throw new TranslatedCommandException("Invalid number");
                }
            }
            else
                meta = -1;

            itemStack = new ItemStack(item, amount, meta);
        }

        if (arguments.isTabCompletion)
            return;

        final Long price = ModuleEconomy.getItemPrice(itemStack, arguments.ident);
        if (price == null || price <= 0)
            throw new TranslatedCommandException("This item cannot be sold");

        final Wallet wallet = APIRegistry.economy.getWallet(arguments.ident);

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                {
                    arguments.error("Sale request timed out");
                    return;
                }
                else if (response == false)
                {
                    arguments.error("Sale canceled");
                    return;
                }

                int removedAmount = 0;
                if (holdingItem)
                {
                    ItemStack currentItemStack = arguments.senderPlayer.getMainHandItem();
                    if (currentItemStack.equals(itemStack))
                    {
                        removedAmount = Math.min(currentItemStack.getCount(), amount);
                        currentItemStack.setCount(currentItemStack.getCount() - removedAmount);
                        if (currentItemStack.getCount() <= 0)
                            arguments.senderPlayer.inventory.items.set(arguments.senderPlayer.inventory.selected, ItemStack.EMPTY);
                    }
                }
                if (removedAmount < amount)
                    removedAmount += ModuleEconomy.tryRemoveItems(arguments.senderPlayer, itemStack, amount - removedAmount);

                wallet.add(removedAmount * price);
                arguments.confirm(Translator.format("You have sold %d %s to the server for %s", //
                        removedAmount, itemStack.getDisplayName(), APIRegistry.economy.toString(removedAmount * price)));
                ModuleEconomy.confirmNewWalletAmount(arguments.ident, wallet);
            }
        };
        String message = Translator.format("Sell %d x %s each for %s (total: %s)?", amount, itemStack.getDisplayName(), APIRegistry.economy.toString(price),
                APIRegistry.economy.toString(amount * price));
        if (APIRegistry.perms.checkPermission(arguments.senderPlayer, getPermissionNode() + ".noconfirm"))
        {
            handler.respond(true);
            return;
        }
        Questioner.addChecked(arguments.sender, message, handler, 20);
    }
}
