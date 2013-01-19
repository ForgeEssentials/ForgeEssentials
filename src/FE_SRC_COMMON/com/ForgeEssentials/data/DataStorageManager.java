package com.ForgeEssentials.data;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
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
	// just keeps an instance of the config for future use.
	private Configuration config;
	private static final String configCategory = "data";

	public static final EnumDriverType defaultDriver = EnumDriverType.TEXT;
	private static EnumDriverType chosen = defaultDriver;

	private ConcurrentHashMap<EnumDriverType, String> typeChosens; // the defaults...
	private ConcurrentHashMap<String, Class<? extends DataDriver>> classMap; // registered ones...
	private ConcurrentHashMap<String, DataDriver> instanceMap; // instantiated ones
	
	private boolean loaded = false;

	protected static ConcurrentHashMap<Class, TypeTagger> taggerList = new ConcurrentHashMap<Class, TypeTagger>();

	public DataStorageManager(Configuration config)
	{
		classMap = new ConcurrentHashMap<String, Class<? extends DataDriver>>();
		instanceMap = new ConcurrentHashMap<String, DataDriver>();
		typeChosens = new ConcurrentHashMap<EnumDriverType, String>();

		this.config = config;

		config.addCustomCategoryComment("Data", "Configuration options for how ForgeEssentials will save its data for persistence between sessions.");
		
		// generates the configs...
		Property prop = config.get("Data", "storageType", defaultDriver.toString());
		prop.comment = "Specifies the variety of data storage FE will use. Options: "+EnumDriverType.getAll(" ");
		
		typeChosens.put(EnumDriverType.TEXT, "ForgeConfig");
		typeChosens.put(EnumDriverType.BINARY, "NBT");
		typeChosens.put(EnumDriverType.SQL, "SQL_DB");
		
		String cat;
		for (EnumDriverType type : EnumDriverType.values())
		{
			cat = "Data."+type;
			config.get(cat, "chosenDriver", typeChosens.get(type));
		}
	}

	/**
	 * Parses the ForgeEssentials config file and determines which Driver to use.
	 * This will be loaded up with the lazy method. only the chosen ones will be loaded...
	 *  
	 * @param Config
	 */
	public void setupManager(FMLServerStartingEvent event)
	{
		// verify default driver...
		if (classMap.get(typeChosens.get(defaultDriver)) == null)
			throw new RuntimeException("{ForgeEssentials} Default DataDriver is invalid! Valid types: "+Arrays.toString(classMap.values().toArray()));

		DataDriver driver;
		for (Entry<String, DataDriver> entry : instanceMap.entrySet())
		{
			try
			{
				
				// tried and tested method of getting the worldName
				String worldName = event.getServer().getFolderName();

				// things MAY error here as well...
				entry.getValue().parseConfigs(config, "Data."+entry.getValue().getType()+"."+entry.getValue().getName(), worldName);

				// register tagged classes...
				for (TypeTagger tag : taggerList.values())
				{
					entry.getValue().onClassRegistered(tag);
				}
			}
			catch (Exception e)
			{
				OutputHandler.SOP("Problem loading DataDriver " + entry.getKey());
				OutputHandler.SOP("ForgeEssentials will not be able to save any data through this driver");
				e.printStackTrace();
			}
		}
		
		loaded = true;
	}

	/**
	 * Should only be done before the server starts. May override existing Driver types.
	 * 
	 * @param name Name to be used in configs
	 * @param c
	 */
	public static void registerDriver(String name, Class<? extends DataDriver> c)
	{
		try
		{
			// If there is a problem constructing the driver, this line will
			// fail and we will enter the catch block.
			DataDriver driver = c.newInstance();
			ForgeEssentials.dataManager.classMap.put(name, c);
			ForgeEssentials.dataManager.instanceMap.put(name, driver);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Problem initializing DataDriver " + name);
			OutputHandler.SOP("ForgeEssentials will not be able to save any data through this driver");
			e.printStackTrace();
		}
	}
	
	public static DataDriver getReccomendedDriver()
	{
		return getDriverOfType(defaultDriver);
	}
	
	public static DataDriver getDriverOfType(EnumDriverType type)
	{
		return getDriverOfName(ForgeEssentials.dataManager.typeChosens.get(type));
	}

	/**
	 * @param name
	 * @return default DataDriver if the requested one is unavailable.
	 */
	private static DataDriver getDriverOfName(String name)
	{
		DataDriver d = ForgeEssentials.dataManager.instanceMap.get(name);
		if (d == null)
		{
			d = ForgeEssentials.dataManager.instanceMap.get(defaultDriver);
		}
		return d;
	}

	public static void registerSaveableClass(Class type)
	{
		if (!type.isAnnotationPresent(SaveableObject.class))
			throw new IllegalArgumentException("Only classes that have the @SaveableObject annotation may be registered!");
		taggerList.put(type, new TypeTagger(type));
	}

	public static boolean hasMapping(Class type)
	{
		return taggerList.containsKey(type);
	}

	public static TypeTagger getTaggerForType(Class type)
	{
		TypeTagger tagged;
		if (!hasMapping(type))
		{
			registerSaveableClass(type);
			tagged = taggerList.get(type);
			if (ForgeEssentials.dataManager.loaded)
				for (DataDriver driver : ForgeEssentials.dataManager.instanceMap.values())
					driver.onClassRegistered(tagged);
					
			return tagged;
		}
		return taggerList.get(type);
	}
	
	public static DBConnector getCoreDBConnector()
	{
		return ((SQLDataDriver)ForgeEssentials.dataManager.getDriverOfType(EnumDriverType.SQL)).connector;
	}
}
