package com.ForgeEssentials.commands.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Use for all commands that need a cooldown, except for warp systems, see TeleportCenter.
 * 
 * @author Dries007
 * 
 */

public class TickHandlerCommands implements IScheduledTickHandler
{
	/*
	 * For AFK system
	 */
	
	public static List<AFKdata> afkList = new ArrayList<AFKdata>();
	public static List<AFKdata> afkListToAdd = new ArrayList<AFKdata>();
	public static List<AFKdata> afkListToRemove = new ArrayList<AFKdata>();
	
	/*
	 * For kit command
	 */
	public static final String BYPASS_KIT_COOLDOWN = "ForgeEssentials.TickHandlerCommands.BypassKitCooldown";

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			PlayerInfo.getPlayerInfo((EntityPlayer) player).KitCooldownTick();
		}
		
		afkList.addAll(afkListToAdd);
		afkListToAdd.clear();
		for(AFKdata data : afkList)
		{
			data.count();
		}
		afkList.removeAll(afkListToRemove);
		afkListToRemove.clear();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		// Not needed here
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "FE_TickHandlerCommands";
	}

	@Override
	public int nextTickSpacing()
	{
		return 20;
	}
}
