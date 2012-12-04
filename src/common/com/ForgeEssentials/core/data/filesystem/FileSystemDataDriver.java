package com.ForgeEssentials.core.data.filesystem;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.data.DataStorageManager;
import com.ForgeEssentials.core.data.DataDriver;

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
		
		this.baseFilePath = ForgeEssentials.FEDIR.toString();
		DataDriver.instance = this;
		
		// Register DataAdapter classes here that save to the file system.
		//registerPersisters(this);
	}
	
	@Override
	public void parseConfigs(Configuration config)
	{
		// I don't think we actually need to do anything here.
	}

	private static void registerPersisters(FileSystemDataDriver obj)
	{
		obj.map.put(PlayerInfo.class, new PlayerInfoDataAdapter(obj.baseFilePath));
		
		// Add additional flat-file storage classes here.
	}
}
