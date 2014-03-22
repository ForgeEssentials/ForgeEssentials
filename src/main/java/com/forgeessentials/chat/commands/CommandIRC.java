package com.forgeessentials.chat.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.chat.IRCHelper;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandIRC extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName() {
		return "irc";
	}@Override
	public void processCommand(ICommandSender sender, String[] args) {
	if(args[0].equalsIgnoreCase("reconnect")){
			IRCHelper.reconnect(sender);
		}else if (args[0].equalsIgnoreCase("disconnect")){
			IRCHelper.shutdown();
		}

	}

	@Override
	public boolean canConsoleUseCommand() {
	return true;
	}

	@Override
	public String getCommandPerm() {
	return "ForgeEssentials.Chat.irc";
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/irc [reconnect|disconnect] Connect or disconnect the IRC server bot.";
	}

}
