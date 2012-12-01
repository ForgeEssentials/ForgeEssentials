package com.ForgeEssentials.commands;

import java.util.HashMap;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSpawn extends ForgeEssentialsCommandBase
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

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
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (player != null)
			{
				ChunkCoordinates spawn = player.getBedLocation();
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
				player.sendChatToPlayer(Localization.get("message.spawned"));
			} else
				sender.sendChatToPlayer(Localization.get("message.error.noplayer"));
		} else
		{
			ChunkCoordinates spawn = sender.getBedLocation();
			if (spawn != null)
			{
				((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, sender.rotationYaw, sender.rotationPitch);
				sender.sendChatToPlayer(Localization.get("message.spawned"));
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (player != null)
			{
				ChunkCoordinates spawn = player.getBedLocation();
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
				player.sendChatToPlayer(Localization.get("message.spawned"));
			} else
				sender.sendChatToPlayer(Localization.get("message.error.noplayer"));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
