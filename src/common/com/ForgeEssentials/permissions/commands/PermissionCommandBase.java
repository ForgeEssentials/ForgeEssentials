package com.ForgeEssentials.permissions.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class PermissionCommandBase extends ForgeEssentialsCommandBase{

	/**
	 * This CommandBase is for commands applying to the permissions system.
	 */
	
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		// TODO Auto-generated method stub
		
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
		return null;
	}

	@Override
	public boolean canConsoleUseCommand() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

}
