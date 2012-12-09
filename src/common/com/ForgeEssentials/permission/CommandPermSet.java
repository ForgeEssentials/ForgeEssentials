package com.ForgeEssentials.permission;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandPermSet extends CommandFEPermBase
{

	@Override
	public String getCommandName()
	{
		// TODO Auto-generated method stub
		return "feperm set";
	}

	@Override
	public String getCommandSyntax(ICommandSender sender)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandInfo(ICommandSender sender)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}
}
