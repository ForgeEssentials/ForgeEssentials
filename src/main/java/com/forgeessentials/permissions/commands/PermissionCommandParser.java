package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldPoint;

public class PermissionCommandParser {

	enum PermissionAction
	{
		ALLOW, DENY, CLEAR
	}

	private ICommandSender sender;
	private EntityPlayerMP senderPlayer;
	private Queue<String> args;

	public PermissionCommandParser(ICommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = new LinkedList<String>(Arrays.asList(args));
		this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
		parseMain();
	}

	public PermissionCommandParser(EntityPlayerMP player, String[] args)
	{
		this.sender = player;
		this.args = new LinkedList<String>(Arrays.asList(args));
		this.senderPlayer = player;
		parseMain();
	}

	private void info(String message)
	{
		OutputHandler.chatConfirmation(sender, message);
	}

	private void error(String message)
	{
		OutputHandler.chatError(sender, message);
	}

	private void parseMain()
	{
		if (args.isEmpty())
		{
			info("Base usage is /p user|group|default.");
			info("Type one of these for more information.");
		}
		else
		{
			switch (args.remove().toLowerCase()) {
			case "user":
				parseUser();
				break;
			case "group":
				parseGroup();
				break;
			case "default":
				parseDefault();
				break;
			default:
				break;
			}
		}
	}

	private void parseUser()
	{
		if (args.isEmpty())
		{
			info("Possible usage:");
			info("/p user <player> : Display user info");
			info("/p user <player> perms : List player's permissions");
			info("/p user <player> group : Player's group settings");
			info("/p user <player> allow|deny|clear <perm> : Player global permissions");
			info("/p user <player> allow|deny|clear [zone] <permission-list> : Player permissions");
		}
		else
		{
			String playerName = args.remove();
			UserIdent ident = null;
			if (playerName.equalsIgnoreCase("_ME_"))
			{
				if (senderPlayer == null)
				{
					error("_ME_ cannot be used in console.");
					return;
				}
				ident = new UserIdent(senderPlayer);
			}
			else
			{
				ident = new UserIdent(playerName);
				if (!ident.hasUUID())
				{
					error(String.format("Player %s not found. playername will be used, but may be inaccurate.", ident.getUsername()));
				}
			}

			if (args.isEmpty())
			{
				Collection<Group> groups = APIRegistry.perms.getPlayerGroups(ident);
				info(String.format("Groups for player %s:", ident.getUsernameOrUUID()));
				for (Group g : groups)
				{
					info(" - " + g.getName());
				}
			}
			else
			{
				switch (args.remove().toLowerCase()) {
				case "group":
					parseUserGroup(ident);
					break;
				case "perms":
					parseUserListPermissions(ident);
					break;
				case "prefix":
					parseUserPrefixSuffix(ident, false);
					break;
				case "suffix":
					parseUserPrefixSuffix(ident, true);
					break;
				case "true":
				case "allow":
					parseUserPermissions(ident, PermissionAction.ALLOW);
					break;
				case "false":
				case "deny":
					parseUserPermissions(ident, PermissionAction.DENY);
					break;
				case "clear":
				case "remove":
					parseUserPermissions(ident, PermissionAction.CLEAR);
					break;
				default:
					break;
				}
			}
		}
	}

	private void parseUserPermissions(UserIdent ident, PermissionAction type)
	{
		if (args.isEmpty())
		{
			error("Missing permission argument!");
			return;
		}

		// Get zone
		Zone zone = APIRegistry.perms.getServerZone();
		if (args.size() > 1)
		{
			String id = args.remove();
			try
			{
				zone = APIRegistry.perms.getZoneById(Integer.parseInt(id));
				if (zone == null)
				{
					error(String.format("No zone by the ID %s exists!", id));
					return;
				}
			}
			catch (NumberFormatException e)
			{
				if (senderPlayer == null)
				{
					error("Cannot identify zones by name from console!");
					return;
				}
				zone = APIRegistry.perms.getWorldZone(senderPlayer.dimension).getAreaZone(id);
				if (zone == null)
				{
					error(String.format("No zone by the name %s exists!", id));
					return;
				}
			}
		}

		// Apply permissions
		while (!args.isEmpty())
		{
			String permissionNode = args.remove();
			String result = null, msg = null;
			switch (type) {
			case ALLOW:
				zone.setPlayerPermission(ident, permissionNode, true);
				msg = "Allowed %s access to %s";
				break;
			case DENY:
				zone.setPlayerPermission(ident, permissionNode, false);
				msg = "Denied %s access to %s";
				break;
			case CLEAR:
				zone.clearPlayerPermission(ident, permissionNode);
				msg = "Cleared %s's acces to %s";
				break;
			}
			info(String.format(msg, ident.getUsernameOrUUID(), permissionNode));
		}
	}

	private void parseUserPrefixSuffix(UserIdent ident, boolean isSuffix)
	{
		PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(ident.getUuid());
		if (args.isEmpty())
		{
			if (isSuffix)
				info(String.format("%s's suffix is %s", ident.getUsernameOrUUID(), playerInfo.getSuffix().isEmpty() ? "empty" : playerInfo.getSuffix()));
			else
				info(String.format("%s's prefix is %s", ident.getUsernameOrUUID(), playerInfo.getPrefix().isEmpty() ? "empty" : playerInfo.getPrefix()));
		}
		else
		{
			String fix = args.remove();
			if (fix.equalsIgnoreCase("clear"))
			{
				fix = "";
				info(String.format("%s's %s cleared", ident.getUsernameOrUUID(), isSuffix ? "suffix" : "prefix"));
			}
			else
			{
				info(String.format("%s's %s set to %s", ident.getUsernameOrUUID(), isSuffix ? "suffix" : "prefix", fix));
			}
			if (isSuffix)
			{
				playerInfo.setSuffix(fix);
			}
			else
			{
				playerInfo.setPrefix(fix);
			}
		}
	}

