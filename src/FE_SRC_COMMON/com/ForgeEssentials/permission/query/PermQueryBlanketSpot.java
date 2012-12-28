package com.ForgeEssentials.permission.query;

import com.ForgeEssentials.permission.PermissionChecker;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class PermQueryBlanketSpot extends PermQuery
{
	public WorldPoint spot;
	
	/**
	 * Assumes the Players position as the "doneTo" point.
	 * @param player
	 * @param permission
	 */
	public PermQueryBlanketSpot(WorldPoint spot, String permission)
	{
		this.spot = spot;
		checker = new PermissionChecker(permission);
	}
}
