package com.ForgeEssentials.permission;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandFEPerm extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "feperm";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public String getSyntaxConsole()
	{
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoConsole()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		// TODO Auto-generated method stub
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
		return null;
	}

}
