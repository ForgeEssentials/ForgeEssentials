package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class ZoneManager
{
	// GLOBAL and WORLD zones.
	public static Zone	GLOBAL;

	public ZoneManager()
	{
		GLOBAL = new Zone("_GLOBAL_", null);
		worldZoneMap = new ConcurrentHashMap<String, Zone>();
		zoneMap = Collections.synchronizedSortedMap(new TreeMap<String, Zone>());
	}

	// ----------------------------------------------
	// ------------ WorldZone stuff -----------------
	// ----------------------------------------------

	protected static ConcurrentHashMap<String, Zone>	worldZoneMap;

	// to load WorldZones
	@ForgeSubscribe
	public void worldLoader(Load e) // thats the WorldLoad event.
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
	protected static SortedMap<String, Zone>	zoneMap;

	/**
	 * WorldZones are not included here.
	 */
	public static void deleteZone(String zoneID)
	{
		zoneMap.remove(zoneID);
	}

	public static boolean doesZoneExist(String zoneID)
	{
		if (zoneID.equals(GLOBAL.getZoneID()))
			return true;
		else if (zoneID.startsWith("WORLD_"))
			return true;
		else
			return zoneMap.containsKey(zoneID);
	}

	public static Zone getZone(String zoneID)
	{
		if (zoneID == null)
			return null;
		else if (zoneID.equals(GLOBAL.getZoneID()))
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
	
	public static Zone getWhichZoneIn(WorldPoint point)
	{
		Zone worldZone = getWorldZone(FunctionHelper.getDimension(point.dim));
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(point) && worldZone.isParentOf(zone))
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


	/**
	 * used for AllorNothing areas..
	 * @param area
	 * @param world
	 * @return
	 */
	public static Zone getWhichZoneIn(AreaBase area, World world)
	{
		Zone worldZone = getWorldZone(world);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(area) && worldZone.isParentOf(zone))
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
