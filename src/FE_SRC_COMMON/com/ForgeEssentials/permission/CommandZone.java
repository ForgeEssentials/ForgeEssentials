package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PropQueryBlanketZone;
import com.ForgeEssentials.api.permissions.query.PropQueryPlayerZone;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
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
		ArrayList<Zone> zones = ZoneManager.getZoneList();
		int zonePages = zones.size() / 15 + 1;
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("list"))
			{
				if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".list")))
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
				if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".list")))
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
					args[1] = ZoneManager.getWhichZoneIn(point).getZoneName();
				}
				if (!ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else
				{
					if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".info." + args[1])))
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
					}
					else
					{
						Zone zone = ZoneManager.getZone(args[1]);
						PropQueryBlanketZone query1 = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.entry", zone, false);
						PropQueryBlanketZone query2 = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.exit", zone, false);
						PermissionsAPI.getPermissionProp(query1);
						PermissionsAPI.getPermissionProp(query2);
						
						
						OutputHandler.chatConfirmation(sender, "Name: " + zone.getZoneName());
						OutputHandler.chatConfirmation(sender, "Parent: " + zone.parent);
						OutputHandler.chatConfirmation(sender, "Priority: " + zone.priority);
						OutputHandler.chatConfirmation(sender, "Dimension: " + zone.dim + "     World: " + FunctionHelper.getDimension(zone.dim).provider.getDimensionName());
						if (query1.hasValue())
						{
							sender.sendChatToPlayer(FunctionHelper.formatColors(FEChatFormatCodes.GREEN + "Global Entry Message: " + FEChatFormatCodes.RESET + query1.getStringValue()));
						}
						else
						{
							OutputHandler.chatConfirmation(sender, "No Global Entry Message Set.");
						}
						if (query2.hasValue())
						{
							sender.sendChatToPlayer(FunctionHelper.formatColors(FEChatFormatCodes.GREEN + "Global Exit Message: " + FEChatFormatCodes.RESET + query2.getStringValue()));
						}
						else
						{
							OutputHandler.chatConfirmation(sender, "No Global Exit Message Set.");
						}
						Point high = zone.getHighPoint();
						Point low = zone.getLowPoint();
						OutputHandler.chatConfirmation(sender, high.x + ", " + high.y + ", " + high.z + " -> " + low.x + ", " + low.y + ", " + low.z);
					}
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))
			{
				if (!ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else
				{
					if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".remove." + args[1])))
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
					}
					else
					{
						ZoneManager.deleteZone(args[1]);
						OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_REMOVE, args[1]));
					}
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("define"))
			{
				if (ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_YESZONE, args[1]));
				}
				else if (info.getSelection() == null)
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOSELECTION));
					return;
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerArea(sender, getCommandPerm() + ".define", info.getSelection(), true)))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					ZoneManager.createZone(args[1], info.getSelection(), sender.worldObj);
					OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_DEFINE, args[1]));
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("redefine"))
			{
				if (!ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_YESZONE, args[1]));
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".redefine." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else if (info.getSelection() == null)
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOSELECTION));
					return;
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerArea(sender, getCommandPerm() + ".redefine." + args[1], info.getSelection(), true)))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					ZoneManager.getZone(args[1]).redefine(info.getPoint1(), info.getPoint2());
					OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_REDEFINE, args[1]));
				}
				return;
			}

		}
		else if (args.length >= 3)
		{
			if (args[0].equalsIgnoreCase("setParent"))
			{
				if (!ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else if (!ZoneManager.doesZoneExist(args[2]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".setparent." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					ZoneManager.getZone(args[1]).parent = args[2];
					OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_SETPARENT, args[1], args[2]));
				}
				return;
			}

			if (args[0].equalsIgnoreCase("entry"))
			{
				if (!ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
					return;
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".entry." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					PropQueryBlanketZone query = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.entry", ZoneManager.getZone(args[1]), false);
					PermissionsAPI.getPermissionProp(query);
					OutputHandler.chatConfirmation(sender, query.getStringValue());

					return;
				}
				else
				{
					String tempEntry = "";
					for (int i = 2; i < args.length; i++)
						tempEntry += args[i] + " ";
					String tempTest = PermissionsAPI.getDEFAULT().name;

					PermissionsAPI.setGroupPermissionProp(PermissionsAPI.getDEFAULT().name, "ForgeEssentials.Permissions.Zone.entry", tempEntry, args[1]);

					OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Entry Message set to: " + tempEntry);
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("exit"))
			{
				if (!ZoneManager.doesZoneExist(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
					return;
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".exit." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					PropQueryBlanketZone query = new PropQueryBlanketZone("ForgeEssentials.Permissions.Zone.exit", ZoneManager.getZone(args[1]), false);
					PermissionsAPI.getPermissionProp(query);
					OutputHandler.chatConfirmation(sender, query.getStringValue());

					return;
				}
				else
				{
					String tempEntry = "";
					for (int i = 2; i < args.length; i++)
						tempEntry += args[i] + " ";
					String tempTest = PermissionsAPI.getDEFAULT().name;

					PermissionsAPI.setGroupPermissionProp(PermissionsAPI.getDEFAULT().name, "ForgeEssentials.Permissions.Zone.exit", tempEntry, args[1]);

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

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.zone";
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
					for (Zone z : ZoneManager.getZoneList())
					{
						list.add(z.getZoneName());
					}
					break;
				case 3:
					if (args[0].equalsIgnoreCase("setparent"))
					{
						for (Zone z : ZoneManager.getZoneList())
						{
							list.add(z.getZoneName());
						}
					}
			}

		return list;
	}

}
