package com.ForgeEssentials.backup;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.google.common.primitives.Ints;

public class BackupConfig extends ModuleConfigBase
{
	private Configuration		config;
	private static final String	MAIN		= "Backup";
	private static final String	AUTOBACKUP	= MAIN + ".autoBackup";
	private static final String	AUTOREMOVE	= MAIN + ".autoRemove";

	// Main
	public static String		backupName;
	public static String		backupDir;
	public static boolean		backupOnWorldUnload;
	public static boolean		backupIfUnloaded;
	public static boolean		enableMsg;

	// AutoBackup
	public static Integer		autoInterval;
	public static Integer		worldSaveInterval;
	public static boolean		worldSaveing;
	public static List<Integer>	whitelist;
	public static List<Integer>	blacklist;
	public static List<String>	extraFolders;

	// AutoRemove
	public static boolean		enableAutoRemove;
	public static Integer		minimunFreeSpace;
	public static Integer		maxfilesperbackupfolder;
	public static Integer		maxBackupLifespan;

	public BackupConfig(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		/*
		 * Main cat
		 */
		config.addCustomCategoryComment(MAIN, "Configure the backup system.");
		backupName = config.get(MAIN, "name", "%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").value;
		backupDir = config.get(MAIN, "backupsDir", "Backup", "The path to the backup folder.").value;
		backupOnWorldUnload = config.get(MAIN, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").getBoolean(true);
		backupIfUnloaded = config.get(MAIN, "backupIfUnloaded", true, "Make backups if world is not loaded.").getBoolean(true);
		enableMsg = config.get(MAIN, "enableMsg", true, "Send a message to eveyone with Permission: \"ForgeEssentials.backup.msg\"").getBoolean(true);

		ModuleBackup.baseFolder = new File(backupDir);

		/*
		 * Subcat autoBackup
		 */
		config.addCustomCategoryComment(AUTOBACKUP, "Settings for the scheduled backup system");

		autoInterval = config.get(AUTOBACKUP, "interval", 30, "Interval in minutes. 0 to disable").getInt();
		worldSaveInterval = config.get(AUTOBACKUP, "worldSaveInterval", 10, "Does a save-all every X minutes. 0 to disable").getInt();
		worldSaveing = config.get(AUTOBACKUP, "worldSaving", false, "If false, doesn't save wold continuesly.").getBoolean(false);
		whitelist = Ints.asList(config.get(AUTOBACKUP, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").getIntList());
		blacklist = Ints.asList(config.get(AUTOBACKUP, "blacklist", new int[] {}, "Don't make automatic backups of these dims. Can still be done via command.").getIntList());
		extraFolders = Arrays.asList(config.get(AUTOBACKUP, "extraFolders", new String[] { "" }, "Make a backup of these folders every autoBackup. Relative to server.jar").valueList);

		/*
		 * Subcat autoRemove
		 */
		config.addCustomCategoryComment(AUTOREMOVE, "Settings for the autoremoval of old backups");

		enableAutoRemove = config.get(AUTOREMOVE, "enable", true, "Automaticly remove old backups").getBoolean(true);
		minimunFreeSpace = config.get(AUTOREMOVE, "minimunFreeSpace", -1, "Minimum of free space that needs to remain on the HDD the server is on. Value in GB. -1 disables this criteria.").getInt();
		maxfilesperbackupfolder = config.get(AUTOREMOVE, "maxfilesperbackupfolder", -1, "Maximum amout of backups per folder or world. -1 to disable this criteria.").getInt();
		maxBackupLifespan = config.get(AUTOREMOVE, "maxBackupLifespan", -1, "Time in hours a backup may last. -1 to disable this criteria.").getInt();

		config.save();
	}

	@Override
	public void forceSave()
	{
		config = new Configuration(file);

		/*
		 * Main cat
		 */
		config.addCustomCategoryComment(MAIN, "Configure the backup system.");
		config.get(MAIN, "name", "%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").value = backupName;
		config.get(MAIN, "backupsDir", "Backup", "The path to the backup folder.").value = backupDir;
		config.get(MAIN, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").value = backupOnWorldUnload + "";
		config.get(MAIN, "backupIfUnloaded", true, "Make backups if world is not loaded.").value = backupIfUnloaded + "";
		config.get(MAIN, "enableMsg", true, "Send a message to eveyone with Permission: \"ForgeEssentials.backup.msg\"").value = enableMsg + "";

		ModuleBackup.baseFolder = new File(backupDir);

		/*
		 * Subcat autoBackup
		 */
		config.addCustomCategoryComment(AUTOBACKUP, "Settings for the scheduled backup system");

		config.get(AUTOBACKUP, "interval", 30, "Interval in minutes. 0 to disable").value = autoInterval + "";
		config.get(AUTOBACKUP, "worldSaveInterval", 10, "Does a save-all every X minutes. 0 to disable").value = worldSaveInterval + "";
		config.get(AUTOBACKUP, "worldSaving", false, "If false, doesn't save wold continuesly.").value = worldSaveing + "";
		config.get(AUTOBACKUP, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").valueList = whitelist.toArray(new String[0]);
		config.get(AUTOBACKUP, "blacklist", new int[] {}, "Don't make automatic backups of these dims. Can still be done via command.").valueList = blacklist.toArray(new String[0]);
		config.get(AUTOBACKUP, "extraFolders", new String[] { "" }, "Make a backup of these folders every autoBackup. Relative to server.jar").valueList = extraFolders.toArray(new String[0]);

		/*
		 * Subcat autoRemove
		 */
		config.addCustomCategoryComment(AUTOREMOVE, "Settings for the autoremoval of old backups");

		config.get(AUTOREMOVE, "enable", true, "Automaticly remove old backups").value = enableAutoRemove + "";
		config.get(AUTOREMOVE, "minimunFreeSpace", -1, "Minimum of free space that needs to remain on the HDD the server is on. Value in GB. -1 disables this criteria.").value = minimunFreeSpace + "";
		config.get(AUTOREMOVE, "maxfilesperbackupfolder", -1, "Maximum amout of backups per folder or world. -1 to disable this criteria.").value = maxfilesperbackupfolder + "";
		config.get(AUTOREMOVE, "maxBackupLifespan", -1, "Time in hours a backup may last. -1 to disable this criteria.").value = maxBackupLifespan + "";

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config = new Configuration(file);

		/*
		 * Main cat
		 */
		config.addCustomCategoryComment(MAIN, "Configure the backup system.");
		backupName = config.get(MAIN, "name", "%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").value;
		backupDir = config.get(MAIN, "backupsDir", "Backup", "The path to the backup folder.").value;
		backupOnWorldUnload = config.get(MAIN, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").getBoolean(true);
		backupIfUnloaded = config.get(MAIN, "backupIfUnloaded", true, "Make backups if world is not loaded.").getBoolean(true);
		enableMsg = config.get(MAIN, "enableMsg", true, "Send a message to eveyone with Permission: \"ForgeEssentials.backup.msg\"").getBoolean(true);

		ModuleBackup.baseFolder = new File(backupDir);

		/*
		 * Subcat autoBackup
		 */
		config.addCustomCategoryComment(AUTOBACKUP, "Settings for the scheduled backup system");

		autoInterval = config.get(AUTOBACKUP, "interval", 30, "Interval in minutes. 0 to disable").getInt();
		worldSaveInterval = config.get(AUTOBACKUP, "worldSaveInterval", 10, "Does a save-all every X minutes. 0 to disable").getInt();
		worldSaveing = config.get(AUTOBACKUP, "worldSaving", false, "If false, doesn't save wold continuesly.").getBoolean(false);
		whitelist = Ints.asList(config.get(AUTOBACKUP, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").getIntList());
		blacklist = Ints.asList(config.get(AUTOBACKUP, "blacklist", new int[] {}, "Don't make automatic backups of these dims. Can still be done via command.").getIntList());
		extraFolders = Arrays.asList(config.get(AUTOBACKUP, "extraFolders", new String[] { "" }, "Make a backup of these folders every autoBackup. Relative to server.jar").valueList);

		/*
		 * Subcat autoRemove
		 */
		config.addCustomCategoryComment(AUTOREMOVE, "Settings for the autoremoval of old backups");

		enableAutoRemove = config.get(AUTOREMOVE, "enable", true, "Automaticly remove old backups").getBoolean(true);
		minimunFreeSpace = config.get(AUTOREMOVE, "minimunFreeSpace", -1, "Minimum of free space that needs to remain on the HDD the server is on. Value in GB. -1 disables this criteria.").getInt();
		maxfilesperbackupfolder = config.get(AUTOREMOVE, "maxfilesperbackupfolder", -1, "Maximum amout of backups per folder or world. -1 to disable this criteria.").getInt();
		maxBackupLifespan = config.get(AUTOREMOVE, "maxBackupLifespan", -1, "Time in hours a backup may last. -1 to disable this criteria.").getInt();

		config.save();
	}
}
