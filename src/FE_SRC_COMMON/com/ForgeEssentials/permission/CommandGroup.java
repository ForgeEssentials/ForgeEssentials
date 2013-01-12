package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandGroup
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer("TEST! Group parsing");

		if (args.length == 0) // display syntax & possible options for this level
		{
			//Make list
			//OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
		/*
		 * Create / remove part
		 *\ /p group create <group> [prefix] [suffix] [parent] [priority] [zone]
		 */
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("make"))
		{
			Zone zone = ZoneManager.getWorldZone(sender.worldObj);
			if(args.length > 3)
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
			PermissionsAPI.createGroupInZone(args[1], zone.getZoneName(), "", "", null, 0);
			sender.sendChatToPlayer("Group " + args[1] + " made in zone " + zone.getZoneName());
			return;
		}
		if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove"))
		{
			if(PermissionsAPI.getGroupForName(args[1]) == null)
			{
				//Group does not exits.
				return;
			}
			Zone zone = ZoneManager.getWorldZone(sender.worldObj);
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
		
		
		Group group = PermissionsAPI.getGroupForName(args[0]);
		if (group == null)
		{
			// No such group!
			return;
		}
		if (args.length == 1) // display user-specific settings & there values for this player
		{
			//Give group info + syntax to change.
			return;
		}
		/*
		 * Settings
		 */
		Zone zone = ZoneManager.getWorldZone(sender.worldObj);
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
					group.prefix = " "	;
				else
					group.prefix = args[3];
				PermissionsAPI.updateGroup(group);
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
					group.suffix = " "	;
				else
					group.suffix = args[3];
				PermissionsAPI.updateGroup(group);
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
				PermissionsAPI.updateGroup(group);
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
				PermissionsAPI.updateGroup(group);
				return;
			}
		}
		/*
		 * Permissions part
		 */
		zone = ZoneManager.getWorldZone(sender.worldObj);
		if(args.length == 4)
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
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
			}
		}
		if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow"))
		{
			PermissionsAPI.setGroupPermission(group.name, args[2], true, zone.getZoneName());
			return;
		}
		if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny"))
		{
			PermissionsAPI.setGroupPermission(group.name, args[2], false, zone.getZoneName());
			return;
		}
		if(args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))
		{
			PermissionsAPI.clearGroupPermission(group.name, args[2], zone.getZoneName());
			return;
		}
		if(args[1].equalsIgnoreCase("get"))
		{
			//arg 2 = perm.
			return;
		}
		
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Copy paste :p
	}

}
