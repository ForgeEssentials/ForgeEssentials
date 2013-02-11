package com.ForgeEssentials.data;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.api.data.IStorageManager;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class StorageManager implements IStorageManager
{
	// just keeps an instance of the config for future use.
	private Configuration											config;
	private static final String										configCategory	= "data";

	public static final EnumDriverType								defaultDriver	= EnumDriverType.TEXT;
	private EnumDriverType											chosen			= defaultDriver;

	private ConcurrentHashMap<EnumDriverType, String>				typeChosens;													// the
																																	// defaults...
	private ConcurrentHashMap<String, Class<? extends DataDriver>>	classMap;														// registered
																																	// ones...
	private ConcurrentHashMap<String, DataDriver>					instanceMap;													// instantiated
																																	// ones

	private static StorageManager									instance;

	private boolean													loaded			= false;

	protected static ConcurrentHashMap<Class, TypeInfo>			taggerList		= new ConcurrentHashMap<Class, TypeInfo>();

	public StorageManager(Configuration config)
	{
		classMap = new ConcurrentHashMap<String, Class<? extends DataDriver>>();
		instanceMap = new ConcurrentHashMap<String, DataDriver>();
		typeChosens = new ConcurrentHashMap<EnumDriverType, String>();

		this.config = config;

		config.addCustomCategoryComment("Data", "Configuration options for how ForgeEssentials will save its data for persistence between sessions.");

		// generates the configs...
		Property prop = config.get("Data", "storageType", defaultDriver.toString());
		prop.comment = "Specifies the variety of data storage FE will use. Options: " + EnumDriverType.getAll(" ");

		typeChosens.put(EnumDriverType.TEXT, "ForgeConfig");
		typeChosens.put(EnumDriverType.BINARY, "NBT");
		typeChosens.put(EnumDriverType.SQL, "SQL_DB");

		String cat;
		for (EnumDriverType type : EnumDriverType.values())
		{
			if (type == EnumDriverType.SQL)
				continue;
			cat = "Data." + type;
			config.get(cat, "chosenDriver", typeChosens.get(type));
		}

		instance = this;
	}

	/**
	 * Parses the ForgeEssentials config file and determines which Driver to
	 * use. This will be loaded up with the lazy method. only the chosen ones
	 * will be loaded...
	 * 
	 * @param Config
	 */
	public void setupManager(FMLServerStartingEvent event)
	{
		// verify default driver...
		if (classMap.get(typeChosens.get(defaultDriver)) == null)
			throw new RuntimeException("{ForgeEssentials} Default DataDriver is invalid! Valid types: " + Arrays.toString(classMap.values().toArray()));

		DataDriver driver;
		for (Entry<String, DataDriver> entry : instanceMap.entrySet())
		{
			try
			{

				// tried and tested method of getting the worldName
				String worldName = event.getServer().getFolderName();

				// things MAY error here as well...
				entry.getValue().parseConfigs(config, "Data." + entry.getValue().getType() + "." + entry.getValue().getName(), worldName);

				// register tagged classes...
				for (TypeInfo tag : taggerList.values())
				{
					entry.getValue().onClassRegistered(tag);
				}
			}
			catch (Exception e)
			{
				OutputHandler.info("Problem loading DataDriver " + entry.getKey());
				OutputHandler.info("ForgeEssentials will not be able to save any data through this driver");
				e.printStackTrace();
			}
		}

		loaded = true;
	}

	/**
	 * Should only be done before the server starts. May override existing
	 * Driver types.
	 * 
	 * @param name
	 * Name to be used in configs
	 * @param c
	 */
	public void registerDriver(String name, Class<? extends DataDriver> c)
	{
		try
		{
			// If there is a problem constructing the driver, this line will
			// fail and we will enter the catch block.
			DataDriver driver = c.newInstance();
			instance.classMap.put(name, c);
			instance.instanceMap.put(name, driver);
		}
		catch (Exception e)
		{
			OutputHandler.info("Problem initializing DataDriver " + name);
			OutputHandler.info("ForgeEssentials will not be able to save any data through this driver");
			e.printStackTrace();
		}
	}

	public DataDriver getReccomendedDriver()
	{
		return getDriverOfType(chosen);
	}

	public DataDriver getDriverOfType(EnumDriverType type)
	{
		return getDriverOfName(instance.typeChosens.get(type));
	}

	/**
	 * @param name
	 * @return default DataDriver if the requested one is unavailable.
	 */
	private DataDriver getDriverOfName(String name)
	{
		DataDriver d = instance.instanceMap.get(name);
		if (d == null)
		{
			d = instance.instanceMap.get(defaultDriver);
		}
		return d;
	}

	public void registerSaveableClass(Class type)
	{
		// maybe not....  TODO: fix..
		if (!type.isAnnotationPresent(SaveableObject.class))
			throw new IllegalArgumentException("Only classes that have the @SaveableObject annotation may be registered!");
		
		
		taggerList.put(type, TypeInfo.getTaggerForType(type));
	}

	public boolean hasMapping(Class type)
	{
		return taggerList.containsKey(type);
	}

	public TypeInfo getInfoForType(Class type)
	{
		TypeInfo tagged;
		if (!hasMapping(type))
		{
			registerSaveableClass(type);
			tagged = taggerList.get(type);
			if (instance.loaded)
				for (DataDriver driver : instance.instanceMap.values())
					driver.onClassRegistered(tagged);

			return tagged;
		}
		return taggerList.get(type);
	}

	public DBConnector getCoreDBConnector()
	{
		return ((SQLDataDriver) instance.getDriverOfType(EnumDriverType.SQL)).connector;
	}
}
