package com.forgeessentials.teleport.util;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.data.v2.DataManager;

public class TeleportDataManager {

    public static Map<String, Warp> warps = new HashMap<String, Warp>();

    public static Map<String, HashMap<String, PWarp>> privateWarps = new HashMap<String, HashMap<String, PWarp>>();

    public static void load()
    {
        loadWarps();
        loadPWarps();
    }

    public static void save()
    {
        saveWarps();
        savePWarps();
    }

    /*
     * Loading loops
     */
    public static void loadWarps()
    {
        warps = DataManager.getInstance().loadAll(Warp.class);
    }

    public static void loadPWarps()
    {
        Map<String, PWarp> loadedWarps = DataManager.getInstance().loadAll(PWarp.class);
        for (PWarp warp : loadedWarps.values())
        {
            HashMap<String, PWarp> map = privateWarps.get(warp.getUsername());
            if (map == null)
            {
                map = new HashMap<String, PWarp>();
            }
            map.put(warp.getName(), warp);
            privateWarps.put(warp.getUsername(), map);
        }
    }

    /*
     * Saving loops
     */
    public static void saveWarps()
    {
        for (Warp warp : warps.values())
        {
            DataManager.getInstance().save(warp, warp.getName());
        }
    }

    public static void savePWarps()
    {
        for (HashMap<String, PWarp> pws : privateWarps.values())
        {
            for (PWarp warp : pws.values())
            {
                DataManager.getInstance().save(warp, warp.getName());
            }
        }
    }

    public static void savePWarps(String username)
    {
        for (PWarp warp : privateWarps.get(username).values())
        {
            DataManager.getInstance().save(warp, warp.getName());
        }
    }

    /*
     * Adding loops
     */

    public static void addWarp(Warp warp)
    {
        warps.put(warp.getName(), warp);
        System.out.println(warp.getName());
        DataManager.getInstance().save(warp, warp.getName());
    }

    /*
     * Removing loops
     */
    public static void removeWarp(Warp warp)
    {
        warps.remove(warp.getName());
        DataManager.getInstance().delete(Warp.class, warp.getName());
    }

    public static void removePWarp(PWarp pwarp)
    {
        DataManager.getInstance().delete(PWarp.class, pwarp.getName());
    }
}
