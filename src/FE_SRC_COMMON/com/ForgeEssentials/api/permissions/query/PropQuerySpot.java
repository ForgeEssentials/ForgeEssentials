package com.ForgeEssentials.api.permissions.query;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.query.PropQuery.PermPropType;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class PropQuerySpot extends PropQuery
{
	public final WorldPoint spot;

	public PropQuerySpot(WorldPoint p, String permKey, PermPropType type)
	{
		super(permKey, type);
		spot = p;
	}

}
