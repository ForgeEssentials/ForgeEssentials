package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandButcher extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "butcher";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{

	}

	@Override
	public String getUsageConsole()
	{
		return "/butcher [radius] <x> <y> <z>";
	}

	@Override
	public String getUsagePlayer(EntityPlayer player)
	{
		return "/butcher [radius] [<x> <y> <z>]";
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

}
