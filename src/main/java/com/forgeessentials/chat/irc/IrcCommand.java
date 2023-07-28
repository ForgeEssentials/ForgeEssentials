package com.forgeessentials.chat.irc;

import java.util.Collection;

import org.pircbotx.hooks.events.MessageEvent;

import net.minecraft.command.CommandException;

public interface IrcCommand
{

    public static final String COMMAND_CHAR = IrcHandler.COMMAND_CHAR;

    public Collection<String> getCommandNames();

    public String getUsage();

    public String getCommandHelp();

    public boolean isAdminCommand();

    public void processCommand(MessageEvent event, String[] args) throws CommandException;

}
