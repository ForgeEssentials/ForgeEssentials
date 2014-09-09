package com.forgeessentials.api.permissions;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link AreaZone} covers just a specific area in one world. It has higher priority than all other {@link Zone} types. Area zones can overlap. Priority is then
 * decided by assigning highest priority to the innermost, smallest area.
 * 
 * @author Olee
 */
@SaveableObject
public class AreaZone extends Zone {

	private WorldZone worldZone;

	@SaveableField
	private String name;

	@SaveableField
	private AreaBase area;

	@SaveableField
	private int priority;

	public AreaZone(WorldZone worldZone, String name, AreaBase area)
	{
		super(worldZone.getServerZone().getRootZone().getNextZoneID());
		this.worldZone = worldZone;
		this.name = name;
		this.area = area;
		this.worldZone.addAreaZone(this);
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
	public String getName()
	{
		return worldZone.toString() + "_" + name;
	}

	@Override
	public Zone getParent()
	{
		// TODO: Get zones covering this one!
		return worldZone;
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
