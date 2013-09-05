package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandFEPermUser
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0) // display syntax & possible options for this level
		{
			OutputHandler.chatConfirmation(sender, "Possible usage:");
			OutputHandler.chatConfirmation(sender, "/p user <player> : Display user statistics");
			OutputHandler.chatConfirmation(sender, "/p user <player> supers : Player's superperms");
			OutputHandler.chatConfirmation(sender, "/p user <player> group : Player's group settings");
			OutputHandler.chatConfirmation(sender, "/p user <player> allow|true|deny|false|clear : Player's individual permissions");
			return;
		}

		String playerName = args[0];
		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (playerName.equalsIgnoreCase("_ME_"))
		{
			player = (EntityPlayerMP) sender;
			playerName = sender.username;
		}
		else if (player == null)
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			OutputHandler.chatConfirmation(sender, args[0] + " will be used, but may be inaccurate.");
		}
		else
		{
			playerName = player.username;
		}

		if (args.length == 1) // display user-specific settings & there values for this player
		{
			ArrayList<Group> groups = APIRegistry.perms.getApplicableGroups(playerName, false, APIRegistry.zones.getGLOBAL().getZoneName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.permissions.user.info.groups", playerName));
			for (Group g : groups)
			{
				OutputHandler.chatConfirmation(sender, " - " + g.name + " -- " + g.zoneName);
			}
			return;
		}
		else if (args[1].equalsIgnoreCase("supers")) // super perms management
		{
			if (args.length == 2) // display user super perms
			{
				Zone zone = APIRegistry.zones.getSUPER();
				ArrayList<String> list = APIRegistry.perms.getPlayerPermissions(playerName, zone.getZoneName());
				boolean error = false;
				for (Object lineObj : list)
				{
					String line = (String) lineObj;
					if (line.equalsIgnoreCase("error"))
					{
						error = true;
						continue;
					}
					if (error)
					{
						OutputHandler.chatError(sender, line);
					}
					else
					{
						OutputHandler.chatConfirmation(sender, line);
					}
				}
				return;
			}
			else if (args.length >= 3) // changing super perms
			{
				Zone zone = APIRegistry.zones.getSUPER();
				String perm = null;
				String value = null;

				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("allow"))
				{
					APIRegistry.perms.setPlayerPermissionProp(playerName, perm, value, zone.getZoneName());
					OutputHandler.chatConfirmation(sender, playerName + " has been allowed " + perm + " prop with value of " + value);
					return;
				}
				// remove super perm setting
				else if (args[2].equalsIgnoreCase("clear") || args[2].equalsIgnoreCase("remove"))
				{
					APIRegistry.perms.clearPlayerPermission(playerName, args[3], zone.getZoneName());
					OutputHandler.chatConfirmation(sender, playerName + "'s access to " + args[2] + " cleared");
					return;
				}
				// deny super perm
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("deny"))
				{
					APIRegistry.perms.setPlayerPermission(playerName, args[3], false, zone.getZoneName());
					OutputHandler.chatConfirmation(sender, playerName + " has been denied " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					String result = APIRegistry.perms.getPermissionForPlayer(player.username, zone.getZoneName(), args[2]);
					if (result == null)
					{
						OutputHandler.chatError(sender, "Error processing statement");
					}
					else if (result.equals("Zone or target invalid"))
					{
						OutputHandler.chatError(sender, "Zone or group does not exist!");
					}
					else
					{
						OutputHandler.chatConfirmation(sender, args[2] + " is " + result + " for " + player.username);
					}
					return;
				}
			}
		}
		else if (args[1].equalsIgnoreCase("group")) // group management
		{
			if (args.length == 2)
			{
				OutputHandler.chatConfirmation(sender, "/p user <player> group add : Adds them to specified group.");
				OutputHandler.chatConfirmation(sender, "/p user <player> group remove : Removes player from specified group.");
				OutputHandler.chatConfirmation(sender, "/p user <player> group set : Removes player from all groups and adds them to specified group.");
			}

			String zoneName = APIRegistry.zones.getGLOBAL().getZoneName();
			if (args.length == 5) // zone is set
			{
				if (APIRegistry.zones.getZone(args[4]) != null)
				{
					zoneName = args[4];
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}

			if (args.length >= 3)
			{
				if (args[2].equalsIgnoreCase("add")) // add player to group
				{
					if (args.length > 3)
					{
						String result = APIRegistry.perms.addPlayerToGroup(args[3], playerName, zoneName);
						if (result != null)
						{
							OutputHandler.chatError(sender, result);
						}
						else
						{
							OutputHandler.chatConfirmation(sender, playerName + " added to group " + args[3]+" in zone "+zoneName);
						}
					}
					else
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
					}
					return;
				}
				else if (args[2].equalsIgnoreCase("remove")) // remove player from
																// group
				{
					if (args.length > 3)
					{
						String result = APIRegistry.perms.clearPlayerGroup(args[3], playerName, zoneName);
						if (result != null)
						{
							OutputHandler.chatError(sender, result);
						}
						else
						{
							OutputHandler.chatConfirmation(sender, playerName + " removed from group " + args[3]);
						}
					}
					return;
				}
				else if (args[2].equalsIgnoreCase("set")) // set player's group
				{
					if (args.length > 3)
					{
						String result = APIRegistry.perms.setPlayerGroup(args[3], playerName, zoneName);
						if (result != null)
						{
							OutputHandler.chatError(sender, result);
						}
						else
						{
							OutputHandler.chatConfirmation(sender, playerName + "'s group set to " + args[3]);
						}
					}
					return;
				}
			}
		}
		else if (args.length >= 2) // player management
		{

			if (args[1].equalsIgnoreCase("prefix")) // prefix
			{
				if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
				{
					PlayerInfo pi = PlayerInfo.getPlayerInfo(playerName);
					if (pi.prefix.trim().length() == 0)
					{
						OutputHandler.chatConfirmation(sender, playerName + " does not have a prefix.");
					}
					else
					{
						OutputHandler.chatConfirmation(sender, playerName + "'s prefix is &f" + pi.prefix);
					}
					return;
				}
				else
				// args[2] must contian "set"
				{
					PlayerInfo pi = PlayerInfo.getPlayerInfo(playerName);
					if (args.length == 3)
					{
						pi.prefix = " ";
						OutputHandler.chatConfirmation(sender, playerName + "'s prefix cleared");
					}
					else
					{
						pi.prefix = args[3];
						OutputHandler.chatConfirmation(sender, playerName + "'s prefix set to &f" + pi.prefix);
					}
					return;
				}
			}
			else if (args[1].equalsIgnoreCase("suffix")) // suffix
			{
				if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
				{
					PlayerInfo pi = PlayerInfo.getPlayerInfo(playerName);
					if (pi.suffix.trim().length() == 0)
					{
						OutputHandler.chatConfirmation(sender, playerName + " does not have a suffix.");
					}
					else
					{
						OutputHandler.chatConfirmation(sender, playerName + "'s suffix is &f" + pi.suffix);
					}
					return;
				}
				else
				// args[2] must contian "set"
				{
					PlayerInfo pi = PlayerInfo.getPlayerInfo(playerName);
					if (args.length == 3)
					{
						pi.suffix = " ";
						OutputHandler.chatConfirmation(sender, playerName + "'s suffix cleared");
					}
					else
					{
						pi.suffix = args[3];
						OutputHandler.chatConfirmation(sender, playerName + "'s suffix set to &f" + pi.suffix);
					}
					return;
				}
			}

			// player permisisons
			String zoneName = APIRegistry.zones.getGLOBAL().getZoneName();
			if (args.length == 4) // zone is set
			{
				if (APIRegistry.zones.getZone(args[3]) != null)
				{
					zoneName = args[3];
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[3]));
					return;
				}
			}

			// allow playerPerms
			if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow"))
			{
				String result = APIRegistry.perms.setPlayerPermission(playerName, args[2], true, zoneName);
				if (result != null)
				{
					OutputHandler.chatError(sender, result);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, playerName + "  allowed access to " + args[2] + ".");
				}
				return;
			}
			// clear player perms
			else if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))
			{
				String result = APIRegistry.perms.clearPlayerPermission(playerName, args[2], zoneName);
				if (result != null)
				{
					OutputHandler.chatError(sender, result);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, playerName + "'s  access to " + args[2] + "cleared.");
				}
				return;
			}
			// deny player perms
			else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny"))
			{
				String result = APIRegistry.perms.setPlayerPermission(playerName, args[2], false, zoneName);
				if (result != null)
				{
					OutputHandler.chatError(sender, result);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, playerName + " denied access to " + args[2] + ".");
				}
				return;
			}
			if (args[1].equalsIgnoreCase("perms"))
			{
				if (args.length == 3)
				{
					if (APIRegistry.zones.getZone(args[2]) != null)
					{
						zoneName = args[2];
					}
					else if (args[2].equalsIgnoreCase("here"))
					{
						zoneName = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender)).getZoneName();
					}
					else
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
						return;
					}
				}
				ArrayList<String> list = APIRegistry.perms.getPlayerPermissions(playerName, zoneName);
				Collections.sort(list);
				ArrayList<String> messageAllowed = new ArrayList<String>();
				ArrayList<String> messageDenied = new ArrayList<String>();
				for (String permission : list)
				{
					if (permission.contains("has no individual permissions."))
					{
						OutputHandler.chatConfirmation(sender, permission);
						return;
					}
					if (permission.contains("true"))
					{
						messageAllowed.add(" " + FEChatFormatCodes.DARKGREEN + permission.substring(0, permission.indexOf(":")));
					}
					else
					{
						messageDenied.add(" " + FEChatFormatCodes.DARKRED + permission.substring(0, permission.indexOf(":")));
					}
				}
				OutputHandler.chatConfirmation(sender, playerName + ": Current permissions in zone " + zoneName + ":");
				OutputHandler.chatConfirmation(sender, " (" + FEChatFormatCodes.DARKGREEN + "ALLOWED" + FEChatFormatCodes.DARKRED + " DENIED" + FEChatFormatCodes.GREEN + ")");
				for (String permission : messageAllowed)
				{
					OutputHandler.chatConfirmation(sender, permission);
				}
				for (String permission : messageDenied)
				{
					OutputHandler.chatConfirmation(sender, permission);
				}
				return;
			}
		}

		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0) // display syntax & possible options for this
								// level
		{
			ChatUtils.sendMessage(sender, "Possible usage:");
			ChatUtils.sendMessage(sender, "/p user <player> : Display user statistics");
			ChatUtils.sendMessage(sender, "/p user <player> supers : Player's superperms");
			ChatUtils.sendMessage(sender, "/p user <player> group : Player's group settings");
			ChatUtils.sendMessage(sender, "/p user <player> allow|true|deny|false|clear : Player's individual permissions");
			return;
		}

		String playerName = args[0];
		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player == null)
		{
			ChatUtils.sendMessage(sender, "ERROR: " + Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			ChatUtils.sendMessage(sender, args[0] + " will be used, but may be inaccurate.");
		}
		else
		{
			playerName = player.username;
		}

		if (args.length == 1) // display user-specific settings & there values
		// for this player
		{
			ArrayList<Group> groups = APIRegistry.perms.getApplicableGroups(playerName, false, APIRegistry.zones.getGLOBAL().getZoneName());
			ChatUtils.sendMessage(sender, Localization.format("command.permissions.user.info.groups", playerName));
			for (Group g : groups)
			{
				ChatUtils.sendMessage(sender, " - " + g.name + " -- " + g.zoneName);
			}
			return;
		}
		else if (args[1].equalsIgnoreCase("supers")) // super perms management
		{
			if (args.length == 2) // display user super perms
			{
				Zone zone = APIRegistry.zones.getSUPER();
				ArrayList<String> list = APIRegistry.perms.getPlayerPermissions(playerName, zone.getZoneName());
				boolean error = false;
				for (Object lineObj : list)
				{
					String line = (String) lineObj;
					if (line.equalsIgnoreCase("error"))
					{
						error = true;
						continue;
					}
					if (error)
					{
						ChatUtils.sendMessage(sender, "ERROR: " + line);
					}
					else
					{
						ChatUtils.sendMessage(sender, line);
					}
				}
				return;
			}
			else if (args.length >= 3) // changing super perms
			{
				Zone zone = APIRegistry.zones.getSUPER();

				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("allow"))
				{
					APIRegistry.perms.setPlayerPermission(playerName, args[3], true, zone.getZoneName());
					ChatUtils.sendMessage(sender, playerName + " has been allowed " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("clear") || args[2].equalsIgnoreCase("remove")) // remove super perm settings
				{
					APIRegistry.perms.clearPlayerPermission(playerName, args[3], zone.getZoneName());
					ChatUtils.sendMessage(sender, playerName + "'s access to " + args[2] + " cleared");
					return;
				}
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("deny")) // deny super perm
				{
					APIRegistry.perms.setPlayerPermission(playerName, args[3], false, zone.getZoneName());
					ChatUtils.sendMessage(sender, playerName + " has been denied " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					String result = APIRegistry.perms.getPermissionForPlayer(player.username, zone.getZoneName(), args[2]);
					if (result == null)
					{
						OutputHandler.chatError(sender, "Error processing statement");
					}
					else if (result.equals("Zone or target invalid"))
					{
						OutputHandler.chatError(sender, "Zone or group does not exist!");
					}
					else
					{
						OutputHandler.chatConfirmation(sender, args[2] + " is " + result + " for " + player.username);
					}
					return;
				}

			}
		}
		else if (args[1].equalsIgnoreCase("group")) // group management
		{
			String zoneName = APIRegistry.zones.getGLOBAL().getZoneName();
			if (args.length == 5) // zone is set
			{
				if (APIRegistry.zones.getZone(args[4]) != null)
				{
					zoneName = args[4];
				}
				else
				{
					ChatUtils.sendMessage(sender, "ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}
			if (args[2].equalsIgnoreCase("add")) // add player to group
			{
				if (args.length > 3)
				{
					String result = APIRegistry.perms.addPlayerToGroup(args[3], playerName, zoneName);
					if (result != null)
					{
						ChatUtils.sendMessage(sender, "ERROR: " + result);
					}
					else
					{
						ChatUtils.sendMessage(sender, playerName + " added to group " + args[3]);
					}
				}
				else
				{
					ChatUtils.sendMessage(sender, "ERROR: " + Localization.get(Localization.ERROR_BADSYNTAX));
				}
				return;
			}
			else if (args[2].equalsIgnoreCase("remove")) // remove player from
			// group
			{
				if (args.length > 3)
				{
					String result = APIRegistry.perms.clearPlayerGroup(args[3], playerName, zoneName);
					if (result != null)
					{
						ChatUtils.sendMessage(sender, "ERROR: " + result);
					}
					else
					{
						ChatUtils.sendMessage(sender, playerName + " removed from group " + args[3]);
					}
				}
				return;
			}
			else if (args[2].equalsIgnoreCase("set")) // set player's group
			{
				if (args.length > 3)
				{
					String result = APIRegistry.perms.setPlayerGroup(args[3], playerName, zoneName);
					if (result != null)
					{
						ChatUtils.sendMessage(sender, "ERROR: " + result);
					}
					else
					{
						ChatUtils.sendMessage(sender, playerName + "'s group set to " + args[3]);
					}
				}
				return;
			}
		}
		else if (args.length >= 3) // player management
		{
			String zoneName = APIRegistry.zones.getGLOBAL().getZoneName();
			if (args.length == 4) // zone is set
			{
				if (APIRegistry.zones.getZone(args[3]) != null)
				{
					zoneName = args[3];
				}
				else
				{
					ChatUtils.sendMessage(sender, "ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}
			if (args[1].equalsIgnoreCase("prefix")) // prefix
			{
				if (args.length == 3 && args[2].equalsIgnoreCase("set"))
				{
					args[2] = args[3];
				}

				if (args.length >= 2)
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = args[2];
					ChatUtils.sendMessage(sender, playerName + "'s prefix set to &f" + args[2]);
				}
				else
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = "";
					ChatUtils.sendMessage(sender, playerName + "'s removed");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("suffix")) // suffix
			{
				if (args.length == 3 && args[2].equalsIgnoreCase("set"))
				{
					args[2] = args[3];
				}
				if (args.length >= 2)
				{
					PlayerInfo.getPlayerInfo(playerName).suffix = args[2];
					ChatUtils.sendMessage(sender, playerName + "'s suffix set to &f" + args[2]);
				}
				else
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = "";
					ChatUtils.sendMessage(sender, playerName + "'s removed");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow")) // allow
																							// player
																							// perm
			{
				String result = APIRegistry.perms.setPlayerPermission(playerName, args[2], true, zoneName);
				if (result != null)
				{
					ChatUtils.sendMessage(sender, "ERROR: " + result);
				}
				else
				{
					ChatUtils.sendMessage(sender, playerName + "  allowed access to " + args[2] + ".");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove")) // remove
																								// perm
																								// settings
			{
				String result = APIRegistry.perms.clearPlayerPermission(playerName, args[2], zoneName);
				if (result != null)
				{
					ChatUtils.sendMessage(sender, "ERROR: " + result);
				}
				else
				{
					ChatUtils.sendMessage(sender, playerName + " denied access to " + args[2] + ".");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny")) // deny
																							// player
																							// perm
			{
				String result = APIRegistry.perms.setPlayerPermission(playerName, args[2], false, zoneName);
				if (result != null)
				{
					ChatUtils.sendMessage(sender, "ERROR: " + result);
				}
				else
				{
					ChatUtils.sendMessage(sender, playerName + "'s  access to " + args[2] + "cleared.");
				}
				return;
			}
		}
		else if (args[1].equalsIgnoreCase("perms"))
		{
			String zoneName = APIRegistry.zones.getGLOBAL().getZoneName();
			if (args.length == 3)
			{
				if (APIRegistry.zones.getZone(args[2]) != null)
				{
					zoneName = args[2];
				}
				else if (args[2].equalsIgnoreCase("here"))
				{
					ChatUtils.sendMessage(sender, "ERROR: You are not allowed to use the here keyword on console.");
					return;
				}
				else
				{
					ChatUtils.sendMessage(sender, "ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
					return;
				}
			}
			ArrayList<String> list = APIRegistry.perms.getPlayerPermissions(playerName, zoneName);
			Collections.sort(list);
			ArrayList<String> messageAllowed = new ArrayList<String>();
			ArrayList<String> messageDenied = new ArrayList<String>();
			for (String perm : list)
			{
				if (perm.contains("has no individual permissions."))
				{
					ChatUtils.sendMessage(sender, perm);
					return;
				}
				if (perm.contains("ALLOW"))
				{
					messageAllowed.add(" " + perm);
				}
				else
				{
					messageDenied.add(" " + perm);
				}
			}
			ChatUtils.sendMessage(sender, playerName + ": Current permissions in zone " + zoneName + ":");
			for (String perm : messageAllowed)
			{
				ChatUtils.sendMessage(sender, perm);
			}
			for (String perm : messageDenied)
			{
				ChatUtils.sendMessage(sender, perm);
			}
			return;
		}
		else
		// display user-specific settings & there values for this player
		{
			ArrayList<Group> groups = APIRegistry.perms.getApplicableGroups(playerName, false, args[1]);
			ChatUtils.sendMessage(sender, Localization.format("command.permissions.user.info.groups", playerName));
			for (Group g : groups)
			{
				ChatUtils.sendMessage(sender, " - " + g.name + " -- " + g.zoneName);
			}
			return;
		}

		ChatUtils.sendMessage(sender, "ERROR: " + Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

}
