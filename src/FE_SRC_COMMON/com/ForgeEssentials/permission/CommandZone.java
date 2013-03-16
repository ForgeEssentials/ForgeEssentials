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
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandZone extends ForgeEssentialsCommandBase
{
	private static String[]	commands	= { "list", "info", "define", "redefine", "remove", "delete", "setParent" };

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
		switch (args.length)
			{
				case 1:
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

						// 1 arg not list?
						break;
					}
				case 2:
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
									OutputHandler.chatConfirmation(sender, "Name: " + zone.getZoneName());
									OutputHandler.chatConfirmation(sender, "Parent: " + zone.parent);
									OutputHandler.chatConfirmation(sender, "Priority: " + zone.priority);
									OutputHandler.chatConfirmation(sender, "Dimension: " + zone.dim + "     World: " + FunctionHelper.getDimension(zone.dim).provider.getDimensionName());
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

						// 2 args and none of those?
						break;
					}
				case 3:
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
						// 3args and not setParent?
						break;
					}
			}

		error(sender);
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
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
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
