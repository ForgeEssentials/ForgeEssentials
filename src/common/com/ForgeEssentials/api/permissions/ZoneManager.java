package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.permissions.Zone;
import com.ForgeEssentials.util.FunctionHelper;

public class ZoneManager
{
	// GLOBAL and WORLD zones.
	public static Zone	GLOBAL;

	public ZoneManager()
	{
		GLOBAL = new Zone("_GLOBAL_");
		worldZoneMap = new HashMap<String, Zone>();
		zoneMap = new TreeMap<String, Zone>();
	}

	// ----------------------------------------------
	// ------------ WorldZone stuff -----------------
	// ----------------------------------------------

	public static HashMap<String, Zone>	worldZoneMap;

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

	// ----------------------------------------------
	// ----------- Zone Sorting stuff ---------------
	// ----------------------------------------------

	// normal zone map. WorldZones and Globals are not included.
	public static TreeMap<String, Zone>	zoneMap;

	public static void deleteZone(String zoneID)
	{
		Zone zone = zoneMap.remove(zoneID);
		zone.delete();
	}

	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		if (zoneMap.containsKey(zoneID))
			return false;
		zoneMap.put(zoneID, new Zone(zoneID, sel, world));
		return true;
	}

	// // TODO: we need an "applicable Area" thing.. to force onto areas..
	// public Zone getWhichZoneIn(AreaBase area, World world)
	// {
	// Zone worldZone = getWorldZone(world);
	// ArrayList<Zone> zones = new ArrayList<Zone>();
	//
	// // add all zones this point is in...
	// for (Zone zone : zoneMap.values())
	// if (zone.contains(area) && worldZone.isParentOf(zone))
	// zones.add(zone);
	//
	// // only 1 zone? thats obvious
	// if (zones.size() == 1)
	// return zones.get(0);
	//
	// // now the sorting fun.
	// else if (zones.size() > 1)
	// return narrow(zones, ZoneManager.GLOBAL);
	//
	// return worldZone;
	// }

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

//	private static Zone narrow(ArrayList<Zone> zones, Zone parent)
//	{
//
//		// we are left with everything whos parent was NOT the one specified....
//		for (Zone zone : zones)
//			if (parent.isParentOf(zone))
//				allChildren.add(zone);
//			else if (zone.parentID.equals(parent.parentID))
//				directChildren.add(zone);
//
//		// nothing is a child of this parent?? check priorities.
//		if (allChildren.size() == 0)
//		{
//			// returns highest priority Zone.
//			Zone priority = null;
//
//			for (Zone zone : zones)
//				if (priority == null || priority.compareTo(zone) < 0)
//					priority = zone;
//
//			// returns highest priority
//			return priority;
//		}
//		else if (allChildren.size() == 1)
//			return allChildren.get(0);
//		else if (directChildren.size() == 1)
//			return narrow(allChildren, directChildren.get(0));
//		else
//		{
//			// get higest priority of direct children.
//			Zone priority = null;
//
//			for (Zone zone : zones)
//				if (priority == null || priority.compareTo(zone) < 0)
//					priority = zone;
//
//			return narrow(allChildren, parent);
//		}
//
//	}
}
