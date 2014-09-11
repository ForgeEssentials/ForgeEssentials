package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.teleport.util.PWarp;
import com.forgeessentials.teleport.util.TeleportDataManager;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.teleport.TeleportCenter;

public class CommandPersonalWarp extends ForgeEssentialsCommandBase {
	public final String PERMSETLIMIT = getPermissionNode() + ".setLimit";
	public final String PERMPROP = getPermissionNode() + ".max";

	@Override
	public String getCommandName()
	{
		return "personalwarp";
	}

	@Override
	public List<String> getCommandAliases()
	{
		List<String> aliases = new ArrayList<String>();
		aliases.add("pw");
		aliases.add("pwarp");
		return aliases;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		HashMap<String, PWarp> map = TeleportDataManager.pwMap.get(sender.getPersistentID());

		if (map == null)
		{
			map = new HashMap<String, PWarp>();
			TeleportDataManager.pwMap.put(sender.getPersistentID().toString(), map);
		}

		if (args.length == 0)
		{
			ChatUtils.sendMessage(sender, "Your personal warps:");
			ChatUtils.sendMessage(sender, FunctionHelper.niceJoin(map.keySet().toArray()));
		}
		else
		{
			if (args[0].equalsIgnoreCase("goto"))
			{
				if (map.containsKey(args[1]))
				{
					PWarp warp = map.get(args[1]);
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender.getPersistentID());
					playerInfo.setLastTeleportOrigin(new WarpPoint(sender));
					CommandBack.justDied.remove(sender.getPersistentID());
					TeleportCenter.addToTpQue(warp.getPoint(), sender);
				}
				else
				{
					OutputHandler.chatError(sender, "That personal warp doesn't exist!");
				}
			}
			else if (args[0].equalsIgnoreCase("add"))
			{
				if (!map.containsKey(args[1]))
				{
					Integer prop = APIRegistry.perms.getPermissionPropertyInt(new UserIdent(sender), PERMPROP);
					if (prop == null || prop == -1)
					{
						map.put(args[1], new PWarp(sender.getPersistentID().toString(), args[1], new WarpPoint(sender)));
						OutputHandler.chatConfirmation(sender, "Personal warp sucessfully added.");
					}
					else if (map.size() < prop)
					{
						map.put(args[1], new PWarp(sender.getPersistentID().toString(), args[1], new WarpPoint(sender)));
						OutputHandler.chatConfirmation(sender, "Personal warp sucessfully added.");
					}
					else
					{
						OutputHandler.chatError(sender, "You have reached your limit.");
					}
				}
				else
				{
					OutputHandler.chatError(sender, "That personal warp already exists.");
				}
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				if (map.containsKey(args[1]))
				{
					TeleportDataManager.removePWarp(map.get(args[1]));
					map.remove(args[1]);
					OutputHandler.chatConfirmation(sender, "Personal warp sucessfully removed.");
				}
				else
				{
					OutputHandler.chatError(sender, "That personal warp doesn't exist!");
				}
			}
			else if (args[0].equalsIgnoreCase("limit") && PermissionsManager.checkPermission(sender, PERMSETLIMIT))
			{
				if (args.length == 1)
				{
					OutputHandler.chatError(sender, "Specify a group or player. (-1 means no limit.)");
				}
				else
				{
					String target;
					if (APIRegistry.perms.getGroup(args[1]) != null)
					{
						target = "g:" + APIRegistry.perms.getGroup(args[1]).getName();
					}
					else if (args[1].equalsIgnoreCase("me"))
					{
						target = "p:" + sender.getCommandSenderName();
					}
					else
					{
						target = "p:" + UserIdent.getPlayerByMatch(sender, args[1]).getCommandSenderName();
					}

					if (args.length == 2)
					{
						OutputHandler.chatConfirmation(sender, String.format("The current limit is %s.", getLimit(target)));
					}
					else
					{
						setLimit(target, parseIntWithMin(sender, args[2], -1));
						OutputHandler.chatConfirmation(sender, String.format("Limit changed to %s.", getLimit(target)));
					}

				}
			}
			else if (args[0].equalsIgnoreCase("limit"))
			{
				OutputHandler.chatConfirmation(sender, String.format("The current limit is %s.", getLimit(sender)));
			}
		}
		TeleportDataManager.pwMap.put(sender.getPersistentID().toString(), map);
		TeleportDataManager.savePWarps(sender.getPersistentID().toString());
	}

	private String getLimit(EntityPlayer sender)
	{
		return APIRegistry.perms.getPermissionProperty(sender, PERMPROP);
	}

	private String getLimit(String target)
	{
		throw new RuntimeException("Not yet implemented!");
//		if (target.startsWith("p:"))
//		{
//			return APIRegistry.perms.getPermissionPropForPlayer(UserIdent.getUuidByUsername(target.replaceFirst("p:", "")), APIRegistry.perms
//					.getGLOBAL().getName(), PERMPROP);
//		}
//		else if (target.startsWith("g:"))
//		{
//			return APIRegistry.perms
//					.getPermissionPropForGroup(target.replaceFirst("g:", ""), APIRegistry.perms.getGlobalZone().getName(), PERMPROP);
//		}
//		else
//		{
//			return "";
//		}
	}

	private void setLimit(String target, int limit)
	{
		throw new RuntimeException("Not yet implemented!");
//		if (target.startsWith("p:"))
//		{
//			APIRegistry.perms.setPlayerPermissionProperty(UserIdent.getUuidByUsername(target.replaceFirst("p:", "")), PERMPROP, "" + limit,
//					APIRegistry.perms.getGlobalZone().getName());
//		}
//		else if (target.startsWith("g:"))
//		{
//			APIRegistry.perms.setGroupPermissionProp(target.replaceFirst("g:", ""), PERMPROP, "" + limit, APIRegistry.perms.getGlobalZone()
//					.getName());
//		}
//		else
//		{
//			return;
//		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getPermissionNode()
	{
		return "fe.teleport." + getCommandName();
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		throw new RuntimeException("Not yet implemented!");
//		if (args.length == 1)
//		{
//			return getListOfStringsMatchingLastWord(args, "goto", "add", "remove", "limit");
//		}
//		if (args.length == 2 && args[0].equalsIgnoreCase("limit"))
//		{
//			Zone zone = sender instanceof EntityPlayer ? APIRegistry.perms.getZoneAt(new WorldPoint((EntityPlayer) sender)) : APIRegistry.perms
//					.getGLOBAL();
//			ArrayList<String> list = new ArrayList<String>();
//			for (String s : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames())
//			{
//				list.add(s);
//			}
//
//			while (zone != null)
//			{
//				for (Group g : APIRegistry.perms.getGroupsInZone(zone.getName()))
//				{
//					list.add(g.name);
//				}
//				zone = APIRegistry.perms.getZone(zone.parent);
//			}
//
//			return getListOfStringsFromIterableMatchingLastWord(args, list);
//		}
//		if (args.length == 2)
//		{
//			if (TeleportDataManager.pwMap.get(sender.getCommandSenderName()) == null)
//			{
//				TeleportDataManager.pwMap.put(sender.getCommandSenderName(), new HashMap<String, PWarp>());
//			}
//			return getListOfStringsFromIterableMatchingLastWord(args, TeleportDataManager.pwMap.get(sender.getCommandSenderName()).keySet());
//		}
//		return null;
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.TRUE;
	}

	public void registerExtraPermissions()
	{
		PermissionsManager.registerPermission(PERMSETLIMIT, RegisteredPermValue.OP);

		APIRegistry.perms.registerPermissionProperty(PERMPROP, "10");
//		APIRegistry.perms.registerPermissionProperty(PERMPROP, 0, GUEST);
//		APIRegistry.perms.registerPermissionProperty(PERMPROP, 10, MEMBER);
//		APIRegistry.perms.registerPermissionProperty(PERMPROP, -1, OP);
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{

		return "/pwarp goto [name] OR <add|remove> <name> Teleports you to a personal warp.";
	}
}
