package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
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
	private boolean tabCompleteMode = false;
	private List<String> tabComplete;

	public PermissionCommandParser(ICommandSender sender, String[] args, boolean tabCompleteMode)
	{
		this.sender = sender;
		this.args = new LinkedList<String>(Arrays.asList(args));
		this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
		this.tabCompleteMode = tabCompleteMode;
		if (tabCompleteMode)
		{
			try
			{
				parseMain();
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			parseMain();
		}
	}

	public List<String> getTabCompleteList()
	{
		return tabComplete;
	}

	private void info(String message)
	{
		if (!tabCompleteMode)
			OutputHandler.chatConfirmation(sender, message);
	}

	private void error(String message)
	{
		if (!tabCompleteMode)
			OutputHandler.chatError(sender, message);
	}

	// Variables for auto-complete
	private static final String[] parseMainArgs = { "user", "group", "default" }; // "export", "promote", "test" };
	private static final String[] parseUserArgs = { "allow", "deny", "clear", "true", "false", "prefix", "suffix", "perms", "group" };
	private static final String[] parseGroupArgs = { "allow", "deny", "clear", "true", "false", "prefix", "suffix", "priority", "parent" };
	private static final String[] parseUserGroupArgs = { "add", "remove" };
	
	//private static final String[] playergargs = { "set", "add", "remove" };

	private void parseMain()
	{
		if (tabCompleteMode && args.size() == 1) {
			tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseMainArgs);
			return;
		}
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
		if (tabCompleteMode && args.size() == 1) {
			tabComplete = new ArrayList<String>();
			for (UserIdent knownPlayerIdent : APIRegistry.perms.getServerZone().getKnownPlayers())
			{
				if (CommandBase.doesStringStartWith(args.peek(), knownPlayerIdent.getUsernameOrUUID()))
					tabComplete.add(knownPlayerIdent.getUsernameOrUUID());
			}
			for (EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
			{
				if (CommandBase.doesStringStartWith(args.peek(), player.getGameProfile().getName()))
					tabComplete.add(player.getGameProfile().getName());
			}
			return;
		}
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

			if (tabCompleteMode && args.size() == 1) {
				tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseUserArgs);
				return;
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
					listUserPermissions(ident);
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
		if (tabCompleteMode) {
			tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseUserArgs);
			for (Zone zone : APIRegistry.perms.getZones())
			{
				if (CommandBase.doesStringStartWith(args.peek(), zone.getName()))
					tabComplete.add(zone.getName());
			}
			for (String perm : ModulePermissions.permissionHelper.enumRegisteredPermissions())
			{
				if (CommandBase.doesStringStartWith(args.peek(), perm))
					tabComplete.add(perm);
			}
			return;
		}
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
		if (tabCompleteMode)
			return;
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

	private void listUserPermissions(UserIdent ident)
	{
		if (tabCompleteMode)
			return;
		
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
		if (tabCompleteMode && args.size() == 1) {
			tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseUserGroupArgs);
			return;
		}
		if (args.isEmpty())
		{
			info("Possible usage:");
			info("/p user <player> group add : Add user to group");
			info("/p user <player> group remove : Add user to group");
		}
		else
		{
			String mode = args.remove().toLowerCase();
			if (!mode.equals("add") || !mode.equals("remove")) {
				error("Syntax error. Please try this instead:");
				error("/p user <player> group add : Add user to group");
				error("/p user <player> group remove : Add user to group");
				return;
			}
			
			if (tabCompleteMode && args.size() == 1) {
				tabComplete = new ArrayList<String>();
				for (String group : APIRegistry.perms.getServerZone().getGroups().keySet())
				{
					if (CommandBase.doesStringStartWith(args.peek(), group))
						tabComplete.add(group);
				}
				return;
			}
			if (args.isEmpty()) {
				info("Usage: /p user <player> group " + mode);
			} else {
				switch (mode) {
				case "add":
					throw new RuntimeException("Not yet implemented!");
					//break;
				case "remove":
					throw new RuntimeException("Not yet implemented!");
					//break;
				}
			}
		}
	}

	private void parseGroup()
	{
		if (tabCompleteMode && args.size() == 1) {
			tabComplete = new ArrayList<String>();
			for (String group : APIRegistry.perms.getServerZone().getGroups().keySet())
			{
				if (CommandBase.doesStringStartWith(args.peek(), group))
					tabComplete.add(group);
			}
			return;
		}
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
				if (tabCompleteMode && args.size() == 1) {
					tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), "create");
					return;
				}
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

			if (tabCompleteMode && args.size() == 1) {
				tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseGroupArgs);
				return;
			}
			if (args.isEmpty())
			{
				throw new RuntimeException("Not yet implemented!");
				// Collection<Group> groups = APIRegistry.perms.getPlayerGroups(group);
				// info(String.format("Groups for player %s:", group.getUsernameOrUUID()));
				// for (Group g : groups)
				// {
				// info(" - " + g.getName());
				// }
			}
			else
			{
				switch (args.remove().toLowerCase()) {
				// case "users":
				// listGroupUsers(group);
				// break;
				// case "perms":
				// listGroupPermissions(group);
				// break;
				// case "prefix":
				// parseGroupPrefixSuffix(group, false);
				// break;
				// case "suffix":
				// parseGroupPrefixSuffix(group, true);
				// break;
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
		if (tabCompleteMode) {
			tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseUserArgs);
			for (Zone zone : APIRegistry.perms.getZones())
			{
				if (CommandBase.doesStringStartWith(args.peek(), zone.getName()))
					tabComplete.add(zone.getName());
			}
			for (String perm : ModulePermissions.permissionHelper.enumRegisteredPermissions())
			{
				if (CommandBase.doesStringStartWith(args.peek(), perm))
					tabComplete.add(perm);
			}
			return;
		}
		
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
