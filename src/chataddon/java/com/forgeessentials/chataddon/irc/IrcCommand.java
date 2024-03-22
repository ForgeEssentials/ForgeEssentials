package com.forgeessentials.chataddon.irc;

import java.util.Collection;

import org.pircbotx.hooks.events.MessageEvent;


import net.minecraft.commands.CommandRuntimeException;

public interface IrcCommand
{

    public static final String COMMAND_CHAR = ModuleIRCBridge.COMMAND_CHAR;

    public Collection<String> getCommandNames();

    public String getUsage();

    public String getCommandHelp();

    public boolean isAdminCommand();

    public void processCommand(MessageEvent event, String[] args) throws CommandRuntimeException;

}
