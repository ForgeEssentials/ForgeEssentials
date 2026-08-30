package com.forgeessentials.chataddon.irc.commands;

import java.util.Arrays;
import java.util.Collection;

import org.pircbotx.hooks.events.MessageEvent;

import com.forgeessentials.chataddon.irc.IrcCommand;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandListPlayers implements IrcCommand
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("list", "online", "players");
    }

    @Override
    public String getUsage()
    {
        return "";
    }

    @Override
    public String getCommandHelp()
    {
        return "Show list of online players";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void processCommand(MessageEvent event, String[] args)
    {
    	event.respondWith("List of players:");
        for (String username : ServerLifecycleHooks.getCurrentServer().getPlayerNames())
        	event.respondWith(" - " + username);
    }

}
