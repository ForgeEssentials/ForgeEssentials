package com.ForgeEssentials.api.permissions.query;

import com.ForgeEssentials.api.permissions.Zone;

public class PropQueryBlanketZone extends PropQuery
{
	public boolean	checkParents;
	public Zone		zone;

	public PropQueryBlanketZone(String permKey, Zone zone, boolean checkParents)
	{
		super(permKey);
		this.zone = zone;
		this.checkParents = checkParents;
	}

}
