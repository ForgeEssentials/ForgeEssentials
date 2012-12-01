package com.ForgeEssentials.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandList extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "list";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer(MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getPlayerListAsString());	
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer(MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getPlayerListAsString());	
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}