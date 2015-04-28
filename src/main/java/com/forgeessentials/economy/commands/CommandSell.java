package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

import cpw.mods.fml.common.registry.GameData;

public class CommandSell extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "sell";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".sell";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/sell <item> <amount> [meta]";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        final boolean holdingItem;
        final ItemStack itemStack;
        final int amount;
        final int meta;
        if (arguments.isEmpty())
        {
            holdingItem = true;
            itemStack = arguments.senderPlayer.getCurrentEquippedItem();
            if (itemStack == null)
                throw new TranslatedCommandException("You need to hold an item first!");
            amount = itemStack.stackSize;
            meta = itemStack.getItemDamage();
        }
        else
        {
            holdingItem = false;
            // Parse item name, amount and meta
            if (arguments.isTabCompletion && arguments.size() == 1)
            {
                for (Object item : GameData.getItemRegistry().getKeys())
                    if (item.toString().startsWith(arguments.peek()))
                        arguments.tabCompletion.add(item.toString());
                for (Object item : GameData.getBlockRegistry().getKeys())
                    if (item.toString().startsWith(arguments.peek()))
                        arguments.tabCompletion.add(item.toString());
                for (Object item : GameData.getItemRegistry().getKeys())
                    if (item.toString().startsWith("minecraft:" + arguments.peek()))
                        arguments.tabCompletion.add(item.toString().substring(10));
                for (Object item : GameData.getBlockRegistry().getKeys())
                    if (item.toString().startsWith("minecraft:" + arguments.peek()))
                        arguments.tabCompletion.add(item.toString().substring(10));
                return;
            }
            String itemName = arguments.remove();

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

            Item item = CommandBase.getItemByText(arguments.senderPlayer, itemName);
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
                    ItemStack currentItemStack = arguments.senderPlayer.getCurrentEquippedItem();
                    if (currentItemStack.isItemEqual(itemStack))
                    {
                        removedAmount = Math.min(currentItemStack.stackSize, amount);
                        currentItemStack.stackSize -= removedAmount;
                        if (currentItemStack.stackSize <= 0)
                            arguments.senderPlayer.inventory.mainInventory[arguments.senderPlayer.inventory.currentItem] = null;
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
        try
        {
            String message = Translator.format("Sell %d x %s each for %s (total: %s)?", amount, itemStack.getDisplayName(),
                    APIRegistry.economy.toString(price), APIRegistry.economy.toString(amount * price));
            Questioner.add(arguments.sender, message, handler, 20);
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

}
