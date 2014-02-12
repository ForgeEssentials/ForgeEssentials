package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.api.permissions.query.PropQueryPlayerZone;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSpawn extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "spawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		Zone zone = APIRegistry.zones.getWorldZone(sender.worldObj);
		if (args.length >= 1)
		{
			if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
				return;
			}
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);

				PropQueryPlayerZone query = new PropQueryPlayerZone(player, CommandSetSpawn.SPAWN_PROP, zone, true);
				APIRegistry.perms.getPermissionProp(query);

				String val = query.getStringValue();
				String[] split = val.split("[;_]");

				int dim = Integer.parseInt(split[0]);
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);

				WarpPoint point = new WarpPoint(dim, x + .5, y + 1, z + .5, player.cameraYaw, player.cameraPitch);

				// teleport
				FunctionHelper.setPlayer(player, point);
				ChatUtils.sendMessage(player, Localization.get("command.spawn.done"));
				return;
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
		else if (args.length == 0)
		{
			PropQueryPlayerZone query = new PropQueryPlayerZone(sender, CommandSetSpawn.SPAWN_PROP, zone, true);
			APIRegistry.perms.getPermissionProp(query);

			String val = query.getStringValue();
			String[] split = val.split("[;_]");

			int dim = Integer.parseInt(split[0]);
			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			int z = Integer.parseInt(split[3]);

			WarpPoint spawn = new WarpPoint(dim, x + .5, y + 1, z + .5, sender.cameraYaw, sender.cameraPitch);
			PlayerInfo.getPlayerInfo(sender.username).back = new WarpPoint(sender);
			TeleportCenter.addToTpQue(spawn, sender);
			ChatUtils.sendMessage(sender, Localization.get("command.spawn.done"));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);

				WarpPoint spawn;
				ChunkCoordinates point = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.getSpawnPoint();
				spawn = new WarpPoint(0, point.posX, point.posY, point.posZ, player.rotationPitch, player.rotationYaw);
				TeleportCenter.addToTpQue(spawn, player);
				ChatUtils.sendMessage(player, Localization.get("command.spawn.done"));
				return;
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}
}
