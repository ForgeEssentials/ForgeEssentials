package com.forgeessentials.api.permissions;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link AreaZone} covers just a specific area in one world. It has higher priority than all other {@link Zone} types. Area zones can overlap. Priority is then
 * decided by assigning highest priority to the innermost, smallest area.
 * 
 * @author Olee
 */
public class AreaZone extends Zone {

	private WorldZone worldZone;

	private String name;

	private AreaBase area;

	private int priority;

	AreaZone(int id)
	{
		super(id);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area, int id)
	{
		this(id);
		this.worldZone = worldZone;
		this.name = name;
		this.area = area;
		this.worldZone.addAreaZone(this);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area)
	{
		this(worldZone, name, area, worldZone.getServerZone().nextZoneID());
	}

	protected boolean isPointInZone(Point point)
	{
		return point.getX() >= area.getLowPoint().getX() && point.getZ() >= area.getLowPoint().getZ() && point.getX() <= area.getHighPoint().getX()
				&& point.getZ() <= area.getHighPoint().getZ() && point.getY() >= area.getLowPoint().getY() && point.getY() <= area.getHighPoint().getY();
	}

	@Override
	public boolean isInZone(WorldPoint point)
	{
		if (!worldZone.isInZone(point))
			return false;
		return isPointInZone(point);
	}

	@Override
	public boolean isInZone(WorldArea area)
	{
		if (!worldZone.isInZone(area))
			return false;
		return isPointInZone(area.getLowPoint()) && isPointInZone(area.getHighPoint());
	}

	@Override
	public boolean isPartOfZone(WorldArea area)
	{
		if (!worldZone.isPartOfZone(area))
			return false;
		return isPointInZone(area.getLowPoint()) || isPointInZone(area.getHighPoint());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return worldZone.getName() + "_" + name;
	}

	@Override
	public Zone getParent()
	{
		// TODO: Get zones covering this one!
		return worldZone;
	}

	@Override
	public ServerZone getServerZone()
	{
		return worldZone.getServerZone();
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

	public void setArea(AreaBase area)
	{
		this.area = area;
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
