package com.forgeessentials.api.permissions.query;

import com.forgeessentials.util.AreaSelector.WorldPoint;

public class PropQueryBlanketSpot extends PropQuery
{
	public final WorldPoint	spot;

	public PropQueryBlanketSpot(WorldPoint p, String permKey)
	{
		super(permKey);
		spot = p;
	}

}
