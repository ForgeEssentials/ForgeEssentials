package com.forgeessentials.permissions;

import com.forgeessentials.api.permissions.IZoneManager;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.selections.Selection;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneHelper implements IZoneManager {
    public static final ClassContainer container = new ClassContainer(Zone.class);
    protected ConcurrentHashMap<String, Zone> worldZoneMap;
    // normal zone map. WorldZones and Globals are not included.
    protected SortedMap<String, Zone> zoneMap;
    // GLOBAL and WORLD zones.
    private Zone GLOBAL;
    private Zone SUPER;

    // ----------------------------------------------
    // ------------ WorldZone stuff -----------------
    // ----------------------------------------------
    private ConcurrentHashMap<WorldPoint, String> pointCache = new ConcurrentHashMap<WorldPoint, String>();
    private ConcurrentHashMap<WorldArea, String> areaCache = new ConcurrentHashMap<WorldArea, String>();

    public ZoneHelper()
    {
        GLOBAL = new Zone("_GLOBAL_");
        SUPER = new Zone("_SUPER_");
        worldZoneMap = new ConcurrentHashMap<String, Zone>();
        zoneMap = Collections.synchronizedSortedMap(new TreeMap<String, Zone>());
    }

    // ----------------------------------------------
    // ----------- Zone Sorting stuff ---------------
    // ----------------------------------------------

    protected void loadZones()
    {
        Object[] objs = ModulePermissions.data.loadAllObjects(container);

        if (objs == null)
        {
            return;
        }

        Zone temp;
        boolean exists;
        for (Object obj : objs)
        {
            temp = (Zone) obj;

			/*
            if (temp == null || temp.getName() == null || temp.getName().isEmpty())
			{
				continue;
			}
			*/

            zoneMap.put(temp.getName(), temp);

            exists = SqlHelper.doesZoneExist(temp.getName());
            if (!exists)
            {
                SqlHelper.createZone(temp.getName());
            }
        }
    }

    // to load WorldZones
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void worldLoader(Load e) // thats the WorldLoad event.
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        String worldString = FunctionHelper.getZoneWorldString(e.world);

        if (!worldZoneMap.containsKey(worldString))
        {
            Zone zone = new Zone(worldString, e.world.provider.dimensionId);
            worldZoneMap.put(worldString, zone);

            boolean exists = SqlHelper.doesZoneExist(zone.getName());
            if (!exists)
            {
                SqlHelper.createZone(zone.getName());
            }
        }
    }

    @Override
    public Zone getWorldZone(World world)
    {
        String worldString = FunctionHelper.getZoneWorldString(world);

        Zone zone = worldZoneMap.get(worldString);

        if (zone == null)
        {
            zone = new Zone(worldString, world.provider.dimensionId);
            worldZoneMap.put(worldString, zone);
        }

        return zone;
    }

    /**
     * WorldZones are not included here.
     */
    @Override
    public void deleteZone(String zoneID)
    {
        Zone deleted = zoneMap.remove(zoneID);
        onZoneDeleted(deleted);
        SqlHelper.delZone(zoneID);
        ModulePermissions.data.deleteObject(new ClassContainer(Zone.class), zoneID);
    }

    @Override
    public boolean doesZoneExist(String zoneID)
    {
        if (zoneID.equals(GLOBAL.getName()))
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

    @Override
    public Zone getZone(String zoneID)
    {
        if (zoneID == null)
        {
            return null;
        }
        else if (zoneID.equalsIgnoreCase(GLOBAL.getName()))
        {
            return GLOBAL;
        }
        else if (zoneID.equalsIgnoreCase(SUPER.getName()))
        {
            return SUPER;
        }
        else if (zoneID.startsWith("WORLD_") || zoneID.startsWith("world_"))
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
    @Override
    public boolean createZone(String zoneID, Selection sel, World world)
    {
        if (zoneMap.containsKey(zoneID))
        {
            return false;
        }

        Zone created = new Zone(zoneID, sel, world);
        zoneMap.put(zoneID, created);
        SqlHelper.createZone(zoneID);
        DataStorageManager.getReccomendedDriver().saveObject(ZoneHelper.container, created);
        onZoneCreated(created);
        return true;
    }

    // ----------------------------------------------
    // ------------ Cache ---------------------------
    // ----------------------------------------------

    @Override
    public Zone getWhichZoneIn(WorldPoint p)
    {
        // check cache..
        World world = FunctionHelper.getDimension(p.dim);
        Zone end = getFromCache(p);
        if (end != null)
        {
            return end;
        }

        Zone worldZone = getWorldZone(world);
        ArrayList<Zone> zones = new ArrayList<Zone>();

        // add all zones this point is in...
        for (Zone zone : zoneMap.values())
        {
            if (zone.contains(p))
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

        putCache(new WorldPoint(world, p.x, p.y, p.z), end.getName());
        return end;
    }

    /**
     * used for AllorNothing areas..
     *
     * @param area
     * @param world
     * @return
     */
    @Override
    public Zone getWhichZoneIn(WorldArea check)
    {

        Zone end = getFromCache(check);
        if (end != null)
        {
            return end;
        }

        Zone worldZone = getWorldZone(FunctionHelper.getDimension(check.dim));
        ArrayList<Zone> zones = new ArrayList<Zone>();
        // add all zones this point is in...
        for (Zone zone : zoneMap.values())
        {
            if (zone.contains(check))
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

    private void putCache(WorldPoint p, String zoneID)
    {
        pointCache.put(p, zoneID);
    }

    private void putCache(WorldArea a, String zoneID)
    {
        areaCache.put(a, zoneID);
    }

    private Zone getFromCache(WorldPoint p)
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

    private Zone getFromCache(WorldArea a)
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

    private void onZoneCreated(Zone created)
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

    private void onZoneDeleted(Zone deleted)
    {
        for (Entry<WorldPoint, String> entry : pointCache.entrySet())
        {
            if (deleted.getName().equals(entry.getValue()))
            {
                pointCache.remove(entry.getKey());
            }
        }

        for (Entry<WorldArea, String> entry : areaCache.entrySet())
        {
            if (deleted.getName().equals(entry.getValue()))
            {
                areaCache.remove(entry.getKey());
            }
        }
    }

    @Override
    public ArrayList<Zone> getZoneList()
    {
        ArrayList<Zone> zones = new ArrayList<Zone>();

        zones.add(GLOBAL);
        zones.addAll(worldZoneMap.values());
        zones.addAll(zoneMap.values());

        return zones;
    }

    @Override
    public Zone getGLOBAL()
    {
        return GLOBAL;
    }

    @Override
    public Zone getSUPER()
    {
        return SUPER;
    }
}
