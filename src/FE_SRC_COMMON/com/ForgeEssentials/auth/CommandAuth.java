package com.ForgeEssentials.auth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandAuth extends ForgeEssentialsCommandBase
{

	public CommandAuth()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCommandName()
	{
		return "Auth";
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList();
		list.add("auth");
		list.add("AUTH");
		return list;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		ArrayList<String> list = new ArrayList();
		if (sender instanceof EntityPlayer)
		{
			
		}
		else
		{
			
		}
		return null;
	}

	@Override
	public String getCommandPerm()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
