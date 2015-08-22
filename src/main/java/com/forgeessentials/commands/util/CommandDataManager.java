package com.forgeessentials.commands.util;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.data.v2.DataManager;

public class CommandDataManager
{

    public static Map<String, Kit> kits = new HashMap<String, Kit>();

    public static void load()
    {
        loadKits();
    }

    public static void save()
    {
        saveKits();
    }

    /*
     * Loading loops
     */
    public static void loadKits()
    {
        kits = DataManager.getInstance().loadAll(Kit.class);
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
