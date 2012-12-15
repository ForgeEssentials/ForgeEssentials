package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.query.PermQueryBlanketSpot;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public abstract class CommandFEPermBase extends ForgeEssentialsCommandBase
{
	@Override
	public final String getCommandName()
	{
		return "feperm " + getCommand();
	}

	public abstract String getCommand();

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("fep " + getCommand());
		return list;
	}

	@Override
	public String getCommandSyntax(ICommandSender sender)
	{
		return Localization.get("command.permissions." + getCommand() + ".syntax");
	}

	@Override
	public String getCommandInfo(ICommandSender sender)
	{
		return Localization.get("command.permissions." + getCommand() + ".info");
	}

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
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		return PermissionsAPI.checkPermAllowed(new PermQueryBlanketSpot(new WorldPoint(block.worldObj, block.xCoord, block.yCoord, block.zCoord), getCommandPerm()));
	}

}
