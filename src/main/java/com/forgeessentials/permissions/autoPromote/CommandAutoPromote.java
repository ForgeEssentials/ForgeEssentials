package com.forgeessentials.permissions.autoPromote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.permissions.SqlHelper;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.WorldPoint;

public class CommandAutoPromote extends ForgeEssentialsCommandBase {
	@Override
	public String getCommandName()
	{
		return "autopromote";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("ap");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		/*
		 * Get the right zone. If nothing valid is given, defaults to the senders position.
		 */
		Zone zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(sender));
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("world"))
			{
				zone = APIRegistry.permissionManager.getWorldZone(sender.worldObj);
			}
			if (args[0].equalsIgnoreCase("global"))
			{
				zone = APIRegistry.permissionManager.getGlobalZone();
			}
			if (APIRegistry.permissionManager.doesZoneExist(args[0]))
			{
				zone = APIRegistry.permissionManager.getZone(args[0]);
			}
		}

		/*
		 * Need to make a new one?
		 */
		AutoPromote ap = AutoPromoteManager.instance().map.get(zone.getName());
		if (ap == null)
		{
			AutoPromoteManager.instance().map.put(zone.getName(), new AutoPromote(zone.getName(), false));
			ap = AutoPromoteManager.instance().map.get(zone.getName());
		}

		/*
		 * Nope, View the existing one?
		 */
		if (args.length == 0 || args.length == 1 || args[1].equalsIgnoreCase("get"))
		{
			String header = "--- AutoPromote for: " + ap.zone + " ---";
			ChatUtils.sendMessage(sender, header);
			ChatUtils.sendMessage(sender, "Enabled: " + (ap.enable ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + ap.enable);
			ChatUtils.sendMessage(sender, "Promotion times: ");
			for (String i : ap.promoteList.keySet())
			{
				ChatUtils.sendMessage(sender, " " + i + " > " + ap.promoteList.get(i));
			}
			StringBuilder footer = new StringBuilder();
			for (int i = 3; i < header.length(); i++)
			{
				footer.append("-");
			}
			ChatUtils.sendMessage(sender, footer.toString());
			return;
		}

		/*
		 * Nope, Enable?
		 */
		if (args[1].equalsIgnoreCase("enable"))
		{
			if (ap.enable)
			{
				OutputHandler.chatWarning(sender, "AutoPromote for " + ap.zone + " was already enabled.");
			}
			else
			{
				ap.enable = true;
				OutputHandler.chatConfirmation(sender, "AutoPromote for " + ap.zone + " enabled.");
			}
		}

		/*
		 * Nope, Disable?
		 */
		if (args[1].equalsIgnoreCase("disable"))
		{
			if (!ap.enable)
			{
				OutputHandler.chatWarning(sender, "AutoPromote for " + ap.zone + " was already disabled.");
			}
			else
			{
				ap.enable = false;
				OutputHandler.chatConfirmation(sender, "AutoPromote for " + ap.zone + " disabled.");
			}
		}

		/*
		 * Nope, Edit?
		 */
		if (args[1].equalsIgnoreCase("edit"))
		{
			if (args.length == 2)
			{
				OutputHandler.chatError(sender, "Available options: 'del', 'add'");
			}
			else if (args[2].equalsIgnoreCase("del") || args[2].equalsIgnoreCase("delete"))
			{
				if (args.length == 4)
				{
					int i = parseInt(sender, args[3]);
					if (ap.promoteList.containsKey(i))
					{
						String group = ap.promoteList.remove(i);
						OutputHandler.chatConfirmation(sender, "You have removed " + i + ":" + group + " from the list.");
					}
					else
					{
						OutputHandler.chatError(sender, args[3] + " is not a number in the list.");
					}
				}
				else
				{
					OutputHandler.chatError(sender, "You have to specify a number to remvove from the list.");
				}
			}
			else if (args[2].equalsIgnoreCase("add"))
			{
				if (args.length == 5)
				{
					int i = parseInt(sender, args[3]);
					if (!ap.promoteList.containsKey(i))
					{
						Group group = SqlHelper.getInstance().getGroupByName(args[4]);
						if (group != null)
						{
							ap.promoteList.put(i + "", group.name);
							OutputHandler.chatConfirmation(sender, "You have added " + i + ":" + group.name + " to the list.");
						}
						else
						{
							OutputHandler.chatError(sender, args[4] + " is not a valid group in " + zone.getName() + ".");
						}
					}
					else
					{
						OutputHandler.chatError(sender, args[3] + " is already on the list.");
					}
				}
				else
				{
					OutputHandler.chatError(sender, "You have to specify a number and group to add to the list. (... add <time> <group>)");
				}
			}
		}

		/*
		 * Nope, Message?
		 */

		if (args[1].equalsIgnoreCase("message"))
		{
			if (args.length == 2 || args[2].equalsIgnoreCase("get"))
			{
				OutputHandler.chatConfirmation(sender, "Current message:");
				ChatUtils.sendMessage(sender, FunctionHelper.formatColors(ap.msg));
			}
			else if (args[2].equalsIgnoreCase("set"))
			{
				String newMsg = "";
				for (int i = 3; i < args.length; i++)
				{
					newMsg = newMsg + args[i] + " ";
				}
				ap.msg = newMsg.trim();
				OutputHandler.chatConfirmation(sender, "New message:");
				ChatUtils.sendMessage(sender, FunctionHelper.formatColors(ap.msg));
			}
			else if (args[2].equalsIgnoreCase("enable"))
			{
				ap.sendMsg = true;
				OutputHandler.chatConfirmation(sender, "You enabled the promote message.");
			}
			else if (args[2].equalsIgnoreCase("disable"))
			{
				ap.sendMsg = false;
				OutputHandler.chatConfirmation(sender, "You disabled the promote message.");
			}
		}

		AutoPromoteManager.save(ap);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getPermissionNode()
	{
		return "fe.perm.autoPromote";
	}

	private List<String> getZoneNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("here");
		list.add("global");
		list.add("world");
		for (Zone zone : APIRegistry.permissionManager.getZoneList())
		{
			list.add(zone.getName());
		}
		return list;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, getZoneNames());
		}
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, "get", "enable", "disable", "edit", "message");
		}
		// Sub of edit
		if (args.length == 3 && args[1].equalsIgnoreCase("edit"))
		{
			return getListOfStringsMatchingLastWord(args, "add", "del");
		}
		// Sub of edit and del
		if (args.length == 4 && args[1].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("del"))
		{
			try
			{
				Zone zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint((Entity) sender));
				if (args[0].equalsIgnoreCase("world"))
				{
					zone = APIRegistry.permissionManager.getWorldZone(((Entity) sender).worldObj);
				}
				if (args[0].equalsIgnoreCase("global"))
				{
					zone = APIRegistry.permissionManager.getGlobalZone();
				}
				if (APIRegistry.permissionManager.doesZoneExist(args[0]))
				{
					zone = APIRegistry.permissionManager.getZone(args[0]);
				}
				AutoPromote ap = AutoPromoteManager.instance().map.get(zone.getName());
				if (ap == null)
				{
					AutoPromoteManager.instance().map.put(zone.getName(), new AutoPromote(zone.getName(), false));
					ap = AutoPromoteManager.instance().map.get(zone.getName());
				}
				return getListOfStringsFromIterableMatchingLastWord(args, ap.getList());
			}
			catch (Exception e)
			{
			}
		}
		// Sub of edit and add
		if (args.length == 5 && args[1].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("add"))
		{
			try
			{
				Zone zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint((Entity) sender));
				if (args[0].equalsIgnoreCase("world"))
				{
					zone = APIRegistry.permissionManager.getWorldZone(((Entity) sender).worldObj);
				}
				if (args[0].equalsIgnoreCase("global"))
				{
					zone = APIRegistry.permissionManager.getGlobalZone();
				}
				if (APIRegistry.permissionManager.doesZoneExist(args[0]))
				{
					zone = APIRegistry.permissionManager.getZone(args[0]);
				}
				List<Group> groups = SqlHelper.getInstance().getGroups();
				List<String> groupNames = new ArrayList<String>();
				for (Group group : groups)
				{
					groupNames.add(group.name);
					ChatUtils.sendMessage(sender, group.name);
				}
				return getListOfStringsFromIterableMatchingLastWord(args, groupNames);
			}
			catch (Exception e)
			{
			}
		}
		// Sub of message
		if (args.length == 3 && args[1].equalsIgnoreCase("message"))
		{
			return getListOfStringsMatchingLastWord(args, "get", "set", "enable", "disable");
		}
		return null;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{

		return "/autopromote <zone> [get|enable|disable|edit|add|message] [other options] Configure auto promotion.";
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{

		return RegisteredPermValue.OP;
	}

}
