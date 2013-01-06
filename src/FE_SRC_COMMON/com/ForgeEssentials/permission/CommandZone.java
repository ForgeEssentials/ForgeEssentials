package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.permission.query.PermQueryPlayerArea;
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
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("zn");
		return list;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender);
		Set<String> set = ZoneManager.zoneMap.keySet();
		int zonePages = set.size() / 15 + 1;
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
					for (String zone : set)
					{
						if (itterrator == 15)
						{
							break;
						}
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
							String[] zones = set.toArray(new String[] {});
							for (int i = (page - 1) * 15; i < page * 15; i++)
							{
								OutputHandler.chatConfirmation(sender, " -" + zones[i]);
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
			else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))
			{
				if (!ZoneManager.zoneMap.containsKey(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else
				{
					if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".remove." + args[1])))
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
					}
					{
						ZoneManager.deleteZone(args[1]);
						OutputHandler.chatConfirmation(sender, Localization.format(Localization.CONFIRM_ZONE_REMOVE, args[1]));
					}
				}
				return;
			}
			else if (args[0].equalsIgnoreCase("define"))
			{
				if (ZoneManager.zoneMap.containsKey(args[1]))
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
				if (!ZoneManager.zoneMap.containsKey(args[1]))
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
				else if (!PermissionsAPI
						.checkPermAllowed(new PermQueryPlayerArea(sender, getCommandPerm() + ".redefine." + args[1], info.getSelection(), true)))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					ZoneManager.zoneMap.get(args[1]).redefine(info.getPoint1(), info.getPoint2());
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
				if (!ZoneManager.zoneMap.containsKey(args[1]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[1]));
				}
				else if (!ZoneManager.zoneMap.containsKey(args[2]))
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_ZONE_NOZONE, args[2]));
				}
				else if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".setparent." + args[1])))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
				else
				{
					ZoneManager.zoneMap.get(args[1]).parent = args[2];
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

}
