package com.forgeessentials.api.permissions;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

public class AreaZone extends Zone {

	private WorldZone worldZone;

	private String name;

	private AreaBase area;

	private int priority;

	public AreaZone(WorldZone worldZone, String name, AreaBase area)
	{
		this.worldZone = worldZone;
		this.name = name;
		this.area = area;
	}

	@Override
	public boolean isPointInZone(WorldPoint point)
	{
		if (!worldZone.isPointInZone(point))
			return false;
		// TODO: new permissions
		return true;
	}

	@Override
	public boolean isAreaInZone(WorldArea area)
	{
		if (!worldZone.isAreaInZone(area))
			return false;
		// TODO: new permissions
		return true;
	}

	@Override
	public boolean isPartOfAreaInZone(WorldArea area)
	{
		if (!worldZone.isPartOfAreaInZone(area))
			return false;
		// TODO: new permissions
		return true;
	}

	@Override
	public Zone getParent()
	{
		// TODO: Get zones covering this one!
		return worldZone;
	}

	@Override
	public String getName()
	{
		return worldZone.toString() + "_" + name;
	}

	public String getShotName()
	{
		return name;
	}

	public WorldZone getWorldZone()
	{
		return worldZone;
	}

	public AreaBase getArea()
	{
		return area;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}


}
