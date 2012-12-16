package com.ForgeEssentials.data;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.filesystem.FileSystemDataDriver;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Manages the DataDrivers and selects the correct one based on configuration settings.
 * Once the DataDriver has been initialized, this class's job is done forever. (Well, until
 * next load, I suppose.)
 * 
 * @author MysteriousAges
 *
 */
public class DataStorageManager
{
	// Default driver is Flat-file. ALWAYS have this as a fallback plan.
	private static String defaultDriver = FileSystemDataDriver.driverType;
	
	/**
	 * Parses the ForgeEssentials config file and determines which Driver to use. 
	 * @param config
	 */
	public static void setupDriver(Configuration config, FMLServerStartingEvent event)
	{
		// Static reflection strings are bad.
		String dataBasePackage = DataDriver.class.getPackage().getName() + ".";
		
		config.addCustomCategoryComment("Data", "Configuration options for how ForgeEssentials will save its data for persistence between sessions.");
		
		Property prop = config.get("Data", "storaageType", defaultDriver);
		prop.comment = "Specifies the variety of data storage FE will use. Options: FileSystem, SQL (MySQL)";
		String driverName = prop.value;
		Class c;
		DataDriver driver;
		
		try
		{
			c = Class.forName(dataBasePackage + driverName.toLowerCase() + "." + driverName + "DataDriver");
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP(String.format("Colud not load storageType specified by configs! (%sDataDriver not found!)", driverName));
			OutputHandler.SOP("Falling back to using default driver.");
			
			try
			{
				c = Class.forName(dataBasePackage + driverName.toLowerCase() + "." + driverName + "DataDriver");
			}
			catch (ClassNotFoundException ex)
			{
				OutputHandler.SOP("Can't load the FileSystem driver type!? Something really terrible has happened.");
				OutputHandler.SOP("Please check that you have installed ForgeEssentials correctly and have the latest reccomended version.");
				OutputHandler.SOP("If you still experience errors, file an Issue at the ForgeEssentials Github project, and post your logs.");
				
				return;
			}
		}
		
		try
		{
			// If there is a problem constructing the driver, this line will fail and we will enter the catch block.
			driver = (DataDriver)(c.getConstructor().newInstance());
		
			String worldName = event.getServer().getFolderName();
			
			// Allows the driver a chance to read config values.
			if (driver.parseConfigs(config, worldName))
			{
				// Update the ForgeEssentials object with this driver.
				ForgeEssentials.instance.setDataStore(driver);
			}
			else
			{
				OutputHandler.SOP("There was a problem parsing the configs. The driver is NOT LOADED.");
				OutputHandler.SOP("ForgeEssentials will not be able to save any of its data.");
			}
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Problem creating an instance of the driver "+ driverName);
			OutputHandler.SOP("ForgeEssentials will not be able to save any of its data.");
			e.printStackTrace();
		}
	}
}
