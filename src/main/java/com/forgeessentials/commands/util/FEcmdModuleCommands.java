package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public abstract class FEcmdModuleCommands extends ForgeEssentialsCommandBase
{
	public boolean				enableCmdBlock	= true;
	public boolean				enableConsole	= true;
	public boolean				enablePlayer	= true;

	public ArrayList<String>	aliasList		= new ArrayList<String>();

	// ---------------------------
	// config interaction
	// ---------------------------

	/**
	 * Override if you want config interaction.
	 * @param config
	 * @param category
	 */
	public void doConfig(Configuration config, String category){}

	@Override
	public List<String> getCommandAliases()
	{
		return aliasList;
	}

	public String[] getDefaultAliases()
	{
		return new String[] {};
	}

	public boolean usefullCmdBlock()
	{
		return canConsoleUseCommand();
	}

	public boolean usefullPlayer()
	{
		return true;
	}

	public abstract RegGroup getReggroup();

	/**
	 * You don't need to register the commandpermission.
	 * @param event
	 */
	public void registerExtraPermissions(IPermRegisterEvent event){}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			if (!enablePlayer)
				return false;
			else
				return canPlayerUseCommand((EntityPlayer) sender);
		}
		else if (sender instanceof TileEntityCommandBlock)
		{
			if (!enableCmdBlock)
				return false;
			else
				return canCommandBlockUseCommand((TileEntityCommandBlock) sender);
		}
		else
		{
			if (!enableConsole)
				return false;
			else
				return canConsoleUseCommand();
		}
	}
	
	public abstract String getCommandName();
}
