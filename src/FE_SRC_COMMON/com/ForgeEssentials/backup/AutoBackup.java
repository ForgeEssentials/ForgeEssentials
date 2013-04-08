package com.ForgeEssentials.backup;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;

import net.minecraftforge.common.DimensionManager;

import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.tasks.TaskRegistry;

public class AutoBackup implements Runnable
{
	public static boolean	isBackingUp	= false;

	public AutoBackup()
	{
		TaskRegistry.registerRecurringTask(this, 0, BackupConfig.autoInterval, 0, 0, 0, BackupConfig.autoInterval, 0, 0);
	}

	@Override
	public void run()
	{
		while (WorldSaver.isSaving())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		isBackingUp = true;

		HashSet<Integer> list = new HashSet<Integer>();
		list.addAll(Arrays.asList(DimensionManager.getIDs()));
		list.removeAll(BackupConfig.blacklist);
		list.addAll(BackupConfig.whitelist);

		for (int i : list)
		{
			Backup backup = new Backup(i, true);
			backup.startThread();
			while (!backup.isDone())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
		}

		for (String folder : BackupConfig.extraFolders)
		{
			Backup backup = new Backup(new File(folder));
			while (!backup.isDone())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
		}

		isBackingUp = false;

		if (BackupConfig.enableAutoRemove)
		{
			if (BackupConfig.minimunFreeSpace != -1)
			{
				diskSpaceCheck();
			}
			if (BackupConfig.maxfilesperbackupfolder != -1)
			{
				checkMaxFilesPerFolder();
			}
			if (BackupConfig.maxBackupLifespan != -1)
			{
				checkMaxFBackupLifespan();
			}
		}

		System.gc();
	}

	public static void checkMaxFBackupLifespan()
	{
		File[] folders = getFolderList(ModuleBackup.baseFolder);

		Long time = System.currentTimeMillis();

		for (File folder : folders)
		{
			for (File file : folder.listFiles())
			{
				if (time > file.lastModified() + BackupConfig.maxBackupLifespan * 3600000)
				{
					OutputHandler.debug("Removed file: " + file.getAbsolutePath());
					try
					{
						Files.delete(file.toPath());
					}
					catch (IOException e)
					{
						OutputHandler.severe("Why you no delete file? "+file);
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void checkMaxFilesPerFolder()
	{
		File[] folders = getFolderList(ModuleBackup.baseFolder);

		for (File folder : folders)
		{
			int trys = 0;
			while (folder.list().length > BackupConfig.maxfilesperbackupfolder && trys < 5)
			{
				trys++;
				File file = lastFileModified(folder);
				OutputHandler.debug("Try #" + trys + "Removed file: " + file.getAbsolutePath());
				try
				{
					Files.delete(file.toPath());
				}
				catch (IOException e)
				{
					OutputHandler.severe("Try #" + trys + "Removed file: " + file.getAbsolutePath());
					OutputHandler.severe("Why you no delete file?");
					e.printStackTrace();
				}
			}
		}
	}

	public static void diskSpaceCheck()
	{
		if (ModuleBackup.baseFolder.getFreeSpace() / 1024 / 1024 / 1024 < BackupConfig.minimunFreeSpace)
		{
			OutputHandler.warning("Low disk space. Removing old backups.");

			int trys = 0;
			while (ModuleBackup.baseFolder.getFreeSpace() / 1024 / 1024 / 1024 < BackupConfig.minimunFreeSpace && trys < 5)
			{
				trys++;
				OutputHandler.debug("try " + trys);
				File[] folders = getFolderList(ModuleBackup.baseFolder);

				for (File folder : folders)
				{
					File file = lastFileModified(folder);
					OutputHandler.debug("Removed file: " + file.getAbsolutePath());
					try
					{
						Files.delete(file.toPath());
					}
					catch (IOException e)
					{
						OutputHandler.severe("Try #" + trys + "Removed file: " + file.getAbsolutePath());
						OutputHandler.severe("Why you no delete file?");
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static File[] getFolderList(File baseFolder)
	{
		return baseFolder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.isDirectory();
			}
		});
	}

	public static File lastFileModified(File folder)
	{
		File[] files = folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choise = null;
		for (File file : files)
		{
			if (file.lastModified() > lastMod)
			{
				choise = file;
				lastMod = file.lastModified();
			}
		}
		return choise;
	}
}
