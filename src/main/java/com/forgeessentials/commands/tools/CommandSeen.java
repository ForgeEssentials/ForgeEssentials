package com.forgeessentials.commands.tools;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;

public class CommandSeen extends FEcmdModuleCommands
{

    @Override
    public String getCommandName()
    {
        return "seen";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/seen <player>: Check when a player has been last seen online";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
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
            return arguments.tabCompletion;
        }
        return arguments.tabCompletion;
    }

    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        UserIdent player = arguments.parsePlayer(false);

        if (player.hasPlayer())
        {
            arguments.confirm(Translator.format("Player %s is currently online", player.getUsernameOrUuid()));
            return;
        }

        if (!player.hasUuid() || !PlayerInfo.exists(player.getUuid()))
            throw new PlayerNotFoundException();

        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        long t = (System.currentTimeMillis() - pi.getLastLogout().getTime()) / 1000;
        arguments.confirm(Translator.format("Player %s was last seen %s ago", player.getUsernameOrUuid(), FunctionHelper.formatDateTimeReadable(t, false)));
        PlayerInfo.discard(pi.ident.getUuid());
    }

}
