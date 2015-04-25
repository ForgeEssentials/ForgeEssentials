package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;

import cpw.mods.fml.common.registry.GameData;

public class CommandSell extends ForgeEssentialsCommandBase
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
    public void processCommand(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender);
        parse(arguments);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
        try
        {
            parse(arguments);
        }
        catch (CommandException e)
        {
            return null;
        }
        return arguments.tabCompletion;
    }

    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("Usage: /sell <item> <amount> [meta]");
            return;
        }

        // Parse item name
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing item name");
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
        int amount = 0;
        try
        {
            amount = Integer.parseInt(arguments.remove());
        }
        catch (NumberFormatException e)
        {
            throw new TranslatedCommandException("Invalid number");
        }
        
        // Parse optional meta
        int meta = -1;
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

        if (arguments.isTabCompletion)
            return;

        UserIdent ident = new UserIdent(arguments.senderPlayer);
        Item item = CommandBase.getItemByText(arguments.senderPlayer, itemName);
        ItemStack itemStack = new ItemStack(item, amount, meta);
        long price = ModuleEconomy.getItemPrice(itemStack.getItem(), ident);
        amount = ModuleEconomy.tryRemoveItems(arguments.senderPlayer, itemStack, amount);

        Wallet wallet = APIRegistry.economy.getWallet(ident);
        wallet.add(amount * price);
        arguments.confirm(Translator.format("You have sold %d %s to the server for %s", //
                amount, itemStack.getDisplayName(), APIRegistry.economy.currency(amount * price)));
        ModuleEconomy.confirmNewWalletAmount(ident, wallet);
    }

}
