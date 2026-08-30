package com.forgeessentials.chataddon.irc.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;

import org.pircbotx.hooks.events.MessageEvent;

import com.forgeessentials.chataddon.irc.IrcCommand;
import com.forgeessentials.chataddon.irc.ModuleIRCBridge;


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
    	event.respondWith("List of commands:");
        for (Entry<String, IrcCommand> command : ModuleIRCBridge.getInstance().commands.entrySet())
        {
        	event.respondWith(COMMAND_CHAR + command.getKey() + " "
                    + command.getValue().getUsage() + ": " + command.getValue().getCommandHelp());
        }
    }

}
