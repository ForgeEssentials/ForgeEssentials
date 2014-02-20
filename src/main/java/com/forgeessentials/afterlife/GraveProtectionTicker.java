package com.forgeessentials.afterlife;

import java.util.EnumSet;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class GraveProtectionTicker implements IScheduledTickHandler
{
	private Deathchest	deathchest;

	public GraveProtectionTicker(Deathchest deathchest)
	{
		this.deathchest = deathchest;
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (Grave grave : deathchest.gravemap.values())
		{
			grave.tick();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "GraveProtectionTicker";
	}

	@Override
	public int nextTickSpacing()
	{
		return 20;
	}

}
