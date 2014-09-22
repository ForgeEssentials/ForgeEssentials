package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.OutputHandler;
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

	private void warn(String message)
	{
		if (!tabCompleteMode)
			OutputHandler.chatWarning(sender, message);
	}

	private void error(String message)
	{
		if (!tabCompleteMode)
			OutputHandler.chatError(sender, message);
	}

	// Variables for auto-complete
	private static final String[] parseMainArgs = { "zones", "user", "group", "list", "reload", "save" }; // "export", "promote", "test" };
	private static final String[] parseUserArgs = { "allow", "deny", "clear", "true", "false", "prefix", "suffix", "perms", "group" };
	private static final String[] parseGroupArgs = { "allow", "deny", "clear", "true", "false", "prefix", "suffix", "priority", "parent" };
	private static final String[] parseUserGroupArgs = { "add", "remove" };

	private void parseMain()
	{
		if (tabCompleteMode && args.size() == 1)
		{
			tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseMainArgs);
			return;
		}
		if (args.isEmpty())
		{
			help();
		}
		else
		{
			switch (args.remove().toLowerCase()) {
			case "save":
				ModulePermissions.permissionHelper.save();
				info("Permissions saved!");
				break;
			case "reload":
				if (ModulePermissions.permissionHelper.load())
					info("Successfully reloaded permissions");
				else
					error("Error while reloading permissions");
				break;
			case "zones":
				listZones();
				break;
			case "list":
				listPermissions();
				break;
			case "user":
				parseUser();
				break;
			case "group":
				parseGroup();
				break;
			default:
				error("Unknown command argument");
				break;
			}
		}
	}

	private void help()
	{
		info("/feperm " + StringUtils.join(parseMainArgs, "|") + ": Displays help for the subcommands");
	}

	private void listPermissions()
	{
		if (senderPlayer == null)
		{
			error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
			return;
		}
		listUserPermissions(new UserIdent(senderPlayer));
	}
	
	private void listZones()
	{
		if (senderPlayer == null)
		{
			error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
			return;
		}
		WorldPoint wp = new WorldPoint(senderPlayer);
		info("Zones at position " + wp.toString());
		for (Zone zone : APIRegistry.perms.getZonesAt(wp))
		{
			info("  #" + zone.getId() + " " + zone.toString());
		}
	}

	private void parseUser()
	{
		if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), FEPermissions.PERM_USER))
		{
			OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}

		if (tabCompleteMode && args.size() == 1)
		{
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

			if (tabCompleteMode && args.size() == 1)
			{
				tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseUserArgs);
				return;
			}
			if (args.isEmpty())
			{
				info(String.format("Groups for player %s:", ident.getUsernameOrUUID()));
				for (String group : APIRegistry.perms.getPlayerGroups(ident))
				{
					info("  " + group);
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
		if (tabCompleteMode)
		{
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
		String fixName = isSuffix ? "suffix" : "prefix";
		if (args.isEmpty())
		{
			String fix = APIRegistry.perms.getServerZone().getPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
			if (fix == null || fix.isEmpty())
				fix = "empty";
			info(String.format("%s's %s is %s", ident.getUsernameOrUUID(), fixName, fix));
		}
		else
		{
			String fix = args.remove();
			if (fix.equalsIgnoreCase("clear"))
			{
				info(String.format("%s's %s cleared", ident.getUsernameOrUUID(), fixName));
				APIRegistry.perms.getServerZone().clearPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
			}
			else
			{
				info(String.format("%s's %s set to %s", ident.getUsernameOrUUID(), fixName, fix));
				APIRegistry.perms.getServerZone().setPlayerPermissionProperty(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
			}
		}
	}

	private void listUserPermissions(UserIdent ident)
	{
		if (tabCompleteMode)
			return;

		if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), FEPermissions.PERM_LIST))
		{
			OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}

		info(ident.getUsernameOrUUID() + " permissions:");

		Map<Zone, Map<String, String>> userPerms = ModulePermissions.permissionHelper.enumUserPermissions(ident);
		for (Entry<Zone, Map<String, String>> zone : userPerms.entrySet())
		{
			boolean printedZone = false;
			for (Entry<String, String> perm : zone.getValue().entrySet())
			{
				if (perm.getKey().equals(FEPermissions.GROUP) || perm.getKey().equals(FEPermissions.GROUP_ID)
						|| perm.getKey().equals(FEPermissions.GROUP_PRIORITY) || perm.getKey().equals(FEPermissions.PREFIX)
						|| perm.getKey().equals(FEPermissions.SUFFIX))
					continue;
				if (!printedZone)
				{
					warn("Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
					printedZone = true;
				}
				info("  " + perm.getKey() + " = " + perm.getValue());
			}
		}

		for (String group : APIRegistry.perms.getPlayerGroups(ident))
		{
			Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper.enumGroupPermissions(group, false);
			if (!groupPerms.isEmpty())
			{
				boolean printedGroup = false;
				for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet())
				{
					boolean printedZone = false;
					for (Entry<String, String> perm : zone.getValue().entrySet())
					{
						if (perm.getKey().equals(FEPermissions.GROUP) || perm.getKey().equals(FEPermissions.GROUP_ID)
								|| perm.getKey().equals(FEPermissions.GROUP_PRIORITY) || perm.getKey().equals(FEPermissions.PREFIX)
								|| perm.getKey().equals(FEPermissions.SUFFIX))
							continue;
						if (!printedGroup)
						{
							warn("Group " + group);
							printedGroup = true;
						}
						if (!printedZone)
						{
							warn("  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
							printedZone = true;
						}
						info("    " + perm.getKey() + " = " + perm.getValue());
					}
				}
			}
		}
	}

	private void parseUserGroup(UserIdent ident)
	{
		if (tabCompleteMode && args.size() == 1)
		{
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
			if (!mode.equals("add") && !mode.equals("remove"))
			{
				error("Syntax error. Please try this instead:");
				error("/p user <player> group add : Add user to group");
				error("/p user <player> group remove : Add user to group");
				return;
			}

			if (tabCompleteMode && args.size() == 1)
			{
				tabComplete = new ArrayList<String>();
				for (String group : APIRegistry.perms.getServerZone().getGroups())
				{
					if (CommandBase.doesStringStartWith(args.peek(), group))
						tabComplete.add(group);
				}
				return;
			}
			if (args.isEmpty())
			{
				error("Usage: /p user <player> group " + mode + " <group-name>");
			}
			else
			{
				String group = args.remove();
				if (!APIRegistry.perms.groupExists(group))
				{
					error(String.format("Group %s not found.", group));
					return;
				}
				switch (mode) {
				case "add":
					APIRegistry.perms.addPlayerToGroup(ident, group);
					info(String.format("Player %s added to group %s", ident.getUsernameOrUUID(), group));
					break;
				case "remove":
					APIRegistry.perms.removePlayerFromGroup(ident, group);
					info(String.format("Player %s removed from group %s", ident.getUsernameOrUUID(), group));
					break;
				}
			}
		}
	}

	private void parseGroup()
	{
		if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), FEPermissions.PERM_GROUP))
		{
			OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}

		if (tabCompleteMode && args.size() == 1)
		{
			tabComplete = new ArrayList<String>();
			for (String group : APIRegistry.perms.getServerZone().getGroups())
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
			info("/p group <group> allow|deny|clear <perm> : String global permissions");
			info("/p group <group> allow|deny|clear [zone] <permission-list> : String permissions");
		}
		else
		{
			String group = args.remove();
			if (!APIRegistry.perms.groupExists(group))
			{
				if (tabCompleteMode && args.size() == 1)
				{
					tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), "create");
					return;
				}
				if (args.isEmpty())
				{
					info(String.format("Group %s does not exist", group));
				}
				else
				{
					String groupArg = args.remove();
					if (groupArg.equalsIgnoreCase("create"))
					{
						APIRegistry.perms.createGroup(group);
						info(String.format("Created group %s", group));
					}
					else
					{
						error(String.format("Group %s does not exist", group));
					}
				}
				return;
			}

			if (tabCompleteMode && args.size() == 1)
			{
				tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseGroupArgs);
				return;
			}
			if (args.isEmpty())
			{
				info("Group " + group + ":");
				// info("  ID    : " + group.getId());
				// info("  prio  : " + group.getPriority());
				// info("  prefix: " + group.getPrefix());
				// info("  suffix: " + group.getSuffix());
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
				case "prefix":
					parseGroupPrefixSuffix(group, false);
					break;
				case "suffix":
					parseGroupPrefixSuffix(group, true);
					break;
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

	private void parseGroupPrefixSuffix(String group, boolean isSuffix)
	{
		if (tabCompleteMode)
			return;
		String fixName = isSuffix ? "suffix" : "prefix";
		if (args.isEmpty())
		{
			String fix = APIRegistry.perms.getServerZone().getGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
			if (fix == null || fix.isEmpty())
				fix = "empty";
			info(String.format("%s's %s is %s", group, fixName, fix));
		}
		else
		{
			String fix = args.remove();
			if (fix.equalsIgnoreCase("clear"))
			{
				info(String.format("%s's %s cleared", group, fixName));
				APIRegistry.perms.getServerZone().clearGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
			}
			else
			{
				info(String.format("%s's %s set to %s", group, fixName, fix));
				APIRegistry.perms.getServerZone().setGroupPermissionProperty(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
			}
		}
	}

	private void parseGroupPermissions(String group, PermissionAction type)
	{
		if (tabCompleteMode)
		{
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
				zone.setGroupPermission(group, permissionNode, true);
				msg = "Allowed %s access to %s";
				break;
			case DENY:
				zone.setGroupPermission(group, permissionNode, false);
				msg = "Denied %s access to %s";
				break;
			case CLEAR:
				zone.clearGroupPermission(group, permissionNode);
				msg = "Cleared %s's acces to %s";
				break;
			}
			info(String.format(msg, group, permissionNode));
		}
	}

}
