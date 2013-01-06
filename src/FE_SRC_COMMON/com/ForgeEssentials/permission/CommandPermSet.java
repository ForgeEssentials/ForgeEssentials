package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandPermSet extends CommandFEPerm
{

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		Zone zone = ZoneManager.GLOBAL;

		switch (args.length)
		{
		case 4:
			if (!ZoneManager.doesZoneExist(args[2]))
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ZONE_NOZONE, args[3]));
				return;
			}
			zone = ZoneManager.getZone(args[3]);

			break;
		case 3:
			// check allow/deny part.
			Result result = parseAllow(args[1]);
			if (result.equals(Result.DEFAULT))
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ILLEGAL_STATE, args[1]));
				return;
			}

			// check Groups.
			String[] entities = args[2].split(":");
			if (entities.length != 2)
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ILLEGAL_ENTITY, args[2]));
			}

			if (entities[0].equalsIgnoreCase("g"))
			{
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender,
						getCommandPerm() + ".group." + entities[2] + "."
								+ args[1])))
				{
					PermissionsAPI.setGroupPermission(entities[2], args[1],
							result.equals(Result.ALLOW), zone.getZoneID());
				} else
				{
					OutputHandler.chatError(sender,
							Localization.get(Localization.ERROR_PERMDENIED));
				}
			} else if (entities[0].equalsIgnoreCase("p"))
			{
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender,
						getCommandPerm() + ".player." + entities[2] + "."
								+ args[1])))
				{
					PermissionsAPI.setPlayerPermission(entities[2], args[1],
							result.equals(Result.ALLOW), zone.getZoneID());
				} else
				{
					OutputHandler.chatError(sender,
							Localization.get(Localization.ERROR_PERMDENIED));
				}
			} else
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ILLEGAL_ENTITY, args[2]));
			}
		default:
			this.error(sender);
			return;
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		Zone zone = ZoneManager.GLOBAL;

		switch (args.length)
		{
		case 4:
			if (!ZoneManager.doesZoneExist(args[2]))
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ZONE_NOZONE, args[3]));
				return;
			}
			zone = ZoneManager.getZone(args[3]);

			break;
		case 3:
			// check allow/deny part.
			Result result = parseAllow(args[1]);
			if (result.equals(Result.DEFAULT))
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ILLEGAL_STATE, args[1]));
				return;
			}

			// check Groups.
			String[] entities = args[2].split(":");
			if (entities.length != 2)
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ILLEGAL_ENTITY, args[2]));
			}

			if (entities[0].equalsIgnoreCase("g"))
			{
				PermissionsAPI.setGroupPermission(entities[2], args[1],
						result.equals(Result.ALLOW), zone.getZoneID());
			} else if (entities[0].equalsIgnoreCase("p"))
			{
				PermissionsAPI.setPlayerPermission(entities[2], args[1],
						result.equals(Result.ALLOW), zone.getZoneID());
			} else
			{
				sender.sendChatToPlayer(Localization.format(
						Localization.ERROR_ILLEGAL_ENTITY, args[2]));
			}
		default:
			this.error(sender);
			return;
		}
	}

	private Result parseAllow(String value)
	{
		if (value.equalsIgnoreCase("allow")
				|| value.equalsIgnoreCase("allowed")
				|| value.equalsIgnoreCase("true"))
		{
			return Result.ALLOW;
		} else if (value.equalsIgnoreCase("deny")
				|| value.equalsIgnoreCase("denied")
				|| value.equalsIgnoreCase("false"))
		{
			return Result.DENY;
		} else
		{
			return Result.DEFAULT;
		}
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.permissions.set";
	}
}
