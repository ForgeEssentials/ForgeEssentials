package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
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
import com.ForgeEssentials.util.AreaSelector.WorldArea;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class ZoneManager
{
	// GLOBAL and WORLD zones.
	public static Zone GLOBAL;
	public static Zone SUPER;

	public ZoneManager()
	{
		GLOBAL = new Zone("_GLOBAL_", Integer.MIN_VALUE);
		SUPER = new Zone("_SUPER_", Integer.MIN_VALUE);
		worldZoneMap = new ConcurrentHashMap<String, Zone>();
		zoneMap = Collections.synchronizedSortedMap(new TreeMap<String, Zone>());
	}

	protected void loadZones()
	{
		Object[] objs = ModulePermissions.data.loadAllObjects(Zone.class);
		Zone temp;
		boolean exists;
		for (Object obj : objs)
		{
			if (obj == null)
				continue;
			
			temp = (Zone) obj;
			zoneMap.put(temp.getZoneName(), temp);
			
			exists = SqlHelper.doesZoneExist(temp.getZoneName());
			if (!exists)
				SqlHelper.createZone(temp.getZoneName());
		}
	}

	// ----------------------------------------------
	// ------------ WorldZone stuff -----------------
	// ----------------------------------------------

	protected static ConcurrentHashMap<String, Zone> worldZoneMap;

	// to load WorldZones
	@ForgeSubscribe
	public void worldLoader(Load e) // thats the WorldLoad event.
	{
		String worldString = FunctionHelper.getZoneWorldString(e.world);

		if (!worldZoneMap.containsKey(worldString))
		{
			Zone zone = new Zone(worldString, e.world.getWorldInfo().getDimension());
			worldZoneMap.put(worldString, zone);
			
			boolean exists = SqlHelper.doesZoneExist(zone.getZoneName());
			if (!exists)
				SqlHelper.createZone(zone.getZoneName());
		}
	}

	public static Zone getWorldZone(World world)
	{
		String worldString = FunctionHelper.getZoneWorldString(world);

		Zone zone = worldZoneMap.get(worldString);

		if (zone == null)
		{
			zone = new Zone(worldString, world.getWorldInfo().getDimension());
			worldZoneMap.put(worldString, zone);
		}

		return zone;
	}

	// ----------------------------------------------
	// ----------- Zone Sorting stuff ---------------
	// ----------------------------------------------

	// normal zone map. WorldZones and Globals are not included.
	protected static SortedMap<String, Zone> zoneMap;

	/**
	 * WorldZones are not included here.
	 */
	public static void deleteZone(String zoneID)
	{
		Zone deleted = zoneMap.remove(zoneID);
		onZoneDeleted(deleted);
		SqlHelper.delZone(zoneID);
	}

	public static boolean doesZoneExist(String zoneID)
	{
		if (zoneID.equals(GLOBAL.getZoneName()))
		{
			return true;
		}
		else if (zoneID.equals(SUPER))
		{
			return true;
		}
		else
		{
			return SqlHelper.doesZoneExist(zoneID);
		}
	}

	public static Zone getZone(String zoneID)
	{
		if (zoneID == null)
		{
			return null;
		}
		else if (zoneID.equals(GLOBAL.getZoneName()))
		{
			return GLOBAL;
		}
		else if (zoneID.startsWith("WORLD_"))
		{
			return worldZoneMap.get(zoneID);
		}
		else
		{
			return zoneMap.get(zoneID);
		}
	}

	/**
	 * WorldZones are not included here.
	 */
	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		if (zoneMap.containsKey(zoneID))
		{
			return false;
		}

		Zone created = new Zone(zoneID, sel, world);
		zoneMap.put(zoneID, created);
		SqlHelper.createZone(zoneID);
		onZoneCreated(created);
		return true;
	}

	public static Set<String> zoneSet()
	{
		return zoneMap.keySet();
	}

	public static Zone getWhichZoneIn(Point p1, World world)
	{
		// check cache..
		Zone end = getFromCache(new WorldPoint(world, p1.x, p1.y, p1.z));
		if (end != null)
		{
			return end;
		}

		Zone worldZone = getWorldZone(world);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
		{
			if (zone.contains(p1) && worldZone.isParentOf(zone))
			{
				zones.add(zone);
			}
		}

		switch (zones.size())
		{
		// no children of the world? return the worldZone
		case 0:
			end = worldZone;
			break;
		// only 1 usable Zone? use it.
		case 1:
			end = zones.get(0);
			break;

		// else.. narrow it down
		default:
		{
			// get the one with the highest priority
			Zone priority = null;

			for (Zone zone : zones)
			{
				if (priority == null || priority.compareTo(zone) < 0)
				{
					priority = zone;
				}
			}

			end = priority;
			break;
		}
		}

		putCache(new WorldPoint(world, p1.x, p1.y, p1.z), end.getZoneName());
		return end;
	}

	public static Zone getWhichZoneIn(WorldPoint point)
	{
		World world = FunctionHelper.getDimension(point.dim);
		return getWhichZoneIn(point, world);
	}

	/**
	 * used for AllorNothing areas..
	 * 
	 * @param area
	 * @param world
	 * @return
	 */
	public static Zone getWhichZoneIn(AreaBase area, World world)
	{
		// check cache..
		Zone end = getFromCache(new WorldArea(world, area));
		if (end != null)
		{
			return end;
		}

		Zone worldZone = getWorldZone(world);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
		{
			if (zone.contains(area) && worldZone.isParentOf(zone))
			{
				zones.add(zone);
			}
		}

		switch (zones.size())
		{
		// no children of the world? return the worldZone
		case 0:
			end = worldZone;
			break;
		// only 1 usable Zone? use it.
		case 1:
			end = zones.get(0);
			break;

		// else.. narrow it down
		default:
		{
			// get the one with the highest priority
			Zone priority = null;

			for (Zone zone : zones)
			{
				if (priority == null || priority.compareTo(zone) < 0)
				{
					priority = zone;
				}
			}

			end = priority;
			break;
		}
		}

		return end;
	}

	// ----------------------------------------------
	// ------------ Cache ---------------------------
	// ----------------------------------------------

	private static ConcurrentHashMap<WorldPoint, String> pointCache = new ConcurrentHashMap<WorldPoint, String>();
	private static ConcurrentHashMap<WorldArea, String> areaCache = new ConcurrentHashMap<WorldArea, String>();

	private static void putCache(WorldPoint p, String zoneID)
	{
		pointCache.put(p, zoneID);
	}

	private static void putCache(WorldArea a, String zoneID)
	{
		areaCache.put(a, zoneID);
	}

	private static Zone getFromCache(WorldPoint p)
	{
		String zoneID = pointCache.get(p);
		if (zoneID == null)
		{
			return null;
		}
		else
		{
			return getZone(zoneID);
		}
	}

	private static Zone getFromCache(WorldArea a)
	{
		String zoneID = areaCache.get(a);
		if (zoneID == null)
		{
			return null;
		}
		else
		{
			return getZone(zoneID);
		}
	}

	private static void onZoneCreated(Zone created)
	{
		for (WorldPoint p : pointCache.keySet())
		{
			if (created.contains(p))
			{
				pointCache.remove(p);
			}
		}

		for (WorldArea a : areaCache.keySet())
		{
			if (created.contains(a))
			{
				areaCache.remove(a);
			}
		}
	}

	private static void onZoneDeleted(Zone deleted)
	{
		for (Entry<WorldPoint, String> entry : pointCache.entrySet())
		{
			if (deleted.getZoneName().equals(entry.getValue()))
			{
				pointCache.remove(entry.getKey());
			}
		}

		for (Entry<WorldArea, String> entry : areaCache.entrySet())
		{
			if (deleted.getZoneName().equals(entry.getValue()))
			{
				areaCache.remove(entry.getKey());
			}
		}
	}
}
