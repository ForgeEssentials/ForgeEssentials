package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.FunctionHelper;

public class CommandWallet extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "wallet";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".wallet";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/wallet: Check your wallet";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
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
            if (!arguments.hasPlayer())
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            arguments.confirm(Translator.format("Your wallet contains %s", APIRegistry.economy.getWallet(arguments.senderPlayer).toString()));
            return;
        }

        UserIdent player = arguments.parsePlayer();
        if (player == null)
            return;
        Wallet wallet = APIRegistry.economy.getWallet(player);

        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.format("Wallet of %s contains %s", player.getUsernameOrUUID(), wallet.toString()));
            return;
        }
        if (arguments.isTabCompletion)
        {
            arguments.tabComplete(new String[] { "set", "add", "remove" });
            return;
        }
        String subCommand = arguments.remove().toLowerCase();

        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing value");
        Long amount = FunctionHelper.tryParseLong(arguments.remove());
        if (amount == null)
            throw new TranslatedCommandException("Invalid number");

        switch (subCommand)
        {
        case "set":
            wallet.set(amount);
            arguments.confirm(Translator.format("Set wallet of %s to %s", player.getUsernameOrUUID(), wallet.toString()));
            break;
        case "add":
            wallet.add(amount);
            arguments.confirm(Translator.format("Added %s to %s's wallet. It now contains %s", //
                    APIRegistry.economy.toString(amount), player.getUsernameOrUUID(), wallet.toString()));
            break;
        case "remove":
            if (!wallet.withdraw(amount))
                throw new TranslatedCommandException("Player %s does not have enough %s in his wallet", //
                        player.getUsernameOrUUID(), APIRegistry.economy.currency(2));
            arguments.confirm(Translator.format("Removed %s from %s's wallet. It now contains %s", //
                    APIRegistry.economy.toString(amount), player.getUsernameOrUUID(), wallet.toString()));
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND);
        }
    }

}
