package com.ForgeEssentials.permission;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

public abstract class CommandFEPermBase extends ForgeEssentialsCommandBase
{
	@Override
	public abstract String getCommandSyntax(ICommandSender sender);
	
	@Override
	public abstract String getCommandInfo(ICommandSender sender);
	
	@Override
	public abstract void processCommand(ICommandSender var1, String[] var2);
	
	// ------------------------------------------
	// -------STUFF-THAT-DOESNT-MATTER-----------
	// ------------------------------------------

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return null;
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}
	
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

}
