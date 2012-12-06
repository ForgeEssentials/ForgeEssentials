package com.ForgeEssentials.util.data;

import java.util.*;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.util.OutputHandler;

/**
 * The basic container for a data persistence "driver". Defines some generic functions
 * for saving and loading objects. DataDriver is meant to be extended to provide
 * support for a variety of different storage mediums. (flat-file, relational DBs...
 * that's probably about it. Different DBMSs may need specialized drivers.)
 * 
 * @author MysteriousAges
 *
 */
public abstract class DataDriver
{
	protected static DataDriver instance;
	
	// Stores bindings between logic classes and their data classes.
	protected HashMap<Class, DataAdapter> map;

	public DataDriver()
	{
		this.map = new HashMap<Class, DataAdapter>();
	}
	
	/**
	 * Gives the DataDriver a chance to load any information from the FE Configs, or
	 * determine any other information it needs to operate.
	 * Called by the core during @PreInit.
	 * 
	 * @param config Main configuration object used by FE
	 * @param worldName 
	 */
	public abstract void parseConfigs(Configuration config, String worldName);
	
	/**
	 * Returns the type of the current DataDriver to allow ForgeEssentials addon modules to determine
	 * which DataDriver is currently in use.
	 * 
	 * @return The type of the current DataDriver being used.
	 */
	public Class getDataDriverType()
	{
		return this.getClass();
	}
	
	/**
	 * Allows the DataDriver to register all Adapters it provides with the DataDriver Map
	 * of Class -> Adapters that are provided by ForgeEssentials.
	 */
	protected abstract void registerAdapters();
	
	/**
	 * Allows ForgeEssentials addon modules to register their own DataAdapters into the system.
	 * 
	 * @param saveType The addon module's class the adapter manages
	 * @param adapter the DataAdapter object
	 * @return True, if the mapping was added successfully.
	 */
	public boolean registerExternalAdapter(Class saveType, DataAdapter adapter)
	{
		boolean flag = false;
		if (!this.map.containsKey(saveType))
		{
			this.map.put(saveType, adapter);
			flag = true;
		}
		return flag;
	}
	
	public static DataDriver getInstance()
	{
		return DataDriver.instance;
	}
	
	/**
	 * Checks the DataDriver to see if it knows how to persist an object.
	 * 
	 * @param o Instance of any class
	 * @return true if and only if the class has a binding to a DataAdapter in the current driver.
	 */
	public boolean hasMapping(Object o)
	{
		return this.map.containsKey(o.getClass());
	}
	
	/**
	 * Saves an object to the data storage defined by this Driver. If the class does not have
	 * a mapping in the current backing, it will not be saved. (See hasMapping() )
	 * 
	 * @param o Object to save
	 * @return True if the DataDriver has an adapter for the object type and is able to write its data to the store.
	 */
	public static boolean saveObject(Object o)
	{
		DataDriver d = DataDriver.instance;
		boolean flag = false;
		if (d != null)
		{
			if (d.hasMapping(o))
			{
				DataAdapter da = d.map.get(o.getClass());
				
				if (da != null)
				{
					flag = da.saveData(o);
				}
				else
				{
					OutputHandler.SOP("DataDriver " + " does not have an instance for " + o.getClass());
				}
			}
		}
		return flag;
	}
	
	/**
	 * Loads data from a store and populates an existing instance of the requested type with
	 * the information from the store. This helps to get around issues with constructors
	 * requiring objects that are not available during load.
	 * 
	 * If no DataDriver has been loaded, the function will not populate the destination object.
	 * 
	 * @param loadingKey Object required by the DataAdapter to uniquely determine which record to load
	 * @param destination Instance of an object that will be populated with data from the store
	 * @return True, if the Driver has a mapping for the object and is able to successfully load from the store. False otherwise.
	 */
	public static boolean loadObject(Object loadingKey, Object destination)
	{
		DataDriver d = DataDriver.instance;
		boolean success = false;
		if (d != null)
		{
			if (d.hasMapping(destination))
			{
				DataAdapter da = d.map.get(loadingKey.getClass());
				
				if (da != null)
				{
					success = da.loadData(loadingKey, destination);
				}
				else
				{
					OutputHandler.SOP("DataDriver does not have an instance for " + destination.getClass());
				}
			}
		}
		return success;
	}
}
