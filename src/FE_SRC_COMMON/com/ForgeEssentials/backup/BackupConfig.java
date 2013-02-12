package com.ForgeEssentials.backup;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;

public class BackupConfig extends ModuleConfigBase
{
	private Configuration		config;
	public static String		backupName;
	public static String		backupDir;

	public static final String	CAT = "Backup";
	
	public BackupConfig(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		config.addCustomCategoryComment(CAT, "Configure the backup system.");
		backupName = config.get(CAT, "name", "%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").value;

		backupDir = config.get(CAT, "backupsDir", "Backup", "The path to the backup folder.").value;

		ModuleBackup.baseFolder = new File(backupDir);
		
		config.save();
	}

	@Override
	public void forceSave()
	{
		config.addCustomCategoryComment(CAT, "Configure the backup system.");
		config.get(CAT, "name", "%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").value = backupName;

		config.get(CAT, "folder", "Backup", "The path to the backup folder.").value = backupDir;

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		backupName = config.get(CAT, "name", "%year-%month-%day_%hour-%min").value;
		backupDir = config.get(CAT, "backupsDir", "Backup").value;
		
		ModuleBackup.baseFolder = new File(backupDir);
	}
}
