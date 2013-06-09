package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.AreaSelector.WarpPoint;
import com.ForgeEssentials.api.AreaSelector.WorldPoint;
import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetSpawn extends FEcmdModuleCommands
{
	public static HashMap<String, WarpPoint>	spawns			= new HashMap<String, WarpPoint>();
	public static final String					SPAWN_PROP		= "ForgeEssentials.BasicCommands.spawnPoint";
	public static final String					SPAWN_TYPE_PROP	= "ForgeEssentials.BasicCommands.spawnType";
	public static HashSet<Integer>				dimsWithProp	= new HashSet<Integer>();

	@Override
	public String getCommandName()
	{
		return "setspawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length <= 0)
		{
			error(sender);
			return;
		}

		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.1"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.2"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.3"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.4"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.5"));
				return;
			}
			else
				error(sender);
		}
		else if (args.length < 4 || args.length > 7)
		{
			error(sender);
			return;
		}
		
		// check point or type.
		String permProp = null;
		String prop = null;
		String output = "";
		if (args[0].equalsIgnoreCase("point"))
		{
			permProp = SPAWN_PROP; 
			
			int dim = 0, x = 0, y = 0, z = 0;
			if (args.length >= 6)
			{
				dim = sender.worldObj.provider.dimensionId;
				x = parseInt(sender, args[3], sender.posX);
				y = parseInt(sender, args[4], sender.posY);
				z = parseInt(sender, args[5], sender.posZ);
			}
			else if (args.length >= 4)
			{
				if (args[3].equalsIgnoreCase("here"))
				{
					WorldPoint p = new WorldPoint(sender);
					x = p.x;
					y = p.y;
					z = p.z;
					dim = p.dim;
				}
				else
					error(sender);
			}
			else
				error(sender);
			
			prop = dim + ";" + x + ";" + y + ";" + z;
			output = Localization.format("command.setspawn.setPoint", x, y, z);
		}
		else if (args[0].equalsIgnoreCase("type"))
		{
			permProp = SPAWN_TYPE_PROP; 
			
			if (args[3].equalsIgnoreCase("none"))
			{
				prop = "none";
			}
			else if (args[3].equalsIgnoreCase("none"))
			{
				prop = "bed";
			}
			else if (args[3].equalsIgnoreCase("none"))
			{
				prop = "point";
			}
			else
				error(sender);
			
			output = Localization.format("command.setspawn.setType", prop);
		}
		else
		{
			error(sender);
		}
		
		// calc zone.
		Zone zone = APIRegistry.zones.getGLOBAL();
		if (args.length == 5)
		{
			if (APIRegistry.zones.doesZoneExist(args[4]))
			{
				zone = APIRegistry.zones.getZone(args[4]);
			}
			else if (args[4].equalsIgnoreCase("here"))
			{
				zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[5]));
				return;
			}
		}
		else if(args.length == 7)
		{
			if (APIRegistry.zones.doesZoneExist(args[6]))
			{
				zone = APIRegistry.zones.getZone(args[6]);
			}
			else if (args[6].equalsIgnoreCase("here"))
			{
				zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[7]));
				return;
			}
		}

		if (args[1].equalsIgnoreCase("user"))
		{
			String name = args[1];
			if (args[2].equalsIgnoreCase("_ME_"))
			{
				name = sender.username;
			}
			else
			{
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
					OutputHandler.chatConfirmation(sender, args[0] + " will be used, but may be inaccurate.");
				}
				else
				{
					name = player.username;
				}
			}
			
			APIRegistry.perms.setPlayerPermissionProp(name, permProp, prop, zone.getZoneName());
			OutputHandler.chatConfirmation(sender, output);
		}
		else if (args[1].equalsIgnoreCase("group"))
		{
			if (APIRegistry.perms.getGroupForName(args[2]) == null)
			{
				OutputHandler.chatError(sender, args[2] + " does not exist as a group!");
				return;
			}
			
			APIRegistry.perms.setGroupPermissionProp(args[2], permProp, prop, zone.getZoneName());
			OutputHandler.chatConfirmation(sender, output);
		}
		else if (args[1].equalsIgnoreCase("zone"))
		{
			if (APIRegistry.zones.doesZoneExist(args[2]))
			{
				zone = APIRegistry.zones.getZone(args[2]);
			}
			else if (args[5].equalsIgnoreCase("here"))
			{
				zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				return;
			}
			
			APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, permProp, prop, zone.getZoneName());
			OutputHandler.chatConfirmation(sender, output);
		}
		else
			error(sender);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length <= 1)
		{
			error(sender);
			return;
		}

		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.1"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.2"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.3"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.4"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.setspawn.help.5"));
				return;
			}
			else
				error(sender);
		}
		else if (args.length < 4 || args.length > 7)
		{
			error(sender);
			return;
		}
		
		// check point or type.
		String permProp = null;
		String prop = null;
		String output = "";
		if (args[0].equalsIgnoreCase("point"))
		{
			permProp = SPAWN_PROP; 
			
			int dim = 0, x = 0, y = 0, z = 0;
			if (args.length >= 7)
			{
				dim = parseInt(sender, args[6]);
				x = parseInt(sender, args[3]);
				y = parseInt(sender, args[4]);
				z = parseInt(sender, args[5]);
			}
			
			prop = dim + ";" + x + ";" + y + ";" + z;
			output = Localization.format("command.setspawn.setPoint", x, y, z);
		}
		else if (args[0].equalsIgnoreCase("type"))
		{
			permProp = SPAWN_TYPE_PROP; 
			
			if (args[3].equalsIgnoreCase("none"))
			{
				prop = "none";
			}
			else if (args[3].equalsIgnoreCase("none"))
			{
				prop = "bed";
			}
			else if (args[3].equalsIgnoreCase("none"))
			{
				prop = "point";
			}
			else
				error(sender);
			
			output = Localization.format("command.setspawn.setType", prop);
		}
		else
		{
			error(sender);
		}
		
		// calc zone.
		Zone zone = APIRegistry.zones.getGLOBAL();
		if (args.length == 6)
		{
			if (APIRegistry.zones.doesZoneExist(args[5]))
			{
				zone = APIRegistry.zones.getZone(args[5]);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[5]));
				return;
			}
		}
		else if(args.length == 8)
		{
			if (APIRegistry.zones.doesZoneExist(args[7]))
			{
				zone = APIRegistry.zones.getZone(args[7]);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[7]));
				return;
			}
		}

		if (args[1].equalsIgnoreCase("user"))
		{
			String name = args[1];
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
					OutputHandler.chatConfirmation(sender, args[0] + " will be used, but may be inaccurate.");
				}
				else
				{
					name = player.username;
				}
			
				APIRegistry.perms.setPlayerPermissionProp(name, permProp, prop, zone.getZoneName());
			OutputHandler.chatConfirmation(sender, output);
		}
		else if (args[1].equalsIgnoreCase("group"))
		{
			if (APIRegistry.perms.getGroupForName(args[2]) == null)
			{
				OutputHandler.chatError(sender, args[2] + " does not exist as a group!");
				return;
			}
			
			APIRegistry.perms.setGroupPermissionProp(args[2], permProp, prop, zone.getZoneName());
			OutputHandler.chatConfirmation(sender, output);
		}
		else if (args[1].equalsIgnoreCase("zone"))
		{
			if (APIRegistry.zones.doesZoneExist(args[2]))
			{
				zone = APIRegistry.zones.getZone(args[2]);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				return;
			}
			
			APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, permProp, prop, zone.getZoneName());
			OutputHandler.chatConfirmation(sender, output);
		}
		else
			error(sender);
	}

	public static void setSpawnPoint(WorldPoint p, Zone zone)
	{
		String val = p.dim + ";" + p.x + ";" + p.y + ";" + p.z;
		APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, SPAWN_PROP, val, zone.getZoneName());
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
		ArrayList<String> completes = new ArrayList<String>();

		// type
		if (args.length == 1)
		{
			completes.add("type");
			completes.add("point");
			completes.add("help");
		}
		// target type
		else if (args.length == 2)
		{
			completes.add("player");
			completes.add("group");
			completes.add("zone");
		}
		// target
		else if (args.length == 3)
		{
			if (args[1].equalsIgnoreCase("player"))
			{
				completes.add("_ME_");
				for (String name : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames())
					completes.add(name);

			}
			else if (args[1].equalsIgnoreCase("group"))
			{
				List<Group> groups = APIRegistry.perms.getGroupsInZone(APIRegistry.zones.getGLOBAL().getZoneName());
				for (Group g : groups)
					completes.add(g.name);
			}
			else if (args[1].equalsIgnoreCase("zone"))
			{
				for (Zone z : APIRegistry.zones.getZoneList())
				{
					completes.add(z.getZoneName());
				}
			}
		}
		// value
		else if (args.length == 4)
		{
			if (args[0].equalsIgnoreCase("type"))
			{
				completes.add("none");
				completes.add("bed");
				completes.add("point");
			}
			else if (args[0].equalsIgnoreCase("point"))
			{
				completes.add("here");
			}
		}
		// zone after 1 arg of vals
		else if (args.length == 5)
		{
			if (args[0].equalsIgnoreCase("type") || (args[0].equalsIgnoreCase("point") && args[4].equalsIgnoreCase("here")))
			{
				for (Zone z : APIRegistry.zones.getZoneList())
				{
					completes.add(z.getZoneName());
				}
			}
		}
		// zone after coords
		else if (args.length == 7)
		{
			if (args[0].equalsIgnoreCase("point"))
			{
				for (Zone z : APIRegistry.zones.getZoneList())
				{
					completes.add(z.getZoneName());
				}
			}
		}

		return getListOfStringsMatchingLastWord(args, completes.toArray(new String[completes.size()]));

	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
