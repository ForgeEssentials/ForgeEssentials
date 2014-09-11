package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * {@link ServerZone} contains every player on the whole server. Has second lowest priority with next being {@link RootZone}.
 * 
 * @author Olee
 */
@SaveableObject
public class ServerZone extends Zone {

	private RootZone rootZone;

	@SaveableField
	private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

	@SaveableField
	private int maxZoneID;

	@SaveableField
	private List<Group> groups = new ArrayList<Group>();

	// private List<PlayerData> players;

	public ServerZone()
	{
		super(1);
	}

	public ServerZone(RootZone rootZone)
	{
		this();
		this.maxZoneID = 1;
		this.rootZone = rootZone;
		this.rootZone.setServerZone(this);
	}

	@Reconstructor
	private static ServerZone reconstruct(IReconstructData tag)
	{
		ServerZone result = new ServerZone();
		result.doReconstruct(tag);
		result.maxZoneID = (int) tag.getFieldValue("maxZoneID");
		result.worldZones = (Map<Integer, WorldZone>) tag.getFieldValue("worldZones");
		return result;
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
	public String getName()
	{
		return "_SERVER_";
	}

	@Override
	public Zone getParent()
	{
		return rootZone;
	}

	public RootZone getRootZone()
	{
		return rootZone;
	}

	public Map<Integer, WorldZone> getWorldZones()
	{
		return worldZones;
	}

	public void clear()
	{
		worldZones.clear();
	}

	public void addWorldZone(WorldZone zone)
	{
		worldZones.put(zone.getDimensionID(), zone);
	}

	public int getNextZoneID()
	{
		return maxZoneID;
	}

	public int nextZoneID()
	{
		return ++maxZoneID;
	}

	public void setMaxZoneId(int maxId)
	{
		this.maxZoneID = maxId;
	}

	void setRootZone(RootZone rootZone)
	{
		this.rootZone = rootZone;
	}


}
