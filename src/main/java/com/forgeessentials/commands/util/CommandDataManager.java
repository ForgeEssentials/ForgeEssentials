package com.forgeessentials.commands.util;

import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;

import java.util.HashMap;

public class CommandDataManager {
    private static ClassContainer conKit = new ClassContainer(Kit.class);
    private static ClassContainer conWT = new ClassContainer(WeatherTimeData.class);

    private static AbstractDataDriver data;

    public static HashMap<String, Kit> kits = new HashMap<String, Kit>();
    public static HashMap<Integer, WeatherTimeData> WTmap = new HashMap<Integer, WeatherTimeData>();

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
        Object[] objs = data.loadAllObjects(conKit);
        for (Object obj : objs)
        {
            Kit kit = (Kit) obj;
            kits.put(kit.getName(), kit);
        }
    }

    public static void loadWT()
    {
        Object[] objs = data.loadAllObjects(conWT);
        for (Object obj : objs)
        {
            WeatherTimeData wt = (WeatherTimeData) obj;
            WTmap.put(wt.dimID, wt);
        }
    }

	/*
     * Saving loops
	 */

    public static void saveKits()
    {
        for (Kit kit : kits.values())
        {
            data.saveObject(conKit, kit);
        }
    }

    public static void saveWT()
    {
        for (WeatherTimeData wt : WTmap.values())
        {
            data.saveObject(conWT, wt);
        }
    }

    /*
     * Adding loops
     */
    public static void addKit(Kit kit)
    {
        kits.put(kit.getName(), kit);
        data.saveObject(conKit, kit);
    }
	
	/*
	 * Removing loops
	 */

    public static void removeKit(Kit kit)
    {
        kits.remove(kit.getName());
        data.deleteObject(conKit, kit.getName());
    }
}
