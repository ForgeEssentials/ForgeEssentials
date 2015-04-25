package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandPay extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "pay";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".pay";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/pay <player> <amount>: Pay another player from your wallet";
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
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender);
        parse(arguments);
        return arguments.tabCompletion;
    }

    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Player needed");
        UserIdent player = arguments.parsePlayer();

        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing value");
        Long amount = FunctionHelper.tryParseLong(arguments.remove());
        if (amount == null)
            throw new TranslatedCommandException("Invalid number");
        if (amount < 1)
            throw new TranslatedCommandException("Invalid number");

        Wallet sender = APIRegistry.economy.getWallet(arguments.userIdent);
        if (!sender.withdraw(amount))
            throw new TranslatedCommandException("You do not have enough %s in your wallet", APIRegistry.economy.currency(2));
        arguments.confirm(Translator.format("You paid %s to %s. You now have %s", //
                APIRegistry.economy.toString(amount), player.getUsernameOrUUID(), sender.toString()));

        Wallet receiver = APIRegistry.economy.getWallet(player);
        receiver.add(amount);
        OutputHandler.chatConfirmation(player.getPlayer(), Translator.format("You were paid %s from %s. You now have %s", //
                APIRegistry.economy.toString(amount), arguments.sender.getCommandSenderName(), receiver.toString()));
    }

}
