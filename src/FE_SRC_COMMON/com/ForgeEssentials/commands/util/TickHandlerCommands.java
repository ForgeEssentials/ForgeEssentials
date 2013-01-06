package com.ForgeEssentials.commands.util;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Use for all commands that need a cooldown, except for warp systems, see
 * TeleportCenter.
 * 
 * @author Dries007
 * 
 */

public class TickHandlerCommands implements IScheduledTickHandler
{
	/*
	 * For kit command
	 */
	public static final String BYPASS_KIT_COOLDOWN = "ForgeEssentials.TickHandlerCommands.BypassKitCooldown";

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (Object player : FMLCommonHandler.instance()
				.getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			PlayerInfo.getPlayerInfo((EntityPlayer) player).KitCooldownTick();
		}
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
		return "TickHandlerCommands";
	}

	@Override
	public int nextTickSpacing()
	{
		return 20;
	}
}
