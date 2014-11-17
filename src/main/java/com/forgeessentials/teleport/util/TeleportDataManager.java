package com.forgeessentials.teleport.util;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.v2.DataManager;

import java.util.HashMap;
import java.util.List;

public class TeleportDataManager {
    private static ClassContainer conWarp = new ClassContainer(Warp.class);
    private static ClassContainer conPWarp = new ClassContainer(PWarp.class);

    public static HashMap<String, Warp> warps = new HashMap<String, Warp>();
    public static HashMap<String, HashMap<String, PWarp>> pwMap = new HashMap<String, HashMap<String, PWarp>>();

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
        List<Warp> loadedWarps = DataManager.getInstance().loadAll(Warp.class);
        if (!loadedWarps.isEmpty())
            for (Warp warp : loadedWarps)
                warps.put(warp.getName(), warp);
        else
        {
            Object[] objs = DataStorageManager.getReccomendedDriver().loadAllObjects(conWarp);
            for (Object obj : objs)
            {
                Warp warp = (Warp) obj;
                warps.put(warp.getName(), warp);
            }
            saveWarps();
        }
    }

    public static void loadPWarps()
    {
        List<PWarp> loadedWarps = DataManager.getInstance().loadAll(PWarp.class);
        if (!loadedWarps.isEmpty())
            for (PWarp warp : loadedWarps)
            {
                HashMap<String, PWarp> map = pwMap.get(warp.getUsername());
                if (map == null)
                {
                    map = new HashMap<String, PWarp>();
                }
                map.put(warp.getName(), warp);
                pwMap.put(warp.getUsername(), map);
            }
        else
        {
            Object[] objs = DataStorageManager.getReccomendedDriver().loadAllObjects(conPWarp);
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
            savePWarps();
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
            DataStorageManager.getReccomendedDriver().saveObject(conWarp, warp);
        }
    }

    public static void savePWarps()
    {
        for (HashMap<String, PWarp> pws : pwMap.values())
        {
            for (PWarp warp : pws.values())
            {
                DataManager.getInstance().save(warp, warp.getName());
                DataStorageManager.getReccomendedDriver().saveObject(conPWarp, warp);
            }
        }
    }

    public static void savePWarps(String username)
    {
        for (PWarp warp : pwMap.get(username).values())
        {
            DataManager.getInstance().save(warp, warp.getName());
            DataStorageManager.getReccomendedDriver().saveObject(conPWarp, warp);
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
        DataStorageManager.getReccomendedDriver().saveObject(conWarp, warp);
    }

    /*
     * Removing loops
     */
    public static void removeWarp(Warp warp)
    {
        warps.remove(warp.getName());
        DataManager.getInstance().delete(Warp.class, warp.getName());
        DataStorageManager.getReccomendedDriver().deleteObject(conWarp, warp.getName());
    }

    public static void removePWarp(PWarp pwarp)
    {
        DataManager.getInstance().delete(PWarp.class, pwarp.getName());
        DataStorageManager.getReccomendedDriver().deleteObject(conPWarp, pwarp.getFilename());
    }
}
