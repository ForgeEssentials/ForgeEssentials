package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

public class WorldZone extends Zone {

	private int dimensionID;

	private List<AreaZone> areaZones = new ArrayList<AreaZone>();

	public WorldZone(int dimensionID)
	{
		this.dimensionID = dimensionID;
	}

	@Override
	public boolean isPlayerInZone(EntityPlayer player)
	{
		return player.dimension == dimensionID;
	}
	
	@Override
	public boolean isPointInZone(WorldPoint point)
	{
		return point.getDimension() == dimensionID;
	}

	@Override
	public boolean isAreaInZone(WorldArea area)
	{
		return area.getDimension() == dimensionID;
	}

	@Override
	public boolean isPartOfAreaInZone(WorldArea area)
	{
		return area.getDimension() == dimensionID;
	}

	@Override
	public String getName()
	{
		return "WORLD_" + dimensionID;
	}

	public int getDimensionID()
	{
		return dimensionID;
	}

	public AreaZone getAreaZone(String areaName)
	{
		for (AreaZone areaZone : areaZones)
		{
			if (areaZone.getShotName().equals(areaName))
			{
				return areaZone;
			}
		}
		return null;
	}

	public void addAreaZone(AreaZone areaZone)
	{
		areaZones.add(areaZone);
	}

	public void removeAreaZone(String areaName)
	{
		areaZones.remove(areaName);
	}

	public Collection<AreaZone> getAreaZones()
	{
		return areaZones;
	}

	public void sortAreaZones()
	{
		throw new RuntimeException("Not yet implemented");
	}

}
