package com.ForgeEssentials.permission.autoPromote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandAutoPromote extends ForgeEssentialsCommandBase
{
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
		 * Get the right zone.
		 * If nothing valid is given, defaults to the senders position.
		 */
		Zone zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("world"))
			{
				zone = ZoneManager.getWorldZone(sender.worldObj);
			}
			if (args[0].equalsIgnoreCase("global"))
			{
				zone = ZoneManager.getGLOBAL();
			}
			if (ZoneManager.doesZoneExist(args[0]))
			{
				zone = ZoneManager.getZone(args[0]);
			}
		}

		/*
		 * Need to make a new one?
		 */
		AutoPromote ap = AutoPromoteManager.instance().map.get(zone.getZoneName());
		if (ap == null)
		{
			AutoPromoteManager.instance().map.put(zone.getZoneName(), new AutoPromote(zone.getZoneName(), false));
			ap = AutoPromoteManager.instance().map.get(zone.getZoneName());
		}

		/*
		 * Nope, View the existing one?
		 */
		if (args.length == 0 || args.length == 1 || args[1].equalsIgnoreCase("get"))
		{
			String header = "--- AutoPromote for: " + ap.zone + " ---";
			sender.sendChatToPlayer(header);
			sender.sendChatToPlayer("Enabled: " + (ap.enable ? FEChatFormatCodes.GREEN : FEChatFormatCodes.RED) + ap.enable);
			sender.sendChatToPlayer("Promotion times: ");
			for (String i : ap.promoteList.keySet())
			{
				sender.sendChatToPlayer(" " + i + " > " + ap.promoteList.get(i));
			}
			StringBuilder footer = new StringBuilder();
			for (int i = 3; i < header.length(); i++)
			{
				footer.append("-");
			}
			sender.sendChatToPlayer(footer.toString());
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
						Group group = getGroup(args[4], zone);
						if (group != null)
						{
							ap.promoteList.put(i + "", group.name);
							OutputHandler.chatConfirmation(sender, "You have added " + i + ":" + group.name + " to the list.");
						}
						else
						{
							OutputHandler.chatError(sender, args[4] + " is not a valid group in " + zone.getZoneName() + ".");
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
				sender.sendChatToPlayer(FunctionHelper.formatColors(ap.msg));
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
				sender.sendChatToPlayer(FunctionHelper.formatColors(ap.msg));
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

	private Group getGroup(String groupName, Zone zone)
	{
		ArrayList<Group> groups = SqlHelper.getGroupsInZone(zone.getZoneName());
		for (Group group : groups)
		{
			if (group.name.equalsIgnoreCase(groupName))
				return group;
		}
		return null;
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
	public String getCommandPerm()
	{
		return "ForgeEssentials.autoPromote";
	}

	private List<String> getZoneNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("here");
		list.add("global");
		list.add("world");
		for (Zone zone : ZoneManager.getZoneList())
		{
			list.add(zone.getZoneName());
		}
		return list;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, getZoneNames());
		if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, "get", "enable", "disable", "edit", "message");
		// Sub of edit
		if (args.length == 3 && args[1].equalsIgnoreCase("edit"))
			return getListOfStringsMatchingLastWord(args, "add", "del");
		// Sub of edit and del
		if (args.length == 4 && args[1].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("del"))
		{
			try
			{
				Zone zone = ZoneManager.getWhichZoneIn(new WorldPoint((Entity) sender));
				if (args[0].equalsIgnoreCase("world"))
				{
					zone = ZoneManager.getWorldZone(((Entity) sender).worldObj);
				}
				if (args[0].equalsIgnoreCase("global"))
				{
					zone = ZoneManager.getGLOBAL();
				}
				if (ZoneManager.doesZoneExist(args[0]))
				{
					zone = ZoneManager.getZone(args[0]);
				}
				AutoPromote ap = AutoPromoteManager.instance().map.get(zone.getZoneName());
				if (ap == null)
				{
					AutoPromoteManager.instance().map.put(zone.getZoneName(), new AutoPromote(zone.getZoneName(), false));
					ap = AutoPromoteManager.instance().map.get(zone.getZoneName());
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
				Zone zone = ZoneManager.getWhichZoneIn(new WorldPoint((Entity) sender));
				if (args[0].equalsIgnoreCase("world"))
				{
					zone = ZoneManager.getWorldZone(((Entity) sender).worldObj);
				}
				if (args[0].equalsIgnoreCase("global"))
				{
					zone = ZoneManager.getGLOBAL();
				}
				if (ZoneManager.doesZoneExist(args[0]))
				{
					zone = ZoneManager.getZone(args[0]);
				}
				ArrayList<Group> groups = SqlHelper.getGroupsInZone(zone.getZoneName());
				ArrayList<String> groupNames = new ArrayList<String>();
				for (Group group : groups)
				{
					groupNames.add(group.name);
					sender.sendChatToPlayer(group.name);
				}
				return getListOfStringsFromIterableMatchingLastWord(args, groupNames);
			}
			catch (Exception e)
			{
			}
		}
		// Sub of message
		if (args.length == 3 && args[1].equalsIgnoreCase("message"))
			return getListOfStringsMatchingLastWord(args, "get", "set", "enable", "disable");
		return null;
	}

}
