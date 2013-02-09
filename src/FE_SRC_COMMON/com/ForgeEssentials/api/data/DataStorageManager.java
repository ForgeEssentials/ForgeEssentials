package com.ForgeEssentials.api.data;

import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.EnumDriverType;
import com.ForgeEssentials.data.TypeTagger;
import com.ForgeEssentials.util.DBConnector;

/**
 * Manages the DataDrivers and selects the correct one based on configuration
 * settings. Once the DataDriver has been initialized, this class's job is done
 * forever. (Well, until next load, I suppose.)
 * 
 * @author MysteriousAges
 * 
 */
public abstract class DataStorageManager
{

	public static IStorageManager	manager;

	/**
	 * Should only be done before the server starts. May override existing
	 * Driver types.
	 * 
	 * @param name
	 *            Name to be used in configs
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
