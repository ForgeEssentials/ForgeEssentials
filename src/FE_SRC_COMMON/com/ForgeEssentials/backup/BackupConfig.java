package com.ForgeEssentials.backup;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.google.common.primitives.Ints;

public class BackupConfig extends ModuleConfigBase
{
	private Configuration		config;
	public static boolean		autoBackup;
	public static boolean		backupOnWorldUnload;
	public static boolean		backupIfUnloaded;
	public static String		backupName;
	public static String		backupDir;
	public static List<Integer>	whitelist;
	public static List<Integer>	blacklist;

	public static final String	CAT	= "Backup";

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

		backupOnWorldUnload = config.get(CAT, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").getBoolean(true);
		backupIfUnloaded = config.get(CAT, "backupIfUnloaded", true, "Make backups if world is not loaded.").getBoolean(true);

		String subcat = CAT + ".autoBackup";
		config.addCustomCategoryComment(subcat, "Settings for the scheduled backup system");

		autoBackup = config.get(subcat, "enable", true, "Enable backups at set intervals.").getBoolean(true);

		whitelist = Ints.asList(config.get(subcat, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").getIntList());
		blacklist = Ints.asList(config.get(subcat, "blacklist", new int[] {}, "Don't make backups of these dims. Can still be done via command.").getIntList());

		ModuleBackup.baseFolder = new File(backupDir);

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.addCustomCategoryComment(CAT, "Configure the backup system.");
		config.get(CAT, "name", "%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").value = backupName;
		config.get(CAT, "folder", "Backup", "The path to the backup folder.").value = backupDir;

		config.get(CAT, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").value = backupOnWorldUnload + "";
		config.get(CAT, "backupIfUnloaded", true, "Make backups if world is not loaded.").value = backupIfUnloaded + "";

		String subcat = CAT + ".autoBackup";
		config.addCustomCategoryComment(subcat, "Settings for the scheduled backup system");

		config.get(subcat, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").valueList = this.whitelist.toArray(new String[0]);
		config.get(subcat, "blacklist", new int[] {}, "Don't make backups of these dims. Can still be done via command.").valueList = this.blacklist.toArray(new String[0]);

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		backupName = config.get(CAT, "name", "%year-%month-%day_%hour-%min").value;
		backupDir = config.get(CAT, "backupsDir", "Backup").value;

		String subcat = CAT + ".autoBackup";

		whitelist = Ints.asList(config.get(subcat, "whitelist", new int[] {}).getIntList());
		blacklist = Ints.asList(config.get(subcat, "blacklist", new int[] {}).getIntList());

		ModuleBackup.baseFolder = new File(backupDir);
	}
}
