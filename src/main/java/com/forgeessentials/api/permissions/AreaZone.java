package com.forgeessentials.api.permissions;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

public class AreaZone extends Zone {

	private WorldZone worldZone;

	private String name;

	private AreaBase area;

	private int priority;

	public AreaZone(WorldZone worldZone, String name, AreaBase area, int id)
	{
		super(id);
		this.worldZone = worldZone;
		this.name = name;
		this.area = area;
		this.worldZone.addAreaZone(this);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area)
	{
		this(worldZone, name, area, APIRegistry.perms.getNextZoneID());
	}

	@Override
	public boolean isInZone(WorldPoint point)
	{
		if (!worldZone.isInZone(point))
			return false;
		// TODO: new permissions
		return true;
	}

	@Override
	public boolean isInZone(WorldArea area)
	{
		if (!worldZone.isInZone(area))
			return false;
		// TODO: new permissions
		return true;
	}

	@Override
	public boolean isPartOfZone(WorldArea area)
	{
		if (!worldZone.isPartOfZone(area))
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
