package com.forgeessentials.api.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

public class GlobalZone extends Zone {
	
	private RootZone rootZone;

	public GlobalZone(RootZone rootZone)
	{
		super(1);
		this.rootZone = rootZone;
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
		return rootZone;
	}

	@Override
	public String getName()
	{
		return "GLOBAL";
	}

}
