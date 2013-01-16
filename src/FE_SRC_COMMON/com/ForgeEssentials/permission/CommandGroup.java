package com.ForgeEssentials.permission;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandGroup
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0) // display syntax & possible options for this level
		{
			//Make list
			OutputHandler.chatConfirmation(sender, "Possible usage:");
			OutputHandler.chatConfirmation(sender, "/p group create|delete");
			OutputHandler.chatConfirmation(sender, "/p group <groupName>");
			OutputHandler.chatConfirmation(sender, "/p group list ");
			//OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
		/*
		 * Create / remove part
		 *\ /p group create <groupName>
		 */
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("make"))
		{
			if(args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "Usage: /p group create <groupname>");
				return;
			}
			Zone zone = ZoneManager.GLOBAL;
			String prefix = "";
			String suffix = "";
			String parent = null;
			int priority = 0;
			if(args.length > 2)
			{
				
				if(ZoneManager.doesZoneExist(args[3]))
				{
					zone = ZoneManager.getZone(args[2]);
				}
				else if(args[3].equalsIgnoreCase("here"))
				{
					zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
			}
			PermissionsAPI.createGroupInZone(args[1], zone.getZoneName(), "", "", null, 0);
			sender.sendChatToPlayer("Group " + args[1] + " made in zone " + zone.getZoneName());
			return;
		}
		if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove"))
		{
			if(args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "Usage: /p group delete <groupname>");
				return;
			}
			if(PermissionsAPI.getGroupForName(args[1]) == null)
			{
				OutputHandler.chatError(sender, args[0] + " does not exist as a group!");
				return;
			}
			Zone zone = ZoneManager.GLOBAL;
			if(args.length == 3)
			{
				if(ZoneManager.doesZoneExist(args[2]))
				{
					zone = ZoneManager.getZone(args[2]);
				}
				else if(args[3].equalsIgnoreCase("here"))
				{
					zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
			}
			//Remove zone
			PermissionsAPI.deleteGroupInZone(args[1], zone.getZoneName());
			OutputHandler.chatConfirmation(sender, "Group " + args[1] + " removed in zone " + zone.getZoneName());
			return;
		}
		if(args[0].equalsIgnoreCase("list"))
		{
			// list the current groups: by zone?  in priority order?
			Zone zone = ZoneManager.GLOBAL;
			if(args.length == 2)
			{
				if(ZoneManager.doesZoneExist(args[1]))
				{
					zone = ZoneManager.getZone(args[1]);
				}
				else if(args[1].equalsIgnoreCase("here"))
				{
					zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
			}
			ArrayList list = PermissionsAPI.getGroupsInZone(zone.getZoneName());
			String groups = "";
			int i = 0;
			for(Object groupObj : list)
			{
				groups += ((Group)groupObj).name;
				i++;
				if(i != list.size())
					groups += ", ";
			}
			OutputHandler.chatConfirmation(sender, "Groups available in zone " + zone.getZoneName() + ":");
			OutputHandler.chatConfirmation(sender, groups);
			return;
		}
		
		
		Group group = PermissionsAPI.getGroupForName(args[0]);
		if (group == null)
		{
			OutputHandler.chatError(sender, args[0] + " does not exist as a group!");
			return;
		}
		if (args.length == 1) // display group-specific settings and their values for this group
		{
			OutputHandler.chatConfirmation(sender, "Current settings for " + group.name + ": prefix=" + group.prefix + ", suffix=" + group.suffix + ", parent=" + group.parent + ", priority=" + group.priority);
			OutputHandler.chatConfirmation(sender, "To change any of these, type /p group <groupname> prefix|suffix|parent|priority set <value>");
			return;
		}
		/*
		 * Settings
		 */
		Zone zone = ZoneManager.GLOBAL;
		if(args.length == 3 && (args[1].equalsIgnoreCase("prefix") || args[1].equalsIgnoreCase("suffix")
				|| args[1].equalsIgnoreCase("parent") || args[1].equalsIgnoreCase("priority")))
		{
			if(args.length == 3)
			{
				if(ZoneManager.doesZoneExist(args[2]))
				{
					zone = ZoneManager.getZone(args[2]);
				}
				else if(args[2].equalsIgnoreCase("here"))
				{
					zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
			}
		}
		else if(args.length == 5)
		{
			if(ZoneManager.doesZoneExist(args[4]))
			{
				zone = ZoneManager.getZone(args[4]);
			}
			else if(args[4].equalsIgnoreCase("here"))
			{
				zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
			}
		}
		if (args[1].equalsIgnoreCase("prefix"))
		{
			if(args.length == 2 || !args[2].equalsIgnoreCase("set"))
			{
				OutputHandler.chatConfirmation(sender, group.name + "'s prefix is " + group.prefix);
				return;
			}
			else // args[2] must contian "set"
			{
				if(args.length == 3)
					group.prefix = " ";
				else
					group.prefix = args[3];
				boolean result = PermissionsAPI.updateGroup(group);
				if(result)
				{
					OutputHandler.chatConfirmation(sender, group.name + "'s prefix set to " + group.prefix);
				}
				else
				{
					OutputHandler.chatError(sender, "Error processing group prefix update.");
				}
				return;
			}
		}
		if (args[1].equalsIgnoreCase("suffix"))
		{
			if(args.length == 2 || !args[2].equalsIgnoreCase("set"))
			{
				OutputHandler.chatConfirmation(sender, group.name + "'s suffix is " + group.suffix);
				return;
			}
			else // args[2] must contian "set"
			{
				if(args.length == 3)
					group.suffix = " ";
				else
					group.suffix = args[3];
				boolean result = PermissionsAPI.updateGroup(group);
				if(result)
				{
					OutputHandler.chatConfirmation(sender, group.name + "'s suffix set to " + group.suffix);
				}
				else
				{
					OutputHandler.chatError(sender, "Error processing group suffix update.");
				}
				return;
			}
		}
		/*
		 * Parent part
		 */
		if (args[1].equalsIgnoreCase("parent"))
		{
			if(args.length == 2 || !args[2].equalsIgnoreCase("set"))
			{
				OutputHandler.chatConfirmation(sender, group.name + "'s parent is " + group.parent);
				return;
			}
			else // args[2] must contian "set"
			{
				if(args.length == 3)
					group.parent = null;
				else
					group.parent = args[3];
				boolean result = PermissionsAPI.updateGroup(group);
				if(result)
				{
					OutputHandler.chatConfirmation(sender, group.name + "'s parent set to " + group.parent);
				}
				else
				{
					OutputHandler.chatError(sender, "Error processing group parent update.");
				}
				return;
			}
		}
		/*
		 * Priority part
		 */
		if (args[1].equalsIgnoreCase("priority"))
		{
			if(args.length == 2 || !args[2].equalsIgnoreCase("set"))
			{
				OutputHandler.chatConfirmation(sender, group.name + "'s priority is " + group.priority);
				return;
			}
			else // args[2] must contian "set"
			{
				if(args.length == 3)
					group.priority = 0;
				else
					try
					{
						group.priority = Integer.parseInt(args[3]);
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, args[3] + "");
					}
				boolean result = PermissionsAPI.updateGroup(group);
				if(result)
				{
					OutputHandler.chatConfirmation(sender, group.name + "'s priority set to " + group.priority);
				}
				else
				{
					OutputHandler.chatError(sender, "Error processing group priority update.");
				}
				return;
			}
		}
		/*
		 * Permissions part
		 */
		zone = ZoneManager.GLOBAL;
		if(args.length == 4)
		{
			if(ZoneManager.doesZoneExist(args[3]))
			{
				zone = ZoneManager.getZone(args[3]);
			}
			else if(args[3].equalsIgnoreCase("here"))
			{
				zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
			}
		}
		if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow"))
		{
			String result = PermissionsAPI.setGroupPermission(group.name, args[2], true, zone.getZoneName());
			if(result == null)
			{
				OutputHandler.chatConfirmation(sender, group.name + " in zone " + zone.getZoneName() + " allowed access to " + args[2]);
			}
			else
			{
				OutputHandler.chatError(sender, result);
			}
			return;
		}
		if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny"))
		{
			String result = PermissionsAPI.setGroupPermission(group.name, args[2], false, zone.getZoneName());
			if(result == null)
			{
				OutputHandler.chatConfirmation(sender, group.name + " in zone " + zone.getZoneName() + " denied access to " + args[2]);
			}
			else
			{
				OutputHandler.chatError(sender, result);
			}
			return;
		}
		if(args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))
		{
			String result = PermissionsAPI.clearGroupPermission(group.name, args[2], zone.getZoneName());
			if(result == null)
			{
				OutputHandler.chatConfirmation(sender, args[2] + " has been removed from " + group.name + " in zone " + zone.getZoneName());
			}
			else
			{
				OutputHandler.chatError(sender, result);
			}
			return;
		}
		if(args[1].equalsIgnoreCase("get"))
		{
			String result = PermissionsAPI.getPermissionForGroup(group.name, zone.getZoneName(), args[2]);
			if(result == null)
			{
				OutputHandler.chatError(sender, "Error processing statement");
			}
			else if(result.equals("Zone or target invalid"))
			{
				OutputHandler.chatError(sender, "Zone or group does not exist!");
			}
			else
			{
				OutputHandler.chatConfirmation(sender, args[2] + " is " + result + " for " + group.name);
			}
			return;
		}
		
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Copy paste :p
	}

}
