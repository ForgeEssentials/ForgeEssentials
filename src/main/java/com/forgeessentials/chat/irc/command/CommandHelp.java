package com.forgeessentials.chat.irc.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;

import org.pircbotx.hooks.events.MessageEvent;

import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.chat.irc.IrcHandler;


public class CommandHelp implements IrcCommand
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Collections.singletonList("help");
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
    public void processCommand(MessageEvent event, String[] args)
    {
    	System.out.println("Running help command");
    	event.respondWith("List of commands:");
        for (Entry<String, IrcCommand> command : IrcHandler.getInstance().commands.entrySet())
        {
        	event.respondWith(COMMAND_CHAR + command.getKey() + " "
                    + command.getValue().getUsage() + ": " + command.getValue().getCommandHelp());
        }
    }

}
