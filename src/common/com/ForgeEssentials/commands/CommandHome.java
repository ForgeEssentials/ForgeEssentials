package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandHome extends ForgeEssentialsCommandBase{

	@Override
	public String getCommandName() {
		return "Go Home";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		OutputHandler.chatError(player, "Not currently implemented");
		
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUsageConsole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsagePlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return "/home Teleports you back to your home location";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		return true;
	}

}
