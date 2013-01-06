package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.permission.query.PermQueryBlanketSpot;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandFEPerm extends ForgeEssentialsCommandBase
{
	@Override
	public final String getCommandName()
	{
		return "feperm";
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("perm");
		list.add("fep");
		list.add("p");
		return list;
	}

	@Override
	public String getCommandSyntax(ICommandSender sender)
	{
		return Localization.get("command.permissions.feperm.syntax");
	}

	@Override
	public String getCommandInfo(ICommandSender sender)
	{
		return Localization.get("command.permissions.feperm.info");
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
		PermResult result = PermissionsAPI
				.checkPermResult(new PermQueryBlanketSpot(new WorldPoint(
						block.worldObj, block.xCoord, block.yCoord,
						block.zCoord), getCommandPerm(), true));
		return result.equals(PermResult.DENY) ? false : true;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		String first = args[0];
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++)
		{
			newArgs[i] = args[i + 1];
		}

		if (first.equalsIgnoreCase("user") || first.equalsIgnoreCase("player"))
		{
			CommandUser.processCommandPlayer(sender, newArgs);
		} else if (first.equalsIgnoreCase("export"))
		{
			CommandExport.processCommandPlayer(sender, newArgs);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		String first = args[0];
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++)
		{
			newArgs[i] = args[i + 1];
		}

		if (first.equalsIgnoreCase("user") || first.equalsIgnoreCase("player"))
		{
			CommandUser.processCommandConsole(sender, newArgs);
		} else if (first.equalsIgnoreCase("export"))
		{
			CommandExport.processCommandConsole(sender, newArgs);
		}
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.perm";
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		PermResult result = PermissionsAPI.checkPermResult(new PermQueryPlayer(
				player, getCommandPerm(), true));
		return result.equals(PermResult.DENY) ? false : true;
	}

}
