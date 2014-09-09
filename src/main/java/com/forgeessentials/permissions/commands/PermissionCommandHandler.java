package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WorldPoint;

public class PermissionCommandHandler {

	enum PermissionAction
	{
		ALLOW, DENY, CLEAR
	}

	private ICommandSender sender;
	private EntityPlayerMP senderPlayer;
	private Queue<String> args;

	public PermissionCommandHandler(ICommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = new LinkedList<String>(Arrays.asList(args));
		this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
		parseMain();
	}

	public PermissionCommandHandler(EntityPlayerMP player, String[] args)
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
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, playerName);
			if (player == null)
			{
				if (playerName.equalsIgnoreCase("_ME_"))
				{
					if (senderPlayer == null)
					{
						error("_ME_ cannot be used in console.");
						return;
					}
					player = senderPlayer;
					playerName = player.getCommandSenderName();
				}
			}
			if (player == null)
			{
				error(String.format("Player %s does not exist, or is not online.", playerName));
				error(String.format("Playername %s will be used, but may be inaccurate.", playerName));
			}
			UUID playerID = FunctionHelper.getPlayerID(playerName);

			if (args.isEmpty())
			{
				if (playerID == null)
				{
					error("Player does not exist.");
				}
				else
				{
					throw new RuntimeException("Not yet implemented!");
//					ArrayList<Group> groups = APIRegistry.perms.getApplicableGroups(playerID, false, APIRegistry.perms.getGlobalZone().getName());
//					info(String.format("Group info for %s:", playerName));
//					for (Group g : groups)
//					{
//						info(" - " + g.name + "  in zone  " + g.zoneName);
//					}
				}
			}
			else
			{
				switch (args.remove().toLowerCase()) {
				case "supers":
					// TODO: Talk to malk what super permissions are for
					error("Not yet implemented.");
					break;
				case "group":
					parseUserGroup(playerID, playerName);
					break;
				case "perms":
					parseUserPerms(playerID, playerName);
					break;
				case "prefix":
					parseUserPrefixSuffix(playerID, playerName, false);
					break;
				case "suffix":
					parseUserPrefixSuffix(playerID, playerName, true);
					break;
				case "true":
				case "allow":
					parseUserPermissions(playerID, playerName, PermissionAction.ALLOW);
					break;
				case "false":
				case "deny":
					parseUserPermissions(playerID, playerName, PermissionAction.DENY);
					break;
				case "clear":
				case "remove":
					parseUserPermissions(playerID, playerName, PermissionAction.CLEAR);
					break;
				default:
					break;
				}
			}
		}
	}

	private void parseUserPermissions(UUID playerID, String playerName, PermissionAction type)
	{
		throw new RuntimeException("Not yet implemented!");
//		if (args.isEmpty())
//		{
//			error("Missing permission argument!");
//			return;
//		}
//		String zoneName = APIRegistry.perms.getGlobalZone().getName();
//		if (args.size() > 1) // zone is set
//		{
//			zoneName = args.remove();
//			if (APIRegistry.perms.getZone(zoneName) == null)
//			{
//				error(String.format("No zone by the name %s exists!", zoneName));
//				return;
//			}
//		}
//		while (!args.isEmpty())
//		{
//			String permissionNode = args.remove();
//			String result = null, msg = null;
//			switch (type) {
//			case ALLOW:
//				result = APIRegistry.perms.setPlayerPermission(playerID, permissionNode, true, zoneName);
//				msg = "%s allowed access to %s";
//				break;
//			case DENY:
//				result = APIRegistry.perms.setPlayerPermission(playerID, permissionNode, false, zoneName);
//				msg = "%s denied access to %s";
//				break;
//			case CLEAR:
//				result = APIRegistry.perms.clearPlayerPermission(playerID, permissionNode, zoneName);
//				msg = "%s cleared acces to %s";
//				break;
//			}
//			if (result != null)
//			{
//				error(result);
//			}
//			else
//			{
//				info(String.format(msg, playerName, permissionNode));
//			}
//		}
	}

	private void parseUserPrefixSuffix(UUID playerID, String playerName, boolean isSuffix)
	{
		PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(playerID);
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

	private void parseUserPerms(UUID playerID, String playerName)
	{
		throw new RuntimeException("Not yet implemented!");
//		String zoneName = APIRegistry.perms.getGlobalZone().getName();
//		if (!args.isEmpty())
//		{
//			zoneName = args.remove();
//			if (zoneName.equalsIgnoreCase("here") && senderPlayer != null)
//			{
//				zoneName = APIRegistry.perms.getZoneAt(new WorldPoint(senderPlayer)).getName();
//			}
//			else if (APIRegistry.perms.getZone(zoneName) == null)
//			{
//				error(String.format("No zone by the name %s exists!", zoneName));
//				return;
//			}
//		}
//
//		// TODO: Clean up everything below this point
//		ArrayList<String> list = APIRegistry.perms.getPlayerPermissions(playerID, zoneName);
//		Collections.sort(list);
//		ArrayList<String> messageAllowed = new ArrayList<String>();
//		ArrayList<String> messageDenied = new ArrayList<String>();
//		for (String permission : list)
//		{
//			if (permission.contains("has no individual permissions."))
//			{
//				info(permission);
//				return;
//			}
//			if (permission.contains("true"))
//			{
//				messageAllowed.add(" " + EnumChatFormatting.DARK_GREEN + permission.substring(0, permission.indexOf(":")));
//			}
//			else
//			{
//				messageDenied.add(" " + EnumChatFormatting.DARK_RED + permission.substring(0, permission.indexOf(":")));
//			}
//		}
//		info(playerName + ": Current permissions in zone " + zoneName + ":");
//		info(" (" + EnumChatFormatting.DARK_GREEN + "ALLOWED" + EnumChatFormatting.DARK_RED + " DENIED" + EnumChatFormatting.GREEN + ")");
//		for (String permission : messageAllowed)
//		{
//			info(permission);
//		}
//		for (String permission : messageDenied)
//		{
//			info(permission);
//		}
	}

	private void parseUserGroup(UUID playerID, String playerName)
	{

	}

	private void parseGroup()
	{
	}

	private void parseDefault()
	{
	}

}
