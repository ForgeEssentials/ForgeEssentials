package com.forgeessentials.api.permissions;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.selections.AreaBase;

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
	public boolean isPlayerInZone(EntityPlayer player)
	{
		if (!worldZone.isPlayerInZone(player))
			return false;

		return true;
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
