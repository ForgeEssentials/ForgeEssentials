package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;

public class CommandHelp implements IrcCommand {

	@Override
	public Collection<String> getCommandNames() {
		return Arrays.asList("help");
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCommandHelp() {
		return "Show help";
	}

	@Override
	public boolean isAdminCommand() {
		return false;
	}

	@Override
	public void processCommand(CommandSource sender, String[] args) throws CommandException {
		ChatOutputHandler.chatConfirmation(sender, "List of commands:");
		for (Entry<String, IrcCommand> command : IrcHandler.getInstance().commands.entrySet()) {
			ChatOutputHandler.chatConfirmation(sender, COMMAND_CHAR + command.getKey() + " "
					+ command.getValue().getUsage() + ": " + command.getValue().getCommandHelp());
		}
	}

}
