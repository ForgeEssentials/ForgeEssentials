package com.ForgeEssentials.playerLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.customEvents.PlayerBlockBreak;
import com.ForgeEssentials.core.customEvents.PlayerBlockPlace;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.playerLogger.types.commandLog;
import com.ForgeEssentials.playerLogger.types.playerTrackerLog;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

public class EventLogger implements IPlayerTracker
{
	public LogLoop logLoop;
	public Thread thread;

	public EventLogger()
	{
		logLoop = new LogLoop();
		Thread thread = new Thread(logLoop,
				"MySQL Connection Thread - PlayerLogger");
		thread.start();

		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
	}

	public static boolean logPlayerChangedDimension = true;
	public static boolean logPlayerRespawn = true;
	public static boolean logCommands = true;
	public static boolean logItemUsage = true;
	public static boolean logBlockChanges = true;
	public static boolean logPlayerLoginLogout = true;

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (logPlayerLoginLogout)
		{
			new playerTrackerLog(
					playerTrackerLog.playerTrackerLogCategory.Login, player);
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		if (logPlayerLoginLogout)
		{
			new playerTrackerLog(
					playerTrackerLog.playerTrackerLogCategory.Logout, player);
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		if (logPlayerChangedDimension)
		{
			new playerTrackerLog(
					playerTrackerLog.playerTrackerLogCategory.ChangedDim,
					player);
		}
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		if (logPlayerRespawn)
		{
			new playerTrackerLog(
					playerTrackerLog.playerTrackerLogCategory.Respawn, player);
		}
	}

	@ForgeSubscribe
	public void command(CommandEvent e)
	{
		if (logCommands && !e.isCanceled() && e.sender instanceof EntityPlayer)
		{
			new commandLog(e.sender.getCommandSenderName(), getCommand(e));
		}
		if (logCommands && !e.isCanceled()
				&& !(e.sender instanceof EntityPlayer))
		{
			new commandLog(e.sender.getCommandSenderName(), getCommand(e));
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void playerBlockBreak(PlayerBlockBreak e)
	{
		if (logBlockChanges && !e.isCanceled())
		{
			String block = e.world.getBlockId(e.blockX, e.blockY, e.blockZ)
					+ ":"
					+ e.world.getBlockMetadata(e.blockX, e.blockY, e.blockZ);
			new blockChangeLog(blockChangeLog.blockChangeLogCategory.broke,
					e.player, block, e.blockX, e.blockY, e.blockZ);
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void playerBlockPlace(PlayerBlockPlace e)
	{
		if (logBlockChanges && !e.isCanceled())
		{
			String block = "";
			if (e.player.inventory.getCurrentItem() != null)
			{
				block = e.player.inventory.getCurrentItem().itemID + ":"
						+ e.player.inventory.getCurrentItem().getItemDamage();
			}
			new blockChangeLog(blockChangeLog.blockChangeLogCategory.placed,
					e.player, block, e.blockX, e.blockY, e.blockZ);
		}
	}

	/*
	 * Needed background stuff
	 */

	public String getCommand(CommandEvent e)
	{
		String command = "/" + e.command.getCommandName();
		for (String str : e.parameters)
		{
			command = command + " " + str;
		}
		return command;
	}
}
