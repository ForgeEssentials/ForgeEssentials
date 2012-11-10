package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.Version;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandFEUpdate extends ForgeEssentialsCommandBase{

	@Override
	public String getCommandName() {
		return "feupdate";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("Checking for updates...");
		Version.checkVersion();
		
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
		return "Check for an update to ForgeEssentials";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canConsoleUseCommand() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

}
