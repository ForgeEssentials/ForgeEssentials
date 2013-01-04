package com.ForgeEssentials.backup;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IModuleConfig;

public class BackupConfig implements IModuleConfig
{
	private File file = new File(ForgeEssentials.FEDIR, "backups.cfg");
	private Configuration config;

	@Override
	public void setGenerate(boolean generate) {}

	@Override
	public void init() 
	{
		config = new Configuration();
		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value;
		String backupdir = ForgeEssentials.fedirloc + config.get("Backups", "folder", "backups/", "The path to the backup folder. This is relative to the ForgeEssentials folder").value;
		
		File dir = new File(backupdir);
		if (!dir.exists())
			dir.mkdirs();
		
		BackupThread.backupDir = dir;
		
		config.save();	
	}

	@Override
	public void forceSave() {}

	@Override
	public void forceLoad(ICommandSender sender) 
	{
		config = new Configuration();
		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value;
		String backupdir = ForgeEssentials.fedirloc + config.get("Backups", "folder", "backups/", "The path to the backup folder. This is relative to the ForgeEssentials folder").value;
		
		File dir = new File(backupdir);
		if (!dir.exists())
			dir.mkdirs();
		
		BackupThread.backupDir = dir;
		
		config.save();
	}

	@Override
	public File getFile() 
	{
		return file;
	}
}
