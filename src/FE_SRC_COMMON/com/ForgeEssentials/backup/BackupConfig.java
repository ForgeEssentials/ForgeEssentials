package com.ForgeEssentials.backup;

import com.ForgeEssentials.api.modules.ModuleConfigBase;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;

import java.io.File;

public class BackupConfig extends ModuleConfigBase
{
	private Configuration		config;
	public static String		backupDir;
	protected static boolean	isRelative;

	public BackupConfig(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min",
				"The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value;

		backupDir = config.get("Backups", "backupsDir", "", "The path to the backup folder. If left blank, it will assume ./ForgeEssentials/Backup/").value;
		isRelative = config.get("Backups", "isRelative", true, "If this is false, the backupsDir path will be treated as an absolute path. Otherwise it is relative to the ForgeEssentials/Backups folder.").getBoolean(true);

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value = BackupThread.backupName;

		config.get("Backups", "folder", "backups/", "The path to the backup folder. If left blank, it will assume ./ForgeEssentials/Backup/").value = backupDir;
		config.get("Backups", "isRelative", true, "If this is false, the backupsDir path will be treated as an absolute path. Otherwise it is relative to the ForgeEssentials folder.").value = "" + isRelative;

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min").value;
		backupDir = config.get("Backups", "backupsDir", "").value;
		isRelative = config.get("Backups", "isRelative", true).getBoolean(true);
	}
}
