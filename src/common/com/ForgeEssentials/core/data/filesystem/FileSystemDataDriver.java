package com.ForgeEssentials.core.data.filesystem;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.data.DataStorageManager;
import com.ForgeEssentials.core.data.DataDriver;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

/**
 * Storage driver for filesystem (flat-file) persistence.
 * 
 * @author MysteriousAges
 *
 */
public class FileSystemDataDriver extends DataDriver
{
	public static final String driverType = "FileSystem";
	
	protected String baseFilePath;
	public static String newline = "\r\n";

	public FileSystemDataDriver()
	{
		super();
		
		DataDriver.instance = this;
	}
	
	@Override
	public void parseConfigs(Configuration config, String worldName)
	{
		Property prop;
		
		prop = config.get("Data.FileSystem", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";
		
		boolean useFEDir = prop.getBoolean(false);
		
		if (useFEDir)
		{
			this.baseFilePath = ForgeEssentials.FEDIR.toString() + "saves/" + worldName + "/";
		}
		else
		{
			if (Side.CLIENT == FMLCommonHandler.instance().getEffectiveSide())
			{
				this.baseFilePath = "./saves/" + worldName + "/";
			}
			else
			{
				this.baseFilePath = "./" + worldName +"/";
			}
		}
		
		config.save();
	}
	
	protected String getBaseBath()
	{
		return this.baseFilePath;
	}

	/**
	 * This function binds all DataAdapters
	 * @param obj 
	 */
	protected void registerAdapters()
	{
		this.map.put(PlayerInfo.class, new PlayerInfoDataAdapter());
		
		// Add additional flat-file storage classes here.
	}
}
