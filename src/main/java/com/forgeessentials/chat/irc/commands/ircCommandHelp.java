package com.forgeessentials.chat.irc.commands;

import org.pircbotx.User;

public class ircCommandHelp extends ircCommand
{

	@Override
	public String[] getAliases()
	{
		return new String[]{};
	}

	@Override
	public String getCommandInfo()
	{
		return "Returns a list of commands and their usage";
	}

	@Override
	public String getCommandUsage()
	{
		return "%help";
	}

	@Override
	public void execute(String[] args, User user)
	{
		user.sendMessage("Help:");
		for(ircCommand cmd : ircCommands.ircCommands) {
			user.sendMessage("");
			user.sendMessage("Command: " + cmd.getCommandName());
			user.sendMessage("Aliases: " + cmd.getAliasesAsString());
			user.sendMessage("Usage: " + cmd.getCommandUsage());
			user.sendMessage("Info: " + cmd.getCommandInfo());
		}
	}

}
