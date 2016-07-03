package com.forgeessentials.commands.player;

import java.util.List;

import com.forgeessentials.api.APIRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
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
        UserIdent player = arguments.parsePlayer(true, false,true);

        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        long duration = arguments.parseTimeReadable();
        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        pi.startTimeout("tempban", duration * 1000L);
        String reason = arguments.toString();
        if (player.hasPlayer())
            player.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.format("You have been banned %s for %s",
                    ChatOutputHandler.formatTimeDurationReadable(duration, true),reason));
        ChatOutputHandler.sendMessage(MinecraftServer.getServer(),Translator.format("Player %s, has been temporarily banned %s for %s",player.getUsername(), ChatOutputHandler.formatTimeDurationReadable(duration, true), reason));
        APIRegistry.perms.setPlayerPermissionProperty(player,"tempban.reason",reason);
    }
    
}
