package com.forgeessentials.chat.irc.commands;

import net.minecraft.server.MinecraftServer;

import org.pircbotx.User;

import com.forgeessentials.util.FunctionHelper;
import com.google.common.collect.HashMultimap;

public class ircCommandList extends ircCommand
{

	@Override
	public String[] getAliases()
	{
		return new String[]{"online", "who", "players"};
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
		HashMultimap<String, String> map = HashMultimap.create();
        for (String username : MinecraftServer.getServer().getConfigurationManager().getAllUsernames())
            map.put(FunctionHelper.getGroupRankString(username), username);
        user.sendMessage("Players online: ");
        for (String group : map.keySet())
            user.sendMessage(group + ": " + FunctionHelper.niceJoin(map.get(group).toArray()));
	}

}
