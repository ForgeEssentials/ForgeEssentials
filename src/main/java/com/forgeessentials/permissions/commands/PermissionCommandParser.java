package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
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
			info("/p user <player> : Display user statistics");
			info("/p user <player> supers : Player's superperms");
			info("/p user <player> group : Player's group settings");
			info("/p user <player> allow|true|deny|false|clear <perm> : Player's individual permissions");
			info("/p user <player> allow|true|deny|false|clear [zone] <permission-list> : Player's individual permissions");
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
				info(String.format("Groups for player %s:", ident.getIdentificationString()));
				for (Group g : groups)
				{
					info(" - " + g.getName());
				}
			}
			else
			{
				switch (args.remove().toLowerCase()) {
				case "group":
					parseUserGroup(ident, playerName);
					break;
				case "perms":
					parseUserPerms(ident, playerName);
					break;
				case "prefix":
					parseUserPrefixSuffix(ident, playerName, false);
					break;
				case "suffix":
					parseUserPrefixSuffix(ident, playerName, true);
					break;
				case "true":
				case "allow":
					parseUserPermissions(ident, playerName, PermissionAction.ALLOW);
					break;
				case "false":
				case "deny":
					parseUserPermissions(ident, playerName, PermissionAction.DENY);
					break;
				case "clear":
				case "remove":
					parseUserPermissions(ident, playerName, PermissionAction.CLEAR);
					break;
				default:
					break;
				}
			}
		}
	}

	private void parseUserPermissions(UserIdent ident, String playerName, PermissionAction type)
	{
		throw new RuntimeException("Not yet implemented!");
		// if (args.isEmpty())
		// {
		// error("Missing permission argument!");
		// return;
		// }
		// String zoneName = APIRegistry.perms.getGlobalZone().getName();
		// if (args.size() > 1) // zone is set
		// {
		// zoneName = args.remove();
		// if (APIRegistry.perms.getZone(zoneName) == null)
		// {
		// error(String.format("No zone by the name %s exists!", zoneName));
		// return;
		// }
		// }
		// while (!args.isEmpty())
		// {
		// String permissionNode = args.remove();
		// String result = null, msg = null;
		// switch (type) {
		// case ALLOW:
		// result = APIRegistry.perms.setPlayerPermission(ident, permissionNode, true, zoneName);
		// msg = "%s allowed access to %s";
		// break;
		// case DENY:
		// result = APIRegistry.perms.setPlayerPermission(ident, permissionNode, false, zoneName);
		// msg = "%s denied access to %s";
		// break;
		// case CLEAR:
		// result = APIRegistry.perms.clearPlayerPermission(ident, permissionNode, zoneName);
		// msg = "%s cleared acces to %s";
		// break;
		// }
		// if (result != null)
		// {
		// error(result);
		// }
		// else
		// {
		// info(String.format(msg, playerName, permissionNode));
		// }
		// }
	}

	private void parseUserPrefixSuffix(UserIdent ident, String playerName, boolean isSuffix)
	{
		PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(ident.getUuid());
		if (args.isEmpty())
		{
			if (isSuffix)
				info(String.format("%s's suffix is %s", playerName, playerInfo.getSuffix().isEmpty() ? "empty" : playerInfo.getSuffix()));
			else
				info(String.format("%s's prefix is %s", playerName, playerInfo.getPrefix().isEmpty() ? "empty" : playerInfo.getPrefix()));
		}
		else
		{
			String fix = args.remove();
			if (fix.equalsIgnoreCase("clear"))
			{
				fix = "";
				info(String.format("%s's %s cleared", playerName, isSuffix ? "suffix" : "prefix"));
			}
			else
			{
				info(String.format("%s's %s set to %s", playerName, isSuffix ? "suffix" : "prefix", fix));
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

	private void parseUserPerms(UserIdent ident, String playerName)
	{
		throw new RuntimeException("Not yet implemented!");
		// String zoneName = APIRegistry.perms.getGlobalZone().getName();
		// if (!args.isEmpty())
		// {
		// zoneName = args.remove();
		// if (zoneName.equalsIgnoreCase("here") && senderPlayer != null)
		// {
		// zoneName = APIRegistry.perms.getZoneAt(new WorldPoint(senderPlayer)).getName();
		// }
		// else if (APIRegistry.perms.getZone(zoneName) == null)
		// {
		// error(String.format("No zone by the name %s exists!", zoneName));
		// return;
		// }
		// }
		//
		// // TODO: Clean up everything below this point
		// ArrayList<String> list = APIRegistry.perms.getPlayerPermissions(ident, zoneName);
		// Collections.sort(list);
		// ArrayList<String> messageAllowed = new ArrayList<String>();
		// ArrayList<String> messageDenied = new ArrayList<String>();
		// for (String permission : list)
		// {
		// if (permission.contains("has no individual permissions."))
		// {
		// info(permission);
		// return;
		// }
		// if (permission.contains("true"))
		// {
		// messageAllowed.add(" " + EnumChatFormatting.DARK_GREEN + permission.substring(0, permission.indexOf(":")));
		// }
		// else
		// {
		// messageDenied.add(" " + EnumChatFormatting.DARK_RED + permission.substring(0, permission.indexOf(":")));
		// }
		// }
		// info(playerName + ": Current permissions in zone " + zoneName + ":");
		// info(" (" + EnumChatFormatting.DARK_GREEN + "ALLOWED" + EnumChatFormatting.DARK_RED + " DENIED" + EnumChatFormatting.GREEN + ")");
		// for (String permission : messageAllowed)
		// {
		// info(permission);
		// }
		// for (String permission : messageDenied)
		// {
		// info(permission);
		// }
	}

	private void parseUserGroup(UserIdent ident, String playerName)
	{

	}

	private void parseGroup()
	{
	}

	private void parseDefault()
	{
	}

}
