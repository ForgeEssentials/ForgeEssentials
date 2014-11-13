package com.forgeessentials.permissions.autoPromote;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.WorldPoint;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		Zone zone = APIRegistry.perms.getZoneAt(new WorldPoint(sender));
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("world"))
			{
				zone = APIRegistry.perms.getWorldZone(sender.worldObj);
			}
			if (args[0].equalsIgnoreCase("global"))
			{
				zone = APIRegistry.perms.getServerZone();
			}
			// TODO: Identify zones by unique names
			// if (APIRegistry.perms.doesZoneExist(args[0]))
			// {
			// zone = APIRegistry.perms.getZone(args[0]);
			// }
		}

		/*
		 * Need to make a new one?
		 */
		AutoPromote ap = AutoPromoteManager.instance().map.get(zone.toString());
		if (ap == null)
		{
			AutoPromoteManager.instance().map.put(zone.getId(), new AutoPromote(zone.getId(), false));
			ap = AutoPromoteManager.instance().map.get(zone.toString());
		}

		/*
		 * Nope, View the existing one?
		 */
		if (args.length == 0 || args.length == 1 || args[1].equalsIgnoreCase("get"))
		{
			String header = "--- AutoPromote for: " + ap.getZone() + " ---";
			OutputHandler.chatNotification(sender, header);
			OutputHandler.chatNotification(sender, "Enabled: " + (ap.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + ap.isEnabled());
			OutputHandler.chatNotification(sender, "Promotion times: ");
			for (String i : ap.getPromoteList().keySet())
			{
				OutputHandler.chatNotification(sender, " " + i + " > " + ap.getPromoteList().get(i));
			}
			StringBuilder footer = new StringBuilder();
			for (int i = 3; i < header.length(); i++)
			{
				footer.append("-");
			}
			OutputHandler.chatNotification(sender, footer.toString());
			return;
		}

		/*
		 * Nope, Enable?
		 */
		if (args[1].equalsIgnoreCase("enable"))
		{
			if (ap.isEnabled())
			{
				OutputHandler.chatWarning(sender, "AutoPromote for " + ap.getZone() + " was already enabled.");
			}
			else
			{
				ap.setEnabled(true);
				OutputHandler.chatConfirmation(sender, "AutoPromote for " + ap.getZone() + " enabled.");
			}
		}

		/*
		 * Nope, Disable?
		 */
		if (args[1].equalsIgnoreCase("disable"))
		{
			if (!ap.isEnabled())
			{
				OutputHandler.chatWarning(sender, "AutoPromote for " + ap.getZone() + " was already disabled.");
			}
			else
			{
				ap.setEnabled(false);
				OutputHandler.chatConfirmation(sender, "AutoPromote for " + ap.getZone() + " disabled.");
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
					if (ap.getPromoteList().containsKey(i))
					{
						String group = ap.getPromoteList().remove(i);
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
					if (!ap.getPromoteList().containsKey(i))
					{
						String group = args[4];
						if (!APIRegistry.perms.groupExists(group))
						{
							ap.getPromoteList().put(i + "", group);
							OutputHandler.chatConfirmation(sender, "You have added " + i + ":" + group + " to the list.");
						}
						else
						{
							OutputHandler.chatError(sender, args[4] + " is not a valid group in " + zone.toString() + ".");
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
				OutputHandler.sendMessage(sender, FunctionHelper.formatColors(ap.getMsg()));
			}
			else if (args[2].equalsIgnoreCase("set"))
			{
				String newMsg = "";
				for (int i = 3; i < args.length; i++)
				{
					newMsg = newMsg + args[i] + " ";
				}
				ap.setMsg(newMsg.trim());
				OutputHandler.chatConfirmation(sender, "New message:");
				OutputHandler.sendMessage(sender, FunctionHelper.formatColors(ap.getMsg()));
			}
			else if (args[2].equalsIgnoreCase("enable"))
			{
				ap.setSendMsg(true);
				OutputHandler.chatConfirmation(sender, "You enabled the promote message.");
			}
			else if (args[2].equalsIgnoreCase("disable"))
			{
				ap.setSendMsg(false);
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
		for (WorldZone world : APIRegistry.perms.getServerZone().getWorldZones().values())
		{
			for (Zone zone : world.getAreaZones())
			{
				list.add(zone.toString());
			}
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
				Zone zone = APIRegistry.perms.getZoneAt(new WorldPoint((Entity) sender));
				if (args[0].equalsIgnoreCase("world"))
				{
					zone = APIRegistry.perms.getWorldZone(((Entity) sender).worldObj);
				}
				if (args[0].equalsIgnoreCase("global"))
				{
					zone = APIRegistry.perms.getServerZone();
				}
				// TODO: Identify zones by unique names
				// if (APIRegistry.perms.doesZoneExist(args[0]))
				// {
				// zone = APIRegistry.perms.getZone(args[0]);
				// }
				AutoPromote ap = AutoPromoteManager.instance().map.get(zone.toString());
				if (ap == null)
				{
					AutoPromoteManager.instance().map.put(zone.getId(), new AutoPromote(zone.getId(), false));
					ap = AutoPromoteManager.instance().map.get(zone.toString());
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
				Zone zone = APIRegistry.perms.getZoneAt(new WorldPoint((Entity) sender));
				if (args[0].equalsIgnoreCase("world"))
				{
					zone = APIRegistry.perms.getWorldZone(((Entity) sender).worldObj);
				}
				if (args[0].equalsIgnoreCase("global"))
				{
					zone = APIRegistry.perms.getServerZone();
				}
				return getListOfStringsFromIterableMatchingLastWord(args, APIRegistry.perms.getServerZone().getGroups());
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
