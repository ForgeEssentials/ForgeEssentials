package com.forgeessentials.commands.util;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.v2.DataManager;

public class CommandDataManager {
    public static HashMap<String, Kit> kits = new HashMap<String, Kit>();
    public static HashMap<Integer, WeatherTimeData> WTmap = new HashMap<Integer, WeatherTimeData>();
    private static ClassContainer conKit = new ClassContainer(Kit.class);
    private static ClassContainer conWT = new ClassContainer(WeatherTimeData.class);
    private static AbstractDataDriver data;

    public static void load()
    {
        data = DataStorageManager.getReccomendedDriver();

        loadKits();
        loadWT();
    }

    public static void save()
    {
        saveKits();
        saveWT();
    }

    /*
     * Loading loops
     */
    public static void loadKits()
    {
        Map<String, Kit> loadedKits = DataManager.getInstance().loadAll(Kit.class);
        if (!loadedKits.isEmpty())
            for (Kit kit : loadedKits.values())
                kits.put(kit.getName(), kit);
        else
        {
            Object[] objs = data.loadAllObjects(conKit);
            for (Object obj : objs)
            {
                if (obj != null)
                {
                    Kit kit = (Kit) obj;
                    kits.put(kit.getName(), kit);
                }
            }
            saveKits();
        }
    }

    public static void loadWT()
    {
        Map<String, WeatherTimeData> wtData = DataManager.getInstance().loadAll(WeatherTimeData.class);
        if (!wtData.isEmpty())
            for (WeatherTimeData wt : wtData.values())
                WTmap.put(wt.dimID, wt);
        else
        {
            Object[] objs = data.loadAllObjects(conWT);
            for (Object obj : objs)
            {
                WeatherTimeData wt = (WeatherTimeData) obj;
                WTmap.put(wt.dimID, wt);
            }
            saveWT();
        }
    }

	/*
     * Saving loops
	 */

    public static void saveKits()
    {
        for (Kit kit : kits.values())
        {
            DataManager.getInstance().save(kit, kit.getName());
            data.saveObject(conKit, kit);
        }
    }

    public static void saveWT()
    {
        for (WeatherTimeData wt : WTmap.values())
        {
            DataManager.getInstance().save(wt, Integer.toString(wt.dimID));
            data.saveObject(conWT, wt);
        }
    }

    /*
     * Adding loops
     */
    public static void addKit(Kit kit)
    {
        kits.put(kit.getName(), kit);
        DataManager.getInstance().save(kit, kit.getName());
        data.saveObject(conKit, kit);
    }

	/*
     * Removing loops
	 */

    public static void removeKit(Kit kit)
    {
        kits.remove(kit.getName());
        DataManager.getInstance().delete(Kit.class, kit.getName());
        data.deleteObject(conKit, kit.getName());
    }
}
