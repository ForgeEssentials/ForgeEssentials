package com.ForgeEssentials.backup;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;

public class BackupConfig implements IModuleConfig
{
	private File file;
	private Configuration config;
	public static File backupDir;

	public BackupConfig()
	{
		file = new File(ForgeEssentials.FEDIR, "backups.cfg");
	}

	@Override
	public void setGenerate(boolean generate)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min",
				"The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value;
		String backupdir = config.get("Backups", "folder", "backups/", "The path to the backup folder. This is relative to the ForgeEssentials folder").value;

		File dir = new File(ForgeEssentials.FEDIR, backupdir);
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		backupDir = dir;

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min",
				"The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value = BackupThread.backupName;

		// TDOD: may be bad....
		String dir = backupDir.getPath();
		dir = dir.replace(ForgeEssentials.FEDIR.getPath(), "");
		config.get("Backups", "folder", "backups/", "The path to the backup folder. This is relative to the ForgeEssentials folder").value = dir;

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		BackupThread.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min").value;
		String backupdir = config.get("Backups", "folder", "backups/").value;

		File dir = new File(ForgeEssentials.FEDIR, backupdir);
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		backupDir = dir;
	}

	@Override
	public File getFile()
	{
		return file;
	}
}
