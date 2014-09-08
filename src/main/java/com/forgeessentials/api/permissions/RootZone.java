package com.forgeessentials.api.permissions;

import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

public class RootZone extends Zone {

	public RootZone()
	{
		super(0);
	}

	@Override
	public boolean isInZone(WorldPoint point)
	{
		return true;
	}

	@Override
	public boolean isInZone(WorldArea point)
	{
		return true;
	}

	@Override
	public boolean isPartOfZone(WorldArea point)
	{
		return true;
	}

	@Override
	public Zone getParent()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "ROOT";
	}

}
