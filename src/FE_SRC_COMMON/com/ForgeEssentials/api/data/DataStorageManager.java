package com.ForgeEssentials.api.data;

import com.ForgeEssentials.data.DataDriver;
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
	
	public static void registerSaveableType(Class<? extends ITypeInfo> infoType, Class type)
	{
		manager.registerSaveableClass(infoType, type);
	}

	public static void registerSaveableType(Class type)
	{
		manager.registerSaveableClass(type);
	}

	public static ITypeInfo getInfoForType(Class type)
	{
		return manager.getInfoForType(type);
	}
	
	public static TypeData getDataForType(Class type)
	{
		return manager.getDataForType(type);
	}
	
	public static TypeData getDataForObject(Object obj)
	{
		return manager.getDataForObject(obj);
	}

	public static DBConnector getCoreDBConnector()
	{
		return manager.getCoreDBConnector();
	}
}
