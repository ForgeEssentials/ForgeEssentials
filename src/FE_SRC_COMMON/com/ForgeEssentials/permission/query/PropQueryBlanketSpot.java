package com.ForgeEssentials.permission.query;

import com.ForgeEssentials.api.AreaSelector.WorldPoint;

public class PropQueryBlanketSpot extends PropQuery
{
	public final WorldPoint	spot;

	public PropQueryBlanketSpot(WorldPoint p, String permKey)
	{
		super(permKey);
		spot = p;
	}

}
