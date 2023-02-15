package com.forgeessentials.chat.irc;

import java.util.Collection;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;

public interface IrcCommand
{

    public static final String COMMAND_CHAR = IrcHandler.COMMAND_CHAR;

    public Collection<String> getCommandNames();

    public String getUsage();

    public String getCommandHelp();

    public boolean isAdminCommand();

    public void processCommand(CommandSource sender, String[] args) throws CommandException;

}
