package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandBackup extends ForgeEssentialsCommandBase{

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		
		
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("Starting backup...");
		
		
	}

	@Override
	public String getSyntaxConsole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoConsole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player) {
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
