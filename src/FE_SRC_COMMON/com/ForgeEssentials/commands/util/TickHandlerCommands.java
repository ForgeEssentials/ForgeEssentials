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
 * Use for all commands that need a cooldown, except for warp systems, see
 * TeleportCenter.
 * @author Dries007
 */

public class TickHandlerCommands implements IScheduledTickHandler
{
	/*
	 * For AFK system
	 */

	public static List<AFKdata>	afkList				= new ArrayList<AFKdata>();
	public static List<AFKdata>	afkListToAdd		= new ArrayList<AFKdata>();
	public static List<AFKdata>	afkListToRemove		= new ArrayList<AFKdata>();

	/*
	 * For kit command
	 */
	public static final String	BYPASS_KIT_COOLDOWN	= "ForgeEssentials.TickHandlerCommands.BypassKitCooldown";

	/*
	 * For TPA system
	 */

	public static List<TPAdata>	tpaList				= new ArrayList<TPAdata>();
	public static List<TPAdata>	tpaListToAdd		= new ArrayList<TPAdata>();
	public static List<TPAdata>	tpaListToRemove		= new ArrayList<TPAdata>();

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		/*
		 * Kit system
		 */
		for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			PlayerInfo.getPlayerInfo(((EntityPlayer) player).username).KitCooldownTick();
		}

		/*
		 * AFK system
		 */
		try
		{
			afkList.addAll(afkListToAdd);
			afkListToAdd.clear();
			for (AFKdata data : afkList)
			{
				data.count();
			}
			afkList.removeAll(afkListToRemove);
			afkListToRemove.clear();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		/*
		 * TPA system
		 */
		try
		{
			tpaList.addAll(tpaListToAdd);
			tpaListToAdd.clear();
			for (TPAdata data : tpaList)
			{
				data.count();
			}
			tpaList.removeAll(tpaListToRemove);
			tpaListToRemove.clear();
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		return "FE_TickHandlerCommands";
	}

	@Override
	public int nextTickSpacing()
	{
		return 20;
	}
}
