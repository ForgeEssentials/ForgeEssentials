package com.ForgeEssentials.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandRestart extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName() {
		return "restart";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("Stopping server...");
		MinecraftServer.serverRunning = false;
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
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		return false;
	}

}
