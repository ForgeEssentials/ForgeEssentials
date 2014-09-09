package com.forgeessentials.api.permissions;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
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

	public ServerZone(RootZone rootZone)
	{
		super(1);
		this.rootZone = rootZone;
		this.rootZone.setServerZone(this);
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

	void addWorldZone(WorldZone zone)
	{
		worldZones.put(zone.getDimensionID(), zone);
	}

}
