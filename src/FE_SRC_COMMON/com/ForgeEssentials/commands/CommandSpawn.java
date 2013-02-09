package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSpawn extends ForgeEssentialsCommandBase
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point>	spawnPoints	= new HashMap<Integer, Point>();

	@Override
	public String getCommandName()
	{
		return "spawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
				return;
			}
			EntityPlayer player = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				player = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (player != null)
			{
				// NBTTagCompound spawn = DataStorage.getData("spawn");
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);

				WarpPoint spawn;
				ChunkCoordinates point = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.getSpawnPoint();
				spawn = new WarpPoint(0, point.posX, point.posY, point.posZ, sender.rotationPitch, sender.rotationYaw);
				TeleportCenter.addToTpQue(spawn, player);
				player.sendChatToPlayer(Localization.get(Localization.SPAWNED));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
		if (args.length == 0)
		{
			// NBTTagCompound data = DataStorage.getData("spawn");
			WarpPoint spawn;
			// if(!(data == null))
			// {
			// spawn = new WarpPoint(data.getInteger("dim"),
			// data.getDouble("x"), data.getDouble("y"),
			// data.getDouble("z"), data.getFloat("pitch"),
			// data.getFloat("yaw"));
			// }
			// else
			// {
			ChunkCoordinates point = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.getSpawnPoint();
			spawn = new WarpPoint(0, point.posX, point.posY, point.posZ, sender.rotationPitch, sender.rotationYaw);
			// }
			// if (spawn != null)
			// {
			PlayerInfo.getPlayerInfo(sender.username).back = new WarpPoint(sender);
			TeleportCenter.addToTpQue(spawn, sender);
			sender.sendChatToPlayer(Localization.get(Localization.SPAWNED));
			// }
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer player = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				player = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (player != null)
			{
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
				ChunkCoordinates spawnpoint = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.getSpawnPoint();
				WarpPoint spawn = new WarpPoint(player.dimension, spawnpoint.posX, spawnpoint.posY, spawnpoint.posZ, player.rotationPitch, player.rotationYaw);
				TeleportCenter.addToTpQue(spawn, player);
				player.sendChatToPlayer(Localization.get(Localization.SPAWNED));
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
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
}
