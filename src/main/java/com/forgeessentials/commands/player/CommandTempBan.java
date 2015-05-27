package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;

public class CommandTempBan extends FEcmdModuleCommands
{

    @Override
    public String getCommandName()
    {
        return "tempban";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/tempban <player> <duration>: Tempban a player";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
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
        UserIdent player = arguments.parsePlayer(true);

        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        int duration = arguments.parseInt();

        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        pi.startTimeout("tempban", duration * 1000);
        if (player.hasPlayer())
            player.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.format("You have been banned for %s",
                    FunctionHelper.formatDateTimeReadable(duration, true)));
    }
}
