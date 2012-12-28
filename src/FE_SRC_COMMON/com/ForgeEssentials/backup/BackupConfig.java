package com.ForgeEssentials.backup;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;

public class BackupConfig
{
	private Configuration config;
	
	public BackupConfig()
	{
		config = new Configuration(new File(ForgeEssentials.FEDIR, "backups.cfg"));
		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value;
		String backupdir = ForgeEssentials.fedirloc + config.get("Backups", "folder", "backups/", "The path to the backup folder. This is relative to the ForgeEssentials folder").value;
		
		File dir = new File(backupdir);
		if (!dir.exists())
			dir.mkdirs();
		
		BackupThread.backupDir = dir;
		
		config.save();
	}
}
