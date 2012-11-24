package com.ForgeEssentials.permissions;

import java.util.Set;
import java.util.SortedSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandZone extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		// TODO Auto-generated method stub
		return "zone";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender);
		Set<String> set = ZoneManager.zoneMap.keySet();
		int zonePages = (set.size() / 15) + 1;
		switch (args.length)
			{
				case 1:
					{
						if (args[0].equalsIgnoreCase("list"))
						{
							if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.zone.list")))
								OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
							else
							{
								OutputHandler.chatConfirmation(sender, " -- Zones List -- (1/" + zonePages + ")");
								int itterrator = 0;
								for (String zone : set)
								{
									if (itterrator == 15)
										break;
									OutputHandler.chatConfirmation(sender, " -" + zone);
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
							if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.zone.list")))
								OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
							else
							{
								try
								{
									int page = Integer.parseInt(args[1]);
									if (page <= 0 || page > zonePages)
									{
										OutputHandler.chatConfirmation(sender, Localization.get("message.error.nopage"));
									}
									else
									{
										OutputHandler.chatConfirmation(sender, " -- Zones List -- (" + page + "/" + zonePages + ")");
										String[] zones = set.toArray(new String[] {});
										for (int i = (page - 1) * 15; i < page * 15; i++)
											OutputHandler.chatConfirmation(sender, " -" + zones[i]);
									}
								}
								catch (NumberFormatException e)
								{
									OutputHandler.chatError(sender, Localization.get("message.error.nan"));
								}
							}
							return;
						}
						else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))
						{
							if (!ZoneManager.zoneMap.containsKey(args[1]))
								OutputHandler.chatError(sender, Localization.get("message.error.nozone"));
							else
							{
								if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.zone.remove." + args[1])))
									OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
								{
									ZoneManager.deleteZone(args[1]);
									OutputHandler.chatConfirmation(sender, args[1] + " was successfully deleted");
								}
							}
							return;
						}
						else if (args[0].equalsIgnoreCase("define"))
						{
							if (ZoneManager.zoneMap.containsKey(args[1]))
								OutputHandler.chatError(sender, Localization.get("message.error.yeszone"));
							else
							{
								if (info.getSelection() == null)
								{
									OutputHandler.chatError(sender, Localization.get("message.error.noselection"));
									return;
								}
								else
								{
									if (!PermissionsAPI.checkPermAllowed(new PermQueryArea(sender, "ForgeEssentials.zone.define", info.getSelection(), true)))
										OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
									else
									{
										ZoneManager.createZone(args[1], info.getSelection(), sender.worldObj);
										OutputHandler.chatConfirmation(sender, args[1] + " created successfully");
									}
								}
							}
							return;
						}
						else if (args[0].equalsIgnoreCase("redefine"))
						{
							if (!ZoneManager.zoneMap.containsKey(args[1]))
								OutputHandler.chatError(sender, Localization.get("message.error.yeszone"));
							else
							{
								if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.zone.redefine." + args[1])))
									OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
								else
								{
									if (info.getSelection() == null)
									{
										OutputHandler.chatError(sender, Localization.get("message.error.noselection"));
										return;
									}
									else
									{
										if (!PermissionsAPI.checkPermAllowed(new PermQueryArea(sender, "ForgeEssentials.zone.redefine." + args[1], info.getSelection(), true)))
											OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
										else
										{
											ZoneManager.zoneMap.get(args[1]).redefine(info.getPoint1(), info.getPoint2());
											OutputHandler.chatConfirmation(sender, args[1] + "redefined successfully");
										}
									}
								}
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
							if (ZoneManager.zoneMap.containsKey(args[1]))
								OutputHandler.chatError(sender, Localization.get("message.error.nozone"));
							else
							{
								if (ZoneManager.zoneMap.containsKey(args[2]))
									OutputHandler.chatError(sender, Localization.get("message.error.nozone"));
								else
								{
									if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.zone.setparent." + args[1])))
										OutputHandler.chatError(sender, Localization.get("message.error.permdenied"));
									else
									{
										ZoneManager.zoneMap.get(args[1]).setParent(ZoneManager.zoneMap.get(args[2]));
										OutputHandler.chatConfirmation(sender, args[1] + "redefined successfully");
									}
								}
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
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, "ForgeEssentials.zone"));
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Permissions.Zone";
	}

}
