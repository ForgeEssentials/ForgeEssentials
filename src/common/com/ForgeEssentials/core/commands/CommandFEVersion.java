package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.Version;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandFEVersion extends ForgeEssentialsCommandBase{

	@Override
	public String getCommandName() {
		return "feversion";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		player.addChatMessage("You are currently running ForgeEssentials version " + Version.version);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("You are currently running ForgeEssentials version " + Version.version);
	}

	@Override
	public String getUsageConsole() {
		return "/feversion";
	}

	@Override
	public String getUsagePlayer(EntityPlayer player) {
		return "/feversion";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		return true;
	}
}