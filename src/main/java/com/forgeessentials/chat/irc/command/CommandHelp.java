package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

import net.minecraft.command.CommandException;

import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.chat.irc.IrcCommand.IrcCommandParser;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.util.CommandParserArgs;

public class CommandHelp extends IrcCommandParser
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("help");
    }

    @Override
    public String getUsage()
    {
        return "";
    }

    @Override
    public String getCommandHelp()
    {
        return "Show help";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        arguments.confirm("List of commands:");
        for (Entry<String, IrcCommand> command : IrcHandler.getInstance().commands.entrySet())
        {
            arguments.confirm(COMMAND_CHAR + command.getKey() + " " + command.getValue().getUsage() + ": " + command.getValue().getCommandHelp());
        }
    }

}
