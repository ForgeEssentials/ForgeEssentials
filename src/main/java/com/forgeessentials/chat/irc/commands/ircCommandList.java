package com.forgeessentials.chat.irc.commands;

import net.minecraft.server.MinecraftServer;

import org.pircbotx.User;

import com.forgeessentials.chat.irc.IRCHelper;

public class ircCommandList extends ircCommand {

    @Override
    public String[] getAliases()
    {
        return new String[] { "online", "who", "players" };
    }

    @Override
    public String getCommandInfo()
    {
        return "Lists all the players on the server";
    }

    @Override
    public String getCommandUsage()
    {
        return "%list";
    }

    @Override
    public void execute(String[] args, User user)
    {
    	IRCHelper.privateMessage(user,"Players online: ");
        for (String username : MinecraftServer.getServer().getConfigurationManager().getAllUsernames())
        {
        	IRCHelper.privateMessage(user,username);
        }
    }

}