	private void parseUserListPermissions(UserIdent ident)
	{
		Map<Zone, Map<String, String>> userPerms = ModulePermissions.permissionHelper.enumUserPermissions(ident);
		if (userPerms.isEmpty())
		{
			info(ident.getUsernameOrUUID() + " has no individual permissions");
			return;
		}

		info(ident.getUsernameOrUUID() + " permissions:");
		for (Entry<Zone, Map<String, String>> zone : userPerms.entrySet())
		{
			info("Zone #" + zone.getKey().getId() + " " + zone.getKey().getName());
			for (Entry<String, String> perm : zone.getValue().entrySet())
			{
				info("  " + perm.getKey() + " = " + perm.getValue());
			}
		}

		for (Group group : APIRegistry.perms.getPlayerGroups(ident))
		{
			Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper.enumGroupPermissions(group.getName());
			if (!groupPerms.isEmpty())
			{
				info("Group #" + group.getId() + " " + group.getName());
				for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet())
				{
					info("Zone #" + zone.getKey().getId() + " " + zone.getKey().getName());
					for (Entry<String, String> perm : zone.getValue().entrySet())
					{
						info("  " + perm.getKey() + " = " + perm.getValue());
					}
				}
			}
		}
	}

	private void parseUserGroup(UserIdent ident)
	{
		throw new RuntimeException("Not yet implemented!");
	}

	private void parseGroup()
	{
		if (args.isEmpty())
		{
			info("Possible usage:");
			info("/p group <group> : Display group info");
			info("/p group <group> create : Create a new group");
			info("/p group <group> perms : List group's permissions");
			info("/p group <group> allow|deny|clear <perm> : Group global permissions");
			info("/p group <group> allow|deny|clear [zone] <permission-list> : Group permissions");
		}
		else
		{
			String groupName = args.remove();
			Group group = APIRegistry.perms.getGroup(groupName);
			if (group == null)
			{
				if (args.isEmpty())
				{
					info(String.format("Group %s does not exist", groupName));
				}
				else
				{
					String groupArg = args.remove();
					if (groupArg.equalsIgnoreCase("create"))
					{
						group = APIRegistry.perms.createGroup(groupName);
						if (group == null)
						{
							info(String.format("Created group %s", groupName));
						}
						else
						{
							error(String.format("Group %s already exists", groupName));
						}
					}
					else
					{
						error(String.format("Group %s does not exist", groupName));
					}
				}
				return;
			}
			if (args.isEmpty())
			{
				throw new RuntimeException("Not yet implemented!");
//				Collection<Group> groups = APIRegistry.perms.getPlayerGroups(group);
//				info(String.format("Groups for player %s:", group.getUsernameOrUUID()));
//				for (Group g : groups)
//				{
//					info(" - " + g.getName());
//				}
			}
			else
			{
				switch (args.remove().toLowerCase()) {
//				case "users":
//					listGroupUsers(group);
//					break;
//				case "perms":
//					listGroupPermissions(group);
//					break;
//				case "prefix":
//					parseGroupPrefixSuffix(group, false);
//					break;
//				case "suffix":
//					parseGroupPrefixSuffix(group, true);
//					break;
				case "true":
				case "allow":
					parseGroupPermissions(group, PermissionAction.ALLOW);
					break;
				case "false":
				case "deny":
					parseGroupPermissions(group, PermissionAction.DENY);
					break;
				case "clear":
				case "remove":
					parseGroupPermissions(group, PermissionAction.CLEAR);
					break;
				default:
					break;
				}
			}
			
		}
	}

	private void parseGroupPermissions(Group group, PermissionAction type)
	{
		if (args.isEmpty())
		{
			error("Missing permission argument!");
			return;
		}

		// Get zone
		Zone zone = APIRegistry.perms.getServerZone();
		if (args.size() > 1)
		{
			String id = args.remove();
			try
			{
				zone = APIRegistry.perms.getZoneById(Integer.parseInt(id));
				if (zone == null)
				{
					error(String.format("No zone by the ID %s exists!", id));
					return;
				}
			}
			catch (NumberFormatException e)
			{
				if (senderPlayer == null)
				{
					error("Cannot identify zones by name from console!");
					return;
				}
				zone = APIRegistry.perms.getWorldZone(senderPlayer.dimension).getAreaZone(id);
				if (zone == null)
				{
					error(String.format("No zone by the name %s exists!", id));
					return;
				}
			}
		}

		// Apply permissions
		while (!args.isEmpty())
		{
			String permissionNode = args.remove();
			String result = null, msg = null;
			switch (type) {
			case ALLOW:
				zone.setGroupPermission(group.getName(), permissionNode, true);
				msg = "Allowed %s access to %s";
				break;
			case DENY:
				zone.setGroupPermission(group.getName(), permissionNode, false);
				msg = "Denied %s access to %s";
				break;
			case CLEAR:
				zone.clearGroupPermission(group.getName(), permissionNode);
				msg = "Cleared %s's acces to %s";
				break;
			}
			info(String.format(msg, group.getName(), permissionNode));
		}
	}

	private void parseDefault()
	{
		throw new RuntimeException("Not yet implemented!");
	}

}
