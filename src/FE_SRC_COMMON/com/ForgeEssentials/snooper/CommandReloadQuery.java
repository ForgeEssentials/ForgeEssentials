package com.ForgeEssentials.snooper;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.IServer;

public class CommandReloadQuery extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName() 
	{
		return "queryreload";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) 
	{
		reload(sender);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) 
	{
		reload(sender);
	}

	public void reload(ICommandSender sender)
	{
		sender.sendChatToPlayer("Killing old one....");
		ModuleSnooper.theThread.closeAllSockets_do(true);
		ModuleSnooper.theThread.running = false;
		sender.sendChatToPlayer("Making new one....");
		ModuleSnooper.startQuery();
	}
	
	@Override
	public boolean canConsoleUseCommand() 
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) 
	{
		return true;
	}

	@Override
	public String getCommandPerm() 
	{
		return "ForgeEssentials.Snooper.commands." + getCommandName();
	}

}
