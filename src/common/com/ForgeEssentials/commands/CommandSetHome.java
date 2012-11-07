package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.Util;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.NBTTagCompound;

public class CommandSetHome extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName() {
		return "Set Home";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		OutputHandler.chatError(player, "Not currently implemented");
	}

	@Override
	public String getUsagePlayer(EntityPlayer player) {
		return "/sethome Sets your home location";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		return true;
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		
	}

	@Override
	public String getUsageConsole() {
		return null;
	}

}
