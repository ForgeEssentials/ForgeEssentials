package com.forgeessentials.commands.util;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.data.v2.DataManager;

public class CommandDataManager
{

    public static Map<String, Kit> kits = new HashMap<String, Kit>();

    public static Map<Integer, WeatherTimeData> WTmap = new HashMap<Integer, WeatherTimeData>();

    public static void load()
    {
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
        kits = DataManager.getInstance().loadAll(Kit.class);
    }

    public static void loadWT()
    {
        Map<String, WeatherTimeData> wtData = DataManager.getInstance().loadAll(WeatherTimeData.class);
        for (WeatherTimeData wt : wtData.values())
            WTmap.put(wt.dimID, wt);
    }

    /*
     * Saving loops
     */

    public static void saveKits()
    {
        for (Kit kit : kits.values())
        {
            DataManager.getInstance().save(kit, kit.getName());
        }
    }

    public static void saveWT()
    {
        for (WeatherTimeData wt : WTmap.values())
        {
            DataManager.getInstance().save(wt, Integer.toString(wt.dimID));
        }
    }

    /*
     * Adding loops
     */
    public static void addKit(Kit kit)
    {
        kits.put(kit.getName(), kit);
        DataManager.getInstance().save(kit, kit.getName());
    }

    /*
     * Removing loops
     */

    public static void removeKit(Kit kit)
    {
        kits.remove(kit.getName());
        DataManager.getInstance().delete(Kit.class, kit.getName());
    }
}
