package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandFEPerm extends ForgeEssentialsCommandBase
{
	// Variables for autocomplete
	String[]	args2		=
							{ "user", "group", "export", "promote" };
	String[]	groupargs	=
							{ "prefix", "suffix", "parent", "priority", "allow", "true", "deny", "false", "clear" };
	String[]	playerargs	=
							{ "prefix", "suffix", "set", "add", "remove", "allow", "true", "deny", "false", "clear" };

	@Override
	public final String getCommandName()
	{
		return "feperm";
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("perm");
		list.add("fep");
		list.add("p");
		return list;
	}

	@Override
	public String getCommandSyntax(ICommandSender sender)
	{
		return Localization.get("command.permissions.feperm.syntax");
	}

	@Override
	public String getCommandInfo(ICommandSender sender)
	{
		return Localization.get("command.permissions.feperm.info");
	}

	// ------------------------------------------
	// -------STUFF-THAT-DOESNT-MATTER-----------
	// ------------------------------------------

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return null;
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		// You have to be OP to change the cmd anyways.
		return true;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			OutputHandler.chatConfirmation(sender, "Base usage is /p user|group|default.");
			OutputHandler.chatConfirmation(sender, "Type one of these for more information.");
			return;
		}
		String first = args[0];
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++)
		{
			newArgs[i] = args[i + 1];
		}

		if (first.equalsIgnoreCase("user") || first.equalsIgnoreCase("player"))
		{
			CommandFEPermUser.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("export"))
		{
			CommandExport.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("group"))
		{
			CommandFEPermGroup.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("promote"))
		{
			CommandFEPermPromote.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("default"))
		{
			Zone zone = ZoneManager.getGLOBAL();
			int zoneIndex = -1;
			if (args.length == 2)
			{
				zoneIndex = 1;
			}
			else if (args.length == 4)
			{
				zoneIndex = 3;
			}
			if (zoneIndex != -1)
			{
				if (ZoneManager.doesZoneExist(args[zoneIndex]))
				{
					zone = ZoneManager.getZone(args[zoneIndex]);
				}
				else if (args[2].equalsIgnoreCase("here"))
				{
					zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[zoneIndex]));
					return;
				}
			}
			if (args.length > 2 && args[1].equalsIgnoreCase("set"))
			{
				if (zone.getZoneName() != ZoneManager.getGLOBAL().getZoneName())
				{
					OutputHandler.chatWarning(sender, "Setting the default group outside of _GLOBAL_ zone is not recommended.");
				}
				if (PermissionsAPI.getGroupForName(args[2]) == null)
				{
					OutputHandler.chatError(sender, args[2] + " does not exist as a group in " + zone.getZoneName() + " zone!");
					return;
				}
				PermissionsAPI.setPlayerGroup(args[2], "_ENTRY_PLAYER_", zone.getZoneName());
				sender.sendChatToPlayer("Default group set to " + PermissionsAPI.getGroupForName(args[2]).name + " in zone " + zone.getZoneName());
			}
			else if (args.length > 2 && args[1].equalsIgnoreCase("add"))
			{
				if (PermissionsAPI.getGroupForName(args[2]) == null)
				{
					OutputHandler.chatError(sender, args[2] + " does not exist as a group in " + zone.getZoneName() + " zone!");
					return;
				}
				PermissionsAPI.addPlayerToGroup(args[2], "_ENTRY_PLAYER_", zone.getZoneName());
				OutputHandler.chatConfirmation(sender, "Group " + PermissionsAPI.getGroupForName(args[2]).name + " in zone " + zone.getZoneName() + " added to defaults.");
			}
			else if (args.length == 1)
			{
				ArrayList<Group> groupList = PermissionsAPI.getApplicableGroups("_ENTRY_PLAYER_", false, zone.getZoneName());
				if (groupList.size() == 1)
				{
					OutputHandler.chatConfirmation(sender, "Default group in zone " + zone.getZoneName() + " is currently " + groupList.get(0).name);
				}
				else
				{
					OutputHandler.chatConfirmation(sender, "Default groups in zone " + zone.getZoneName() + " are currently:\n");
					for (Group group : groupList)
					{
						OutputHandler.chatConfirmation(sender, " " + group.name);
					}
				}
				OutputHandler.chatConfirmation(sender, "To change the default groups, type /p default add|set <groupname> [zone]");
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendChatToPlayer("Base usage is /p user|group|default.");
			sender.sendChatToPlayer("Type one of these for more information.");
			return;
		}
		String first = args[0];
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++)
		{
			newArgs[i] = args[i + 1];
		}

		if (first.equalsIgnoreCase("user") || first.equalsIgnoreCase("player"))
		{
			CommandFEPermUser.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("export"))
		{
			CommandExport.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("group"))
		{
			CommandFEPermGroup.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("promote"))
		{
			CommandFEPermPromote.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("default"))
		{
			Zone zone = ZoneManager.getGLOBAL();
			int zoneIndex = -1;
			if (args.length == 2)
			{
				zoneIndex = 1;
			}
			else if (args.length == 4)
			{
				zoneIndex = 3;
			}
			if (zoneIndex != -1)
			{
				if (ZoneManager.doesZoneExist(args[zoneIndex]))
				{
					zone = ZoneManager.getZone(args[zoneIndex]);
				}
				else if (args[2].equalsIgnoreCase("here"))
				{
					sender.sendChatToPlayer("Cannot use \"here\" as console");
				}
				else
				{
					sender.sendChatToPlayer("ERROR: " + Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
					return;
				}
			}
			if (args.length > 2 && args[1].equalsIgnoreCase("set"))
			{
				if (zone.getZoneName() != ZoneManager.getGLOBAL().getZoneName())
				{
					sender.sendChatToPlayer("WARNING: Setting the default group outside of _GLOBAL_ zone is not recommended.");
				}
				if (PermissionsAPI.getGroupForName(args[2]) == null)
				{
					sender.sendChatToPlayer("ERROR: " + args[2] + " does not exist as a group in " + zone.getZoneName() + " zone!");
					return;
				}
				PermissionsAPI.setPlayerGroup(args[2], "_ENTRY_PLAYER_", zone.getZoneName());
				sender.sendChatToPlayer("Default group set to " + PermissionsAPI.getGroupForName(args[2]).name + " in zone " + zone.getZoneName());
			}
			else if (args.length > 2 && args[1].equalsIgnoreCase("add"))
			{
				if (PermissionsAPI.getGroupForName(args[2]) == null)
				{
					sender.sendChatToPlayer("ERROR: " + args[2] + " does not exist as a group in " + zone.getZoneName() + " zone!");
					return;
				}
				PermissionsAPI.addPlayerToGroup(args[2], "_ENTRY_PLAYER_", zone.getZoneName());
				sender.sendChatToPlayer("Group " + PermissionsAPI.getGroupForName(args[2]).name + " in zone " + zone.getZoneName() + " added to defaults.");
			}
			else if (args.length == 1)
			{
				ArrayList<Group> groupList = PermissionsAPI.getApplicableGroups("_ENTRY_PLAYER_", false, zone.getZoneName());
				if (groupList.size() == 1)
				{
					sender.sendChatToPlayer("Default group in zone " + zone.getZoneName() + " is currently " + groupList.get(0).name);
				}
				else
				{
					sender.sendChatToPlayer("Default groups in zone " + zone.getZoneName() + " are currently:\n");
					for (Group group : groupList)
					{
						sender.sendChatToPlayer(" " + group.name);
					}
				}
				sender.sendChatToPlayer("To change the default groups, type /p default add|set <groupname> [zone]");
			}
		}
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.perm";
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		PermResult result = PermissionsAPI.checkPermResult(new PermQueryPlayer(player, getCommandPerm(), true));
		return result.equals(PermResult.DENY) ? false : true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, args2);
		else
		{

		}
		switch (args.length)
			{
				case 1:
					return getListOfStringsMatchingLastWord(args, args2);
				case 2:
					if (args[0].equalsIgnoreCase("group"))
					{
						List<Group> groups = PermissionsAPI.getGroupsInZone(ZoneManager.getGLOBAL().getZoneName());
						ArrayList<String> groupnames = new ArrayList<String>();
						for (int i = 0; i < groups.size(); i++)
						{
							groupnames.add(groups.get(i).name);
						}
						groupnames.add("create");
						return getListOfStringsFromIterableMatchingLastWord(args, groupnames);
					}
					break;
				case 3:
					if (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("player"))
						return getListOfStringsMatchingLastWord(args, playerargs);
					else if (args[0].equalsIgnoreCase("group") && !args[1].equalsIgnoreCase("create"))
						return getListOfStringsMatchingLastWord(args, groupargs);
					break;
			}
		return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
	}

}
