package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.teleport.TeleportCenter;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSpawn extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName()
	{
		return "spawn";
	}

	public static WarpPoint getPlayerSpawn(EntityPlayerMP player, WorldPoint location)
	{
	    UserIdent ident = new UserIdent(player);
	    if (location == null)
	        location = new WorldPoint(player);
		String spawnProperty = APIRegistry.perms.getPermission(ident, location, null, APIRegistry.perms.getPlayerGroups(ident), FEPermissions.SPAWN, true);
		WorldPoint point = null;
		if (spawnProperty == null)
			return null;
		if (spawnProperty.equalsIgnoreCase("bed"))
		{
			if (player.getBedLocation() != null)
			{
				ChunkCoordinates spawn = player.getBedLocation();
				EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
				point = new WorldPoint(player.dimension, spawn.posX, spawn.posY, spawn.posZ);
			}
		}
		else
		{
			point = WorldPoint.fromString(spawnProperty);
		}
		if (point == null)
			return null;
		return new WarpPoint(point, player.cameraYaw, player.cameraPitch);
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		Zone zone = APIRegistry.perms.getWorldZone(sender.worldObj);
		if (args.length >= 1)
		{
			if (!PermissionsManager.checkPermission(sender, getPermissionNode() + ".others"))
			{
				throw new CommandException(FEPermissions.MSG_NO_COMMAND_PERM);
			}
			EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
			if (player == null)
			{
				throw new CommandException(String.format("Player %s does not exist, or is not online.", args[0]));
			}

			WarpPoint point = getPlayerSpawn(player, null);
			if (point == null)
			{
				throw new CommandException("There is no spawnpoint set for that player.");
			}

			PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
			OutputHandler.chatNotification(player, "Teleporting to spawn.");
			TeleportCenter.teleport(point, player);
		}
		else if (args.length == 0)
		{
			EntityPlayerMP player = (EntityPlayerMP) sender;

			WarpPoint point = getPlayerSpawn(player, null);
			if (point == null)
			{
				throw new CommandException("There is no spawnpoint set for that player.");
			}

			PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
			OutputHandler.chatConfirmation(player, "Teleporting to spawn.");
			TeleportCenter.teleport(point, player);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length < 1)
		{
			throw new CommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
		}

		if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender).setCommand(this), getPermissionNode() + ".others"))
		{
			throw new CommandException(FEPermissions.MSG_NO_COMMAND_PERM);
		}
		EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
		if (player == null)
		{
			throw new CommandException(String.format("Player %s does not exist, or is not online.", args[0]));
		}

		WarpPoint point = getPlayerSpawn(player, null);
		if (point == null)
		{
			throw new CommandException("There is no spawnpoint set for that player.");
		}

		PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
		OutputHandler.chatNotification(player, "Teleporting to spawn.");
		TeleportCenter.teleport(point, player);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getPermissionNode()
	{
		return "fe.teleport.spawn";
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.TRUE;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return "/spawn [player] Teleport you or another player to their spawn point.";
		}
		else
		{
			return "/spawn <player> Teleport a player to their spawn point.";
		}
	}
}
