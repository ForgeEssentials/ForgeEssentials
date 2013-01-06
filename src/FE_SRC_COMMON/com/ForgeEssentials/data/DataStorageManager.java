package com.ForgeEssentials.data;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
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
	// Default driver is Flat-file. ALWAYS have this as a fallback plan.
	private static String defaultDriver = "ForgeConfig";

	// just keeps an instance of the config for future use.
	private Configuration config;

	private ConcurrentHashMap<String, Class<? extends DataDriver>> classMap; // registerred
																				// ones...

	private ConcurrentHashMap<String, DataDriver> instanceMap; // instantiated
																// ones

	protected static ConcurrentHashMap<Class, TypeTagger> taggerList = new ConcurrentHashMap<Class, TypeTagger>();

	public DataStorageManager(Configuration config)
	{
		classMap = new ConcurrentHashMap<String, Class<? extends DataDriver>>();
		instanceMap = new ConcurrentHashMap<String, DataDriver>();

		this.config = config;

		config.addCustomCategoryComment("Data", "Configuration options for how ForgeEssentials will save its data for persistence between sessions.");

		String temp = ForgeConfigDataDriver.class.getSimpleName();
		Property prop = config.get("Data", "storageType", temp.substring(0, temp.indexOf("DataDriver")));
		prop.comment = "Specifies the variety of data storage FE will use. Options: ForgeConfig, SQLite, NBT";
	}

	/**
	 * Parses the ForgeEssentials config file and determines which Driver to use.
	 * 
	 * @param config
	 */
	public void setupManager(FMLServerStartingEvent event)
	{
		// verify default driver...
		assert classMap.get(defaultDriver) != null : new RuntimeException("{ForgeEssentials} Default DataDriver is invalid! Valid types: "
				+ Arrays.toString(classMap.values().toArray()));

		DataDriver driver;

		for (Entry<String, Class<? extends DataDriver>> entry : classMap.entrySet())
		{
			try
			{
				// If there is a problem constructing the driver, this line will
				// fail and we will enter the catch block.
				driver = entry.getValue().newInstance();

				// tried and tested method of getting the worldName
				String worldName = event.getServer().getFolderName();

				// things MAY error here as well...
				driver.parseConfigs(config, worldName);

				// register tagged classes...
				for (TypeTagger tag : taggerList.values())
				{
					driver.onClassRegisterred(tag);
				}

				instanceMap.put(entry.getKey(), driver);
			}
			catch (Exception e)
			{
				OutputHandler.SOP("Problem initializing DataDriver " + entry.getKey());
				OutputHandler.SOP("ForgeEssentials will not be able to save any data through this driver");
				e.printStackTrace();
			}
		}
	}

	public void clearDrivers()
	{
		instanceMap.clear();
	}

	/**
	 * Should only be done before the server starts. May override existing Driver types.
	 * 
	 * @param name
	 *            Name to be used in configs
	 * @param c
	 */
	public static void registerDriver(String name, Class<? extends DataDriver> c)
	{
		ForgeEssentials.dataManager.classMap.put(name, c);
	}

	/**
	 * @param name
	 * @return default DataDriver if the requested one is unavailable.
	 */
	public static DataDriver getDriverOfName(String name)
	{
		DataDriver d = ForgeEssentials.dataManager.instanceMap.get(name);
		if (d == null)
		{
			d = ForgeEssentials.dataManager.instanceMap.get(defaultDriver);
		}
		return d;
	}

	/**
	 * This method returns a DataDriver that is not internally tracked and can be used at your discretion.
	 * 
	 * @param config
	 * @param type
	 * @return NULL if something happens regarding the instantiation,
	 */
	public static DataDriver getSpecialDriver(Configuration config, String type)
	{
		try
		{
			// If there is a problem constructing the driver, this line will
			// fail and we will enter the catch block.
			DataDriver driver = ForgeEssentials.dataManager.classMap.get(type).newInstance();

			// tried and tested method of getting the worldName
			String worldName = FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName();

			// things MAY error here as well...
			driver.parseConfigs(config, worldName);

			// register tagged classes...
			for (TypeTagger tag : taggerList.values())
			{
				driver.onClassRegisterred(tag);
			}

			return driver;
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Problem initializing DataDriver " + type);
			OutputHandler.SOP("ForgeEssentials will not be able to save any data through this driver");
			e.printStackTrace();
			return null;
		}
	}

	public static void registerSaveableClass(Class type)
	{
		assert type.isAnnotationPresent(SaveableObject.class) : new IllegalArgumentException(
				"Only classes that have the @SaveableObject annotation may be registerred!");
		taggerList.put(type, new TypeTagger(type));
	}

	public static boolean hasMapping(Object o)
	{
		return taggerList.containsKey(o.getClass());
	}

	public static boolean hasMapping(Class type)
	{
		return taggerList.containsKey(type);
	}

	public static TypeTagger getTaggerForType(Class type)
	{
		if (!hasMapping(type))
		{
			registerSaveableClass(type);
		}
		return taggerList.get(type);
	}
}
