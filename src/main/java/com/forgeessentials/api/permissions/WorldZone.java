package com.forgeessentials.api.permissions;

import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link WorldZone} covers the entirety of a world. Third lowest in priority with next being {@link ServerZone}.
 * 
 * @author Olee
 */
public class WorldZone extends Zone {

	private ServerZone serverZone;

	private int dimensionID;

	private List<AreaZone> areaZones = new ArrayList<AreaZone>();

	public WorldZone(int id)
	{
		super(id);
	}

	public WorldZone(ServerZone serverZone, int dimensionID, int id)
	{
		this(id);
		this.dimensionID = dimensionID;
		this.serverZone = serverZone;
		this.serverZone.addWorldZone(this);
	}

	public WorldZone(ServerZone serverZone, int dimensionID)
	{
		this(serverZone, dimensionID, serverZone.nextZoneID());
	}

	@Override
	public boolean isPlayerInZone(EntityPlayer player)
	{
		return player.dimension == dimensionID;
	}

	@Override
	public boolean isInZone(WorldPoint point)
	{
		return point.getDimension() == dimensionID;
	}

	@Override
	public boolean isInZone(WorldArea area)
	{
		return area.getDimension() == dimensionID;
	}

	@Override
	public boolean isPartOfZone(WorldArea area)
	{
		return area.getDimension() == dimensionID;
	}

	@Override
	public String getName()
	{
		return "WORLD_" + dimensionID;
	}

	@Override
	public Zone getParent()
	{
		return serverZone;
	}

	@Override
	public ServerZone getServerZone()
	{
		return serverZone;
	}

	public int getDimensionID()
	{
		return dimensionID;
	}

	public AreaZone getAreaZone(String areaName)
	{
		for (AreaZone areaZone : areaZones)
		{
			if (areaZone.getShortName().equals(areaName))
			{
				return areaZone;
			}
		}
		return null;
	}

	public boolean removeAreaZone(AreaZone zone)
	{
		return serverZone.removeZone(zone) | areaZones.remove(zone);
	}

	public Collection<AreaZone> getAreaZones()
	{
		return areaZones;
	}

	public void sortAreaZones()
	{
		Collections.sort(areaZones);
	}

	void addAreaZone(AreaZone areaZone)
	{
		areaZones.add(areaZone);
		getServerZone().addZone(areaZone);
		sortAreaZones();
		setDirty();
	}

}
