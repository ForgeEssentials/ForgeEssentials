package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

/**
 * {@link WorldZone} covers the entirety of a world. Third lowest in priority with next being {@link ServerZone}.
 * 
 * @author Olee
 */
@SaveableObject
public class WorldZone extends Zone {

	private ServerZone serverZone;

	@SaveableField
	private int dimensionID;

	@SaveableField
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

	@Reconstructor
	private static WorldZone reconstruct(IReconstructData tag)
	{
		WorldZone result = new WorldZone((int) tag.getFieldValue("id"));
		result.doReconstruct(tag);
		result.dimensionID = (int) tag.getFieldValue("dimensionID");
		result.areaZones = (List<AreaZone>) tag.getFieldValue("areaZones");
		return result;
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
			if (areaZone.getShotName().equals(areaName))
			{
				return areaZone;
			}
		}
		return null;
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
		// TODO: Sort for priorities
		throw new RuntimeException("Not yet implemented");
	}

	void addAreaZone(AreaZone areaZone)
	{
		areaZones.add(areaZone);
	}

}
