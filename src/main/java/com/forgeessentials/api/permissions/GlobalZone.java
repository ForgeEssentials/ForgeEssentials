package com.forgeessentials.api.permissions;

import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

public class GlobalZone extends Zone {

	@Override
	public boolean isPointInZone(WorldPoint point)
	{
		return true;
	}

	@Override
	public boolean isAreaInZone(WorldArea point)
	{
		return true;
	}

	@Override
	public boolean isPartOfAreaInZone(WorldArea point)
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "GLOBAL";
	}

}
