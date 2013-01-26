package com.ForgeEssentials.api.permissions.query;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;

import net.minecraft.world.World;

public class PermQueryBlanketZone extends PermQuery
{
	public Zone toCheck;

	public PermQueryBlanketZone(String permission, String zoneID)
	{
		toCheck = ZoneManager.getZone(zoneID);
		checkForward = false;
	}

	public PermQueryBlanketZone(String permission, String zoneID, boolean checkForward)
	{
		toCheck = ZoneManager.getZone(zoneID);
		this.checkForward = checkForward;
	}

	public PermQueryBlanketZone(String permission, Zone zone)
	{
		toCheck = zone;
		checkForward = false;
	}

	public PermQueryBlanketZone(String permission, Zone zone, boolean checkForward)
	{
		toCheck = zone;
		this.checkForward = checkForward;
	}

	/**
	 * uses the WorldZone for the specified world
	 * 
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
		toCheck = ZoneManager.getGLOBAL();
	}
}
