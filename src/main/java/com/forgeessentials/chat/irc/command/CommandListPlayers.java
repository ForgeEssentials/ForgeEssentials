package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandListPlayers implements IrcCommand {

	@Override
	public Collection<String> getCommandNames() {
		return Arrays.asList("list", "online", "players");
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCommandHelp() {
		return "Show list of online players";
	}

	@Override
	public boolean isAdminCommand() {
		return false;
	}

	@Override
	public void processCommand(CommandSource sender, String[] args) throws CommandException {
		ChatOutputHandler.chatConfirmation(sender, "List of players:");
		for (String username : ServerLifecycleHooks.getCurrentServer().getPlayerNames())
			ChatOutputHandler.chatConfirmation(sender, " - " + username);
	}

}
