package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PropQueryBlanketZone;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandZone extends ForgeEssentialsCommandBase
{
	private static String[]	commands	= { "list", "info", "define", "redefine", "remove", "setParent", "entry", "exit" };

	@Override
	public String getCommandName()
	{
		return "zone";
	}

	@Override
	public List<String> getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("zn");
		return list;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
		ArrayList<Zone> zones = APIRegistry.zones.getZoneList();
		int zonePages = zones.size() / 15 + 1;
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("list"))
			{
				if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".list")))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					OutputHandler.chatConfirmation(sender, Localization.format("command.permissions.zone.list.header", 1, zonePages));
					int itterrator = 0;
					String output;
					for (Zone zone : zones)
					{
						if (itterrator == 15)
						{
							break;
						}
						output = " - " + zone.getZoneName();
						if (zone.isWorldZone())
						{
							output = output + " --> WorldZone";
						}
						OutputHandler.chatConfirmation(sender, output);
					}
				}
				return;
			}
			else
			{
				error(sender);
			}

		}
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("list"))
			{
				if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".list")))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					try
					{
						int page = Integer.parseInt(args[1]);
						if (page <= 0 || page > zonePages)
						{
							OutputHandler.chatConfirmation(sender, Localization.get(Localization.ERROR_NOPAGE));
						}
						else
						{
							OutputHandler.chatConfirmation(sender, Localization.format("command.permissions.zone.list.header", page, zonePages));
							String output;
							Zone zone;
							for (int i = (page - 1) * 15; i < page * 15; i++)
							{
								zone = zones.get(i);
								output = " - " + zone.getZoneName();
								if (zone.isWorldZone())
								{
									output = output + " --> WorldZone";
								}
								OutputHandler.chatConfirmation(sender, output);
							}
						}
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, 1));
					}
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("info"))
			{
				if (args[1].equalsIgnoreCase("here"))
				{
					WorldPoint point = new WorldPoint(sender);
					args[1] = APIRegistry.zones.getWhichZoneIn(point).getZoneName();
				}
				if (!APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else
				{
					if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".info." + args[1])))
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
					}
					else
					{
						Zone zone = APIRegistry.zones.getZone(args[1]);
						PropQueryBlanketZone query1 = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.entry", zone, false);
						PropQueryBlanketZone query2 = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.exit", zone, false);
						APIRegistry.perms.getPermissionProp(query1);
						APIRegistry.perms.getPermissionProp(query2);

						OutputHandler.chatConfirmation(sender, "Name: " + zone.getZoneName());
						OutputHandler.chatConfirmation(sender, "Parent: " + zone.parent);
						OutputHandler.chatConfirmation(sender, "Priority: " + zone.priority);
						OutputHandler.chatConfirmation(sender, "Dimension: " + zone.dim + "     World: " + FunctionHelper.getDimension(zone.dim).provider.getDimensionName());
						ChatUtils.sendMessage(sender, FunctionHelper.formatColors(FEChatFormatCodes.GREEN + "Entry Message: " + FEChatFormatCodes.RESET + query1.getStringValue()));
						ChatUtils.sendMessage(sender, FunctionHelper.formatColors(FEChatFormatCodes.GREEN + "Exit Message: " + FEChatFormatCodes.RESET + query2.getStringValue()));
						Point high = zone.getHighPoint();
						Point low = zone.getLowPoint();
						OutputHandler.chatConfirmation(sender, high.x + ", " + high.y + ", " + high.z + " -> " + low.x + ", " + low.y + ", " + low.z);
					}
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))
			{
				if (!APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else
				{
					if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".remove." + args[1])))
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
					}
					else
					{
						APIRegistry.zones.deleteZone(args[1]);
						OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_REMOVE, args[1]));
					}
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("define"))
			{
				if (APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_YESZONE, args[1]));
				}
				else if (info.getSelection() == null)
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOSELECTION));
					return;
				}
				else if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayerArea(sender, getCommandPerm() + ".define", info.getSelection(), true)))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					APIRegistry.zones.createZone(args[1], info.getSelection(), sender.worldObj);
					OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_DEFINE, args[1]));
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("redefine"))
			{
				if (!APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_YESZONE, args[1]));
				}
				else if (info.getSelection() == null)
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOSELECTION));
					return;
				}
				else if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayerArea(sender, getCommandPerm() + ".redefine." + args[1], info.getSelection(), true)))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					Zone z = APIRegistry.zones.getZone(args[1]);
					z.redefine(info.getPoint1(), info.getPoint2());
					saveZone(z);
					OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_REDEFINE, args[1]));
				}
				return;
			}

		}
		else if (args.length >= 3)
		{
			if (args[0].equalsIgnoreCase("setParent"))
			{
				if (!APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else if (!APIRegistry.zones.doesZoneExist(args[2]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
				else if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".setparent." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					Zone z = APIRegistry.zones.getZone(args[1]);
					z.parent = args[2];
					saveZone(z);
					OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_SETPARENT, args[1], args[2]));
				}
				return;
			}

			if (args[0].equalsIgnoreCase("entry"))
			{
				if (!APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
					return;
				}
				else if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".entry." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					PropQueryBlanketZone query = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.entry", APIRegistry.zones.getZone(args[1]), false);
					APIRegistry.perms.getPermissionProp(query);
					OutputHandler.chatConfirmation(sender, query.getStringValue());

					return;
				}
				else if (args[2].equalsIgnoreCase("remove"))
                {
                    APIRegistry.perms.clearGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, "ForgeEssentials.Permissions.Zone.entry", args[1]);
                    OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Entry Message removed.");
                }
				else
				{
					String tempEntry = "";
					for (int i = 2; i < args.length; i++)
					{
						tempEntry += args[i] + " ";
					}
					APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, "ForgeEssentials.Permissions.Zone.entry", tempEntry, args[1]);

					OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Entry Message set to: " + tempEntry);
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("exit"))
			{
				if (!APIRegistry.zones.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
					return;
				}
				else if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".exit." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					PropQueryBlanketZone query = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.exit", APIRegistry.zones.getZone(args[1]), false);
					APIRegistry.perms.getPermissionProp(query);
					OutputHandler.chatConfirmation(sender, query.getStringValue());

					return;
				}
				else if (args[2].equalsIgnoreCase("remove"))
				{
				    APIRegistry.perms.clearGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, "ForgeEssentials.Permissions.Zone.exit", args[1]);
				    OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Exit Message removed.");
				}
				else
				{
					String tempEntry = "";
					for (int i = 2; i < args.length; i++)
					{
						tempEntry += args[i] + " ";
					}
					APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, "ForgeEssentials.Permissions.Zone.exit", tempEntry, args[1]);

					OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Exit Message set to: " + tempEntry);
					return;
				}
			}

		}
		else
		{
			error(sender);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// no defining zones from the console.
	}

	private void saveZone(Zone z)
	{
		DataStorageManager.getReccomendedDriver().saveObject(ZoneHelper.container, z);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.permissions.zone";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();
		switch (args.length)
			{
				case 0:
				case 1:
					for (String c : commands)
					{
						list.add(c);
					}
					break;
				case 2:
					for (Zone z : APIRegistry.zones.getZoneList())
					{
						list.add(z.getZoneName());
					}
					break;
				case 3:
					if (args[0].equalsIgnoreCase("setparent"))
					{
						for (Zone z : APIRegistry.zones.getZoneList())
						{
							list.add(z.getZoneName());
						}
					}
			}

		return list;
	}

}
