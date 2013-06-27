package com.ForgeEssentials.commands.util;


import java.util.HashMap;


import com.ForgeEssentials.data.AbstractDataDriver;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;


public class CommandDataManager
{
	private static ClassContainer							conWarp		= new ClassContainer(Warp.class);
	private static ClassContainer							conPWarp	= new ClassContainer(PWarp.class);
	private static ClassContainer							conKit		= new ClassContainer(Kit.class);
	private static ClassContainer                           conWT       = new ClassContainer(WeatherTimeData.class);


	private static AbstractDataDriver						data;


	public static HashMap<String, Kit>						kits		= new HashMap<String, Kit>();
	public static HashMap<String, Warp>						warps		= new HashMap<String, Warp>();
	public static HashMap<String, HashMap<String, PWarp>>	pwMap		= new HashMap<String, HashMap<String, PWarp>>();
	public static HashMap<Integer, WeatherTimeData>         WTmap        = new HashMap<Integer, WeatherTimeData>();


	public static void load()
	{
		data = DataStorageManager.getReccomendedDriver();


		loadWarps();
		loadPWarps();
		loadKits();
		loadWT();
	}


	public static void save()
	{
		saveWarps();
		savePWarps();
		saveKits();
		saveWT();
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


	public static void removeKit(Kit kit)
	{
		kits.remove(kit.getName());
		data.deleteObject(conKit, kit.getName());
	}
}

