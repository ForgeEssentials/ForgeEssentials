package com.ForgeEssentials.core.data.filesystem;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.data.DataBacking;

/**
 * Holds a collection of Class -> DataPersister mappings for a flat-file storage method.
 * 
 * @author MysteriousAges
 *
 */
public class FileSystemDataBacking extends DataBacking
{
	
	protected String baseFilePath;
	public static String newline = "\r\n";

	public FileSystemDataBacking(String path)
	{
		super();
		
		this.baseFilePath = path;
		FileSystemDataBacking.instance = this;
		
		// Register DataPersister classes here that save to the file system.
		registerPersisters(this);
	}
	
	/**
	 * Register FE classes
	 * @param obj
	 */
	private static void registerPersisters(FileSystemDataBacking obj)
	{
		obj.map.put(PlayerInfo.class, new PlayerInfoDataPersister(obj.baseFilePath));
		
		// Add additional flat-file storage classes here.
	}
}
