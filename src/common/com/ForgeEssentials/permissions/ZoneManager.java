package com.ForgeEssentials.permissions;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.core.FunctionHelper;

public class ZoneManager
{
	// GLOBAL and WORLD zones.
	public static Zone	GLOBAL;
	protected static HashMap<String, Zone> worldZoneMap;
	
	// normal zone map. WorldZones and Globals are not included.
	public static HashMap<String, Zone> zoneMap;

	public ZoneManager()
	{
		GLOBAL = new Zone("__GLOBAL__");
		worldZoneMap = new HashMap<String, Zone>();
	}
	
	// to load WorldZones
	@ForgeSubscribe
	public void worldLoader(WorldEvent.Load e)
	{
		String worldString = FunctionHelper.getZoneWorldString(e.world);
		
		if (!worldZoneMap.containsKey(worldString))
		{
			Zone zone = new Zone(worldString);
			worldZoneMap.put(worldString, zone);
		}
	}
	
	public static Zone getWorldZone(World world)
	{
		String worldString = FunctionHelper.getZoneWorldString(world);
		
		Zone zone = worldZoneMap.get(worldString);
		
		if (zone == null)
		{
			zone = new Zone(worldString);
			worldZoneMap.put(worldString, zone);
		}
		
		return zone;
	}
	
	/*
	private static HashMap<String, Zone>	zoneMap	= new HashMap<String, Zone>();

	public static void deleteZone(String zoneID)
	{
		zoneMap.remove(zoneID);
	}

	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		if (zoneMap.containsKey(zoneID))
			return false;
		zoneMap.put(zoneID, new Zone(zoneID, sel, world));
		return true;
	}

	private static Zone narrow(ArrayList<Zone> zones, Zone parent)
	{
		ArrayList<Zone> allChildren = new ArrayList<Zone>();
		ArrayList<Zone> directChildren = new ArrayList<Zone>();

		// we are left with everything whos parent was NOT the one specified....
		for (Zone zone : zones)
			if (parent.isParentOf(zone))
				allChildren.add(zone);
			else if (zone.parentID.equals(parent.parentID))
				directChildren.add(zone);

		// nothing is a child of this parent?? check priorities.
		if (allChildren.size() == 0)
		{
			// returns highest priority Zone.
			Zone priority = null;

			for (Zone zone : zones)
				if (priority == null || priority.compareTo(zone) < 0)
					priority = zone;

			// returns highest priority
			return priority;
		}
		else if (allChildren.size() == 1)
			return allChildren.get(0);
		else if (directChildren.size() == 1)
			return narrow(allChildren, directChildren.get(0));
		else
		{
			// get higest priority of direct children.
			Zone priority = null;

			for (Zone zone : zones)
				if (priority == null || priority.compareTo(zone) < 0)
					priority = zone;

			return narrow(allChildren, parent);
		}
	}
	
	// TODO: we need an "applicable Area" thing.. to force onto areas..
	public Zone getWhichZoneIn(AreaBase area, World world)
	{
		String worldString = world.getWorldInfo().getWorldName() + "_" + world.getWorldInfo().getDimension();
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(area) && worldString.equals(zone.worldString))
				zones.add(zone);

		// only 1 zone? thats obvious
		if (zones.size() == 1)
			return zones.get(0);

		// now the sorting fun.
		else if (zones.size() > 1)
			return narrow(zones, ZoneManager.GLOBAL);

		return ZoneManager.GLOBAL;
	}

	public Zone getWhichZoneIn(Point p1, World world)
	{
		String worldString = world.getWorldInfo().getWorldName() + "_" + world.getWorldInfo().getDimension();
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(p1) && worldString.equals(zone.worldString))
				zones.add(zone);

		// only 1 zone? thats obvious
		if (zones.size() == 1)
			return zones.get(0);

		// now the sorting fun.
		else if (zones.size() > 1)
			return narrow(zones, ZoneManager.GLOBAL);

		return ZoneManager.GLOBAL;
	}
	*/
}
