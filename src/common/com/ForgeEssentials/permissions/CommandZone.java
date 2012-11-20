package com.ForgeEssentials.permissions;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandZone extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		// TODO Auto-generated method stub
		return "zone";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		// do something.
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// no defining zones from the console.
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		// TODO no command for this from the console.
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Permissions.Zone";
	}

}
