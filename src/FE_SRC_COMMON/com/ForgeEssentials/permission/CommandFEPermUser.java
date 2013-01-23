package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

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
		
		boolean playerExists = true;
		String playerName = args[0];
		EntityPlayerMP player = FunctionHelper.getPlayerFromUsername(args[0]);
		if (playerName.equalsIgnoreCase("_ME_"))
		{
			player = (EntityPlayerMP) sender;
			playerName = sender.username;
		}
		else if (player == null)
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			OutputHandler.chatConfirmation(sender, args[0] + " will be used, but may be inaccurate.");
			playerExists = false;
		}
		else
		{
			playerName = player.username;
		}
		
		if (args.length == 1) // display user-specific settings & there values for this player
		{
			ArrayList<Group> groups = PermissionsAPI.getApplicableGroups(args[0], false, ZoneManager.GLOBAL.getZoneName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.permissions.user.info.groups", player.username));
			for (Group g : groups)
			{
				OutputHandler.chatConfirmation(sender, " - "+g.name+" -- "+g.zoneName);
			}
			return;
		}
		else if (args[1].equalsIgnoreCase("supers")) // super perms management
		{
			if (args.length == 2) // display user super perms
			{
				Zone zone = ZoneManager.SUPER;
				ArrayList list = PermissionsAPI.getPlayerPermissions(args[0], zone.getZoneName());
				boolean error = false;
				for(Object lineObj : list)
				{
					String line = (String)lineObj;
					if(line.equalsIgnoreCase("error"))
					{
						error = true;
						continue;
					}
					if(error)
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
				Zone zone = ZoneManager.SUPER;
				if (args.length == 5) // zone is set
				{
					if(ZoneManager.doesZoneExist(args[4]))
					{
						zone = ZoneManager.getZone(args[4]);
					}
					else
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					}
				}
				
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("allow"))
				{
					PermissionsAPI.setPlayerPermission(playerName, args[3], true, zone.getZoneName());
					OutputHandler.chatConfirmation(sender, playerName + " has been allowed " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("clear") || args[2].equalsIgnoreCase("remove")) // remove super
																									// perm settings
				{
					PermissionsAPI.clearPlayerPermission(playerName, args[3], zone.getZoneName());
					OutputHandler.chatConfirmation(sender, playerName + "'s access to " + args[2] + " cleared");
					return;
				}
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("deny")) // deny super perm
				{
					PermissionsAPI.setPlayerPermission(playerName, args[3], false, zone.getZoneName());
					OutputHandler.chatConfirmation(sender, playerName + " has been denied " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					//Get current state.
					return;
				}
				
			}
		}
		else if (args[1].equalsIgnoreCase("group")) // group management
		{
			String zoneName = ZoneManager.GLOBAL.getZoneName();
			if (args.length == 5) // zone is set
			{
				if(ZoneManager.getZone(args[4]) != null)
				{
					zoneName = args[4];
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}
			
			if (args[2].equalsIgnoreCase("add")) // add player to group
			{
				if(args.length > 3)
				{
					String result = PermissionsAPI.addPlayerToGroup(args[3], playerName, zoneName);
					if(result != null)
					{
						OutputHandler.chatError(sender, result);
					}
					else
					{
						OutputHandler.chatConfirmation(sender, playerName + " added to group " + args[3]);
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
				}
				return;
			}
			else if (args[2].equalsIgnoreCase("remove")) // remove player from group
			{
				if(args.length > 3)
				{
					String result = PermissionsAPI.clearPlayerGroup(args[3], playerName, zoneName);
					if(result != null)
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
				if(args.length > 3)
				{
					String result = PermissionsAPI.setPlayerGroup(args[3], playerName, zoneName);
					if(result != null)
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
		else if (args.length >= 3) // player management
		{
			String zoneName = ZoneManager.GLOBAL.getZoneName();
			if (args.length == 4) // zone is set
			{
				if(ZoneManager.getZone(args[3]) != null)
				{
					zoneName = args[3];
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}
			
			if (args[1].equalsIgnoreCase("prefix")) // prefix
			{
				if(args.length == 2 && args[2].equalsIgnoreCase("set"))
				{
					args[2] = args[3];
				}
				
				if(args.length >= 2)
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = args[2];
					OutputHandler.chatConfirmation(sender, playerName + "'s prefix set to &f" + args[2]);
				}
				else
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = "";
					OutputHandler.chatConfirmation(sender, playerName + "'s removed");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("suffix")) // suffix
			{
				if(args.length == 2 && args[2].equalsIgnoreCase("set")) {
					args[2] = args[3];
				}
				
				if(args.length >= 2)
				{
					PlayerInfo.getPlayerInfo(playerName).suffix = args[2];
					OutputHandler.chatConfirmation(sender, playerName + "'s suffix set to &f" + args[2]);
				}
				else
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = "";
					OutputHandler.chatConfirmation(sender, playerName + "'s removed");
				}
				return;
			}

			else if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow")) // allow player perm
			{
				String result = PermissionsAPI.setPlayerPermission(playerName, args[2], true, zoneName);
				if(result != null)
				{
					OutputHandler.chatError(sender, result);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, playerName + "  allowed access to " + args[2] + ".");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove")) // remove perm settings
			{
				String result = PermissionsAPI.clearPlayerPermission(playerName, args[2], zoneName);
				if(result != null)
				{
					OutputHandler.chatError(sender, result);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, playerName + " denied access to " + args[2] + ".");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny")) // deny player perm
			{
				String result = PermissionsAPI.setPlayerPermission(playerName, args[2], false, zoneName);
				if(result != null)
				{
					OutputHandler.chatError(sender, result);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, playerName + "'s  access to " + args[2] + "cleared.");
				}
				return;
			}
		}
		
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0) // display syntax & possible options for this level
		{
			sender.sendChatToPlayer("Possible usage:");
			sender.sendChatToPlayer("/p user <player> : Display user statistics");
			sender.sendChatToPlayer("/p user <player> supers : Player's superperms");
			sender.sendChatToPlayer("/p user <player> group : Player's group settings");
			sender.sendChatToPlayer("/p user <player> allow|true|deny|false|clear : Player's individual permissions");
			return;
		}
		
		boolean playerExists = true;
		String playerName = args[0];
		EntityPlayerMP player = FunctionHelper.getPlayerFromUsername(args[0]);
		if (player == null)
		{
			sender.sendChatToPlayer("ERROR: " + Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			sender.sendChatToPlayer(args[0] + " will be used, but may be inaccurate.");
			playerExists = false;
		}
		else
		{
			playerName = player.username;
		}
		
		if (args.length == 1) // display user-specific settings & there values for this player
		{
			ArrayList<Group> groups = PermissionsAPI.getApplicableGroups(args[0], false, ZoneManager.GLOBAL.getZoneName());
			sender.sendChatToPlayer(Localization.format("command.permissions.user.info.groups", player.username));
			for (Group g : groups)
			{
				sender.sendChatToPlayer(" - "+g.name+" -- "+g.zoneName);
			}
			return;
		}
		else if (args[1].equalsIgnoreCase("supers")) // super perms management
		{
			if (args.length == 2) // display user super perms
			{
				Zone zone = ZoneManager.SUPER;
				ArrayList list = PermissionsAPI.getPlayerPermissions(args[0], zone.getZoneName());
				boolean error = false;
				for(Object lineObj : list)
				{
					String line = (String)lineObj;
					if(line.equalsIgnoreCase("error"))
					{
						error = true;
						continue;
					}
					if(error)
					{
						sender.sendChatToPlayer("ERROR: " + line);
					}
					else
					{
						sender.sendChatToPlayer(line);
					}
				}
				return;
			}
			else if (args.length >= 3) // changing super perms
			{
				Zone zone = ZoneManager.SUPER;
				if (args.length == 5) // zone is set
				{
					if(ZoneManager.doesZoneExist(args[4]))
					{
						zone = ZoneManager.getZone(args[4]);
					}
					else
					{
						sender.sendChatToPlayer("ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					}
				}
				
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("allow"))
				{
					PermissionsAPI.setPlayerPermission(playerName, args[3], true, zone.getZoneName());
					sender.sendChatToPlayer(playerName + " has been allowed " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("clear") || args[2].equalsIgnoreCase("remove")) // remove super
																									// perm settings
				{
					PermissionsAPI.clearPlayerPermission(playerName, args[3], zone.getZoneName());
					sender.sendChatToPlayer(playerName + "'s access to " + args[2] + " cleared");
					return;
				}
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("deny")) // deny super perm
				{
					PermissionsAPI.setPlayerPermission(playerName, args[3], false, zone.getZoneName());
					sender.sendChatToPlayer(playerName + " has been denied " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					//Get current state.
					return;
				}
				
			}
		}
		else if (args[1].equalsIgnoreCase("group")) // group management
		{
			String zoneName = ZoneManager.GLOBAL.getZoneName();
			if (args.length == 5) // zone is set
			{
				if(ZoneManager.getZone(args[4]) != null)
				{
					zoneName = args[4];
				}
				else
				{
					sender.sendChatToPlayer("ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}
			if (args[2].equalsIgnoreCase("add")) // add player to group
			{
				if(args.length > 3)
				{
					String result = PermissionsAPI.addPlayerToGroup(args[3], playerName, zoneName);
					if(result != null)
					{
						sender.sendChatToPlayer("ERROR: " + result);
					}
					else
					{
						sender.sendChatToPlayer(playerName + " added to group " + args[3]);
					}
				}
				else
				{
					sender.sendChatToPlayer("ERROR: " + Localization.get(Localization.ERROR_BADSYNTAX));
				}
				return;
			}
			else if (args[2].equalsIgnoreCase("remove")) // remove player from group
			{
				if(args.length > 3)
				{
					String result = PermissionsAPI.clearPlayerGroup(args[3], playerName, zoneName);
					if(result != null)
					{
						sender.sendChatToPlayer("ERROR: " + result);
					}
					else
					{
						sender.sendChatToPlayer(playerName + " removed from group " + args[3]);
					}
				}
				return;
			}
			else if (args[2].equalsIgnoreCase("set")) // set player's group
			{
				if(args.length > 3)
				{
					String result = PermissionsAPI.setPlayerGroup(args[3], playerName, zoneName);
					if(result != null)
					{
						sender.sendChatToPlayer("ERROR: " + result);
					}
					else
					{
						sender.sendChatToPlayer(playerName + "'s group set to " + args[3]);
					}
				}
				return;
			}
		}
		else if (args.length >= 3) // player management
		{
			String zoneName = ZoneManager.GLOBAL.getZoneName();
			if (args.length == 4) // zone is set
			{
				if(ZoneManager.getZone(args[3]) != null)
				{
					zoneName = args[3];
				}
				else
				{
					sender.sendChatToPlayer("ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					return;
				}
			}
			
			if (args[1].equalsIgnoreCase("prefix")) // prefix
			{
				if(args.length == 2 && args[2].equalsIgnoreCase("set")) {
					args[2] = args[3];
				}
				
				if(args.length >= 2)
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = args[2];
					sender.sendChatToPlayer(playerName + "'s prefix set to &f" + args[2]);
				}
				else
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = "";
					sender.sendChatToPlayer(playerName + "'s removed");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("suffix")) // suffix
			{
				if(args.length == 2 && args[2].equalsIgnoreCase("set")) {
					args[2] = args[3];
				}
				if(args.length >= 2)
				{
					PlayerInfo.getPlayerInfo(playerName).suffix = args[2];
					sender.sendChatToPlayer(playerName + "'s suffix set to &f" + args[2]);
				}
				else
				{
					PlayerInfo.getPlayerInfo(playerName).prefix = "";
					sender.sendChatToPlayer(playerName + "'s removed");
				}
				return;
			}

			else if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow")) // allow player perm
			{
				String result = PermissionsAPI.setPlayerPermission(playerName, args[2], true, zoneName);
				if(result != null)
				{
					sender.sendChatToPlayer("ERROR: " + result);
				}
				else
				{
					sender.sendChatToPlayer(playerName + "  allowed access to " + args[2] + ".");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove")) // remove perm settings
			{
				String result = PermissionsAPI.clearPlayerPermission(playerName, args[2], zoneName);
				if(result != null)
				{
					sender.sendChatToPlayer("ERROR: " + result);
				}
				else
				{
					sender.sendChatToPlayer(playerName + " denied access to " + args[2] + ".");
				}
				return;
			}
			else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny")) // deny player perm
			{
				String result = PermissionsAPI.setPlayerPermission(playerName, args[2], false, zoneName);
				if(result != null)
				{
					sender.sendChatToPlayer("ERROR: " + result);
				}
				else
				{
					sender.sendChatToPlayer(playerName + "'s  access to " + args[2] + "cleared.");
				}
				return;
			}
		}
		else // display user-specific settings & there values for this player
		{
			ArrayList<Group> groups = PermissionsAPI.getApplicableGroups(args[0], false, args[1]);
			sender.sendChatToPlayer(Localization.format("command.permissions.user.info.groups", player.username));
			for (Group g : groups)
			{
				sender.sendChatToPlayer(" - "+g.name+" -- "+g.zoneName);
			}
			return;
		}
		
		sender.sendChatToPlayer("ERROR: " + Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

}
