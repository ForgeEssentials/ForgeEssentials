package com.forgeessentials.teleport.util;

import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;

import java.util.HashMap;

public class TeleportDataManager {
    private static ClassContainer conWarp = new ClassContainer(Warp.class);
    private static ClassContainer conPWarp = new ClassContainer(PWarp.class);

    private static AbstractDataDriver data;

    public static HashMap<String, Warp> warps = new HashMap<String, Warp>();
    public static HashMap<String, HashMap<String, PWarp>> pwMap = new HashMap<String, HashMap<String, PWarp>>();

    public static void load()
    {
        data = DataStorageManager.getReccomendedDriver();

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
        Object[] objs = data.loadAllObjects(conWarp);
        for (Object obj : objs)
        {
            Warp warp = (Warp) obj;
            warps.put(warp.getName(), warp);
        }
    }

    public static void loadPWarps()
    {
        Object[] objs = data.loadAllObjects(conPWarp);
        for (Object obj : objs)
        {
            PWarp warp = (PWarp) obj;
            HashMap<String, PWarp> map = pwMap.get(warp.getUsername());
            if (map == null)
            {
                map = new HashMap<String, PWarp>();
            }
            map.put(warp.getName(), warp);
            pwMap.put(warp.getUsername(), map);
        }
    }

    /*
     * Saving loops
     */
    public static void saveWarps()
    {
        for (Warp warp : warps.values())
        {
            data.saveObject(conWarp, warp);
        }
    }

    public static void savePWarps()
    {
        for (HashMap<String, PWarp> pws : pwMap.values())
        {
            for (PWarp warp : pws.values())
            {
                data.saveObject(conPWarp, warp);
            }
        }
    }

    public static void savePWarps(String username)
    {
        for (PWarp warp : pwMap.get(username).values())
        {
            data.saveObject(conPWarp, warp);
        }
    }

	/*
     * Adding loops
	 */

    public static void addWarp(Warp warp)
    {
        warps.put(warp.getName(), warp);
        data.saveObject(conWarp, warp);
    }

    /*
     * Removing loops
     */
    public static void removeWarp(Warp warp)
    {
        warps.remove(warp.getName());
        data.deleteObject(conWarp, warp.getName());
    }

    public static void removePWarp(PWarp pwarp)
    {
        data.deleteObject(conPWarp, pwarp.getFilename());
    }
}
