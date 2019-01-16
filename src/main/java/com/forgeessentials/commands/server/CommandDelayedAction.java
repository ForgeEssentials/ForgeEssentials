package com.forgeessentials.commands.server;

import java.util.TimerTask;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandDelayedAction extends ParserCommandBase
{

    @Override
    public String getName()
    {
        return "fedelayedaction";
    }

    @Override
    public String[] getDefaultAliases() {
        return new String[]{"delayedaction"};
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/delayedaction [time] [command] Run a command after a specified timeout.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".delayedaction";
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        long time = arguments.parseTimeReadable();
        final String execute = StringUtils.join(arguments.args.iterator(), " ");
        if (arguments.isTabCompletion)
            return;
        TaskRegistry.schedule(new TimerTask() {
            @Override
            public void run()
            {
                arguments.server.getCommandManager().executeCommand(arguments.sender, execute);
            }
        }, time);
        arguments.notify("Timer set to run command '%s' in %s", execute, ChatOutputHandler.formatTimeDurationReadableMilli(time, true));

    }

}
