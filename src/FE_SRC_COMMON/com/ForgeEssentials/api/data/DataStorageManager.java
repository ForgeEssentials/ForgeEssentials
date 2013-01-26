package com.ForgeEssentials.api.data;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.EnumDriverType;
import com.ForgeEssentials.data.SQLDataDriver;
import com.ForgeEssentials.data.StorageManager;
import com.ForgeEssentials.data.TypeTagger;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Manages the DataDrivers and selects the correct one based on configuration settings. Once the DataDriver has been initialized, this class's job is done
 * forever. (Well, until next load, I suppose.)
 * 
 * @author MysteriousAges
 * 
 */
public class DataStorageManager
{
	
	public static IStorageManager manager;

	/**
	 * Should only be done before the server starts. May override existing Driver types.
	 * 
	 * @param name Name to be used in configs
	 * @param c
	 */
	public static void registerDriver(String name, Class<? extends DataDriver> c)
	{
		manager.registerDriver(name, c);
	}
	
	public static DataDriver getReccomendedDriver()
	{
		return manager.getReccomendedDriver();
	}
	
	public static DataDriver getDriverOfType(EnumDriverType type)
	{
		return manager.getDriverOfType(type);
	}

	public static void registerSaveableClass(Class type)
	{
		manager.hasMapping(type);
	}

	public static boolean hasMapping(Class type)
	{
		return manager.hasMapping(type);
	}

	public static TypeTagger getTaggerForType(Class type)
	{
		return manager.getTaggerForType(type);
	}
	
	public static DBConnector getCoreDBConnector()
	{
		return manager.getCoreDBConnector();
	}
}
