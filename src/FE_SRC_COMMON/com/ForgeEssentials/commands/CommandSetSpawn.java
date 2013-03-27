package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandSetSpawn extends FEcmdModuleCommands
{
	public static HashMap<String, WarpPoint>	spawns			= new HashMap<String, WarpPoint>();
	public static final String					spawnProp		= "ForgeEssentials.BasicCommands.spawnPoint";
	public static HashSet<Integer>				dimsWithProp	= new HashSet<Integer>();

	@Override
	public String getCommandName()
	{
		return "setspawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length <= 1)
		{
			error(sender);
			return;
		}

		Zone zone = ZoneManager.getGLOBAL();
		if (ZoneManager.doesZoneExist(args[0]))
		{
			zone = ZoneManager.getZone(args[0]);
		}
		else if (args[0].equalsIgnoreCase("here"))
		{
			zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
		}
		else
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[0]));
			return;
		}

		WorldPoint point = null;

		if (args.length == 2)
		{
			if (args[1].equalsIgnoreCase("here"))
			{
				point = new WorldPoint(sender);
			}
			else
			{
				error(sender);
				return;
			}
		}
		else if (args.length == 4)
		{
			int x = parseInt(sender, args[1]);
			int y = parseInt(sender, args[2]);
			int z = parseInt(sender, args[3]);
			point = new WorldPoint(sender.worldObj.provider.dimensionId, x, y, z);
		}
		else
		{
			error(sender);
			return;
		}

		setSpawnPoint(point, zone);
		OutputHandler.chatConfirmation(sender, Localization.format("command.setspawn.set", zone.getZoneName(), point.x, point.y, point.z));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length <= 1)
		{
			error(sender);
		}

		Zone zone = ZoneManager.getGLOBAL();
		if (ZoneManager.doesZoneExist(args[0]))
		{
			zone = ZoneManager.getZone(args[0]);
		}
		else
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[0]));
			return;
		}

		if (args.length == 5)
		{
			int dim = parseInt(sender, args[1]);
			int x = parseInt(sender, args[2]);
			int y = parseInt(sender, args[3]);
			int z = parseInt(sender, args[4]);
			WorldPoint point = new WorldPoint(dim, x, y, z);
			setSpawnPoint(point, zone);
			OutputHandler.chatConfirmation(sender, Localization.format("command.setspawn.set", zone.getZoneName(), point.x, point.y, point.z));
		}
		else
		{
			error(sender);
		}

	}

	public static void setSpawnPoint(WorldPoint p, Zone zone)
	{
		String val = p.dim + ";" + p.x + ";" + p.y + ";" + p.z;
		PermissionsAPI.setGroupPermissionProp(PermissionsAPI.getDEFAULT().name, spawnProp, val, zone.getZoneName());
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			ArrayList<String> names = new ArrayList<String>();
			for (Zone z : ZoneManager.getZoneList())
			{
				names.add(z.getZoneName());
			}
			return names;
		}
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}