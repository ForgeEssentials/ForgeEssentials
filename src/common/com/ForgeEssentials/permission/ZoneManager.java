package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.src.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class ZoneManager
{
	// GLOBAL and WORLD zones.
	public static Zone	GLOBAL;

	public ZoneManager()
	{
		GLOBAL = new Zone("_GLOBAL_", null);
		worldZoneMap = new HashMap<String, Zone>();
		zoneMap = new TreeMap<String, Zone>();
	}

	// ----------------------------------------------
	// ------------ WorldZone stuff -----------------
	// ----------------------------------------------

	protected static HashMap<String, Zone>	worldZoneMap;

	// to load WorldZones
	@ForgeSubscribe
	public void worldLoader(WorldEvent.Load e)
	{
		String worldString = FunctionHelper.getZoneWorldString(e.world);

		if (!worldZoneMap.containsKey(worldString))
		{
			Zone zone = new Zone(worldString, e.world);
			worldZoneMap.put(worldString, zone);
		}
	}

	public static Zone getWorldZone(World world)
	{
		String worldString = FunctionHelper.getZoneWorldString(world);

		Zone zone = worldZoneMap.get(worldString);

		if (zone == null)
		{
			zone = new Zone(worldString, world);
			worldZoneMap.put(worldString, zone);
		}

		return zone;
	}

	// ----------------------------------------------
	// ----------- Zone Sorting stuff ---------------
	// ----------------------------------------------

	// normal zone map. WorldZones and Globals are not included.
	protected static TreeMap<String, Zone>	zoneMap;

	/**
	 * WorldZones are not included here.
	 */
	public static void deleteZone(String zoneID)
	{
		Zone zone = zoneMap.remove(zoneID);
		zone.delete();
	}
	
	public static Zone getZone(String zoneID)
	{
		if (zoneID.equals(GLOBAL.getZoneID()))
			return GLOBAL;
		else if (zoneID.startsWith("WORLD_"))
			return worldZoneMap.get(zoneID);
		else
			return zoneMap.get(zoneID);
	}

	/**
	 * WorldZones are not included here.
	 */
	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		if (zoneMap.containsKey(zoneID))
			return false;
		zoneMap.put(zoneID, new Zone(zoneID, sel, world));
		return true;
	}
	
	public static Set<String> zoneSet()
	{
		return zoneMap.keySet();
	}

	public static Zone getWhichZoneIn(Point p1, World world)
	{
		Zone worldZone = getWorldZone(world);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(p1) && worldZone.isParentOf(zone))
				if (zone.hasChildThatContains(p1))
					continue;
				else
					zones.add(zone);


		switch (zones.size())
			{
			// no children of the world? return the worldZone
				case 0:
					return worldZone;
					// only 1 usable Zone? use it.
				case 1:
					return zones.get(0);

					// else.. narrow it down
				default:
					{
						// get the one with the highest priority
						Zone priority = null;

						for (Zone zone : zones)
							if (priority == null || priority.compareTo(zone) < 0)
								priority = zone;

						return priority;
					}
			}
	}
	
	public static Zone getWhichZoneIn(AreaBase area, World world)
	{
		Zone worldZone = getWorldZone(world);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(area) && worldZone.isParentOf(zone))
				if (zone.hasChildThatContains(area))
					continue;
				else
					zones.add(zone);


		switch (zones.size())
			{
			// no children of the world? return the worldZone
				case 0:
					return worldZone;
					// only 1 usable Zone? use it.
				case 1:
					return zones.get(0);

					// else.. narrow it down
				default:
					{
						// get the one with the highest priority
						Zone priority = null;

						for (Zone zone : zones)
							if (priority == null || priority.compareTo(zone) < 0)
								priority = zone;

						return priority;
					}
			}
	}
}
