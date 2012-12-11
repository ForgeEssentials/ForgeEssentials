package com.ForgeEssentials.permission.query;

import net.minecraft.src.World;

import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;

public class PermQueryBlanketZone extends PermQuery
{
	public Zone toCheck;

	public PermQueryBlanketZone(String permission, String zoneID)
	{
		toCheck = ZoneManager.getZone(zoneID);
	}
	
	public PermQueryBlanketZone(String permission, Zone zone)
	{
		toCheck = zone;
	}
	
	/**
	 * uses the WorldZone for the specified world
	 * @param world
	 */
	public PermQueryBlanketZone(String permission, World world)
	{
		toCheck = ZoneManager.getWorldZone(world);
	}
	
	/**
	 * Assumes GLOBAL as the zone
	 */
	public PermQueryBlanketZone(String permission)
	{
		toCheck = ZoneManager.GLOBAL;
	}
}
