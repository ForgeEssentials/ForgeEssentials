package com.ForgeEssentials.playerLogger;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.playerLogger.types.commandLog;
import com.ForgeEssentials.playerLogger.types.playerTrackerLog;
import com.ForgeEssentials.util.events.PlayerBlockBreak;
import com.ForgeEssentials.util.events.PlayerBlockPlace;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class EventLogger implements IPlayerTracker
{
	public LogLoop	logLoop;
	public Thread	thread;
	public Side		side	= FMLCommonHandler.instance().getEffectiveSide();

	public EventLogger()
	{
		logLoop = new LogLoop();
		Thread thread = new Thread(logLoop, "ForgeEssentials - MySQL Connection Thread - PlayerLogger");
		thread.start();

		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
	}

	public static boolean				logPlayerChangedDimension	= true;
	public static boolean				logPlayerRespawn			= true;
	public static boolean				logItemUsage				= true;
	public static boolean				logBlockChanges				= true;
	public static boolean				logPlayerLoginLogout		= true;

	public static boolean				logCommands_Player			= true;
	public static boolean				logCommands_Block			= true;
	public static boolean				logCommands_rest			= true;
	public static boolean				BlockChange_WhiteList_Use	= false;
	public static ArrayList<Integer>	BlockChange_WhiteList		= new ArrayList<Integer>();
	public static ArrayList<Integer>	BlockChange_BlackList		= new ArrayList<Integer>();
	public static List<String>			exempt_players 				= new ArrayList<String> ();
	public static List<String>			exempt_groups 				= new ArrayList<String> ();

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (logPlayerLoginLogout && side.isServer())
		{
			if(exempt(player))
				return;
			ModulePlayerLogger.log(new playerTrackerLog(playerTrackerLog.playerTrackerLogCategory.Login, player));
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		if (logPlayerLoginLogout && side.isServer())
		{
			if(exempt(player))
				return;
			ModulePlayerLogger.log(new playerTrackerLog(playerTrackerLog.playerTrackerLogCategory.Logout, player));
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		if (logPlayerChangedDimension && side.isServer())
		{
			if(exempt(player))
				return;
			ModulePlayerLogger.log(new playerTrackerLog(playerTrackerLog.playerTrackerLogCategory.ChangedDim, player));
		}
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		if (logPlayerRespawn && side.isServer())
		{
			if(exempt(player))
				return;
			ModulePlayerLogger.log(new playerTrackerLog(playerTrackerLog.playerTrackerLogCategory.Respawn, player));
		}
	}

	@ForgeSubscribe
	public void command(CommandEvent e)
	{
		if (logCommands_Player && !e.isCanceled() && e.sender instanceof EntityPlayer && side.isServer())
		{
			if(exempt((EntityPlayer)e.sender))
				return;
			ModulePlayerLogger.log(new commandLog(e.sender.getCommandSenderName(), getCommand(e)));
			return;
		}
		if (logCommands_Block && !e.isCanceled() && e.sender instanceof TileEntityCommandBlock && side.isServer())
		{
			ModulePlayerLogger.log(new commandLog(e.sender.getCommandSenderName(), getCommand(e)));
			return;
		}
		if (logCommands_rest && !e.isCanceled() && side.isServer())
		{
			ModulePlayerLogger.log(new commandLog(e.sender.getCommandSenderName(), getCommand(e)));
			return;
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void playerBlockBreak(PlayerBlockBreak e)
	{
		if (logBlockChanges && !e.isCanceled() && side.isServer())
		{
			if(exempt(e.player))
				return;
			String block = e.world.getBlockId(e.blockX, e.blockY, e.blockZ) + ":" + e.world.getBlockMetadata(e.blockX, e.blockY, e.blockZ);
			TileEntity te = e.world.getBlockTileEntity(e.blockX, e.blockY, e.blockZ);
			ModulePlayerLogger.log(new blockChangeLog(blockChangeLog.blockChangeLogCategory.broke, e.player, block, e.blockX, e.blockY, e.blockZ, te));
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void playerBlockPlace(PlayerBlockPlace e)
	{
		if (logBlockChanges && !e.isCanceled() && side.isServer())
		{
			if(exempt(e.player))
				return;
			if (BlockChange_WhiteList_Use && !BlockChange_WhiteList.contains(e.player.dimension))
				return;
			if (BlockChange_BlackList.contains(e.player.dimension) && !BlockChange_WhiteList.contains(e.player.dimension))
				return;

			String block = "";
			if (e.player.inventory.getCurrentItem() != null)
			{
				block = e.player.inventory.getCurrentItem().itemID + ":" + e.player.inventory.getCurrentItem().getItemDamage();
			}
			int x = e.blockX;
			int y = e.blockY;
			int z = e.blockZ;
			switch (e.side)
				{
					case 0:
						y--;
						break;
					case 1:
						y++;
						break;
					case 2:
						z--;
						break;
					case 3:
						z++;
						break;
					case 4:
						x--;
						break;
					case 5:
						x++;
						break;
				}
			ModulePlayerLogger.log(new blockChangeLog(blockChangeLog.blockChangeLogCategory.placed, e.player, block, x, y, z, null));
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

	public static boolean exempt(EntityPlayer player)
	{
		for (String un : exempt_players)
		{
			if (un.replaceAll("\"", "").equalsIgnoreCase(player.username))
				return true;
		}
		for(Group group : PermissionsAPI.getApplicableGroups(player, false))
		{
			if (exempt_groups.contains(group.name))
				return true;	
		}
		return false;
	}
}
