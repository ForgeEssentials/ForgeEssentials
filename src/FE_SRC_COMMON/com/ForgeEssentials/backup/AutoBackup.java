package com.ForgeEssentials.backup;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

import com.ForgeEssentials.util.OutputHandler;

public class AutoBackup implements Runnable
{
	private Thread			thread;
	public static boolean	isBackingUp = false;
	
	public AutoBackup()
	{	
		thread = new Thread(this, "ForgeEssentials - AutoBackup");
		thread.start();
	}
	
	@Override
	public void run()
	{	
		while(MinecraftServer.getServer().isServerRunning() && BackupConfig.autoInterval != 0)
		{
			try {thread.sleep(BackupConfig.autoInterval * 1000 * 60);}
			catch (InterruptedException e) {e.printStackTrace();}
			
			while(AutoWorldSave.isSaving)
			{
				try {thread.sleep(1000);}
				catch (InterruptedException e) {e.printStackTrace();}
			}
			
			isBackingUp = true;
			List<Integer> list = Arrays.asList(DimensionManager.getIDs());
			
			for(int i : BackupConfig.blacklist)
			{
				list.remove(i);
			}
			
			for(int i : BackupConfig.whitelist)
			{
				if(!list.contains(i))
				{
					list.add(i);
				}
			}
			
			for(int i : list)
			{
				Backup backup = new Backup(i, true);
				while(!backup.isDone())
				{
					try {thread.sleep(1000);}
					catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			
			for(String folder : BackupConfig.extraFolders)
			{
				Backup backup = new Backup(new File(folder));
				while(!backup.isDone())
				{
					try {thread.sleep(1000);}
					catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			
			isBackingUp = false;
			
			if(BackupConfig.enableAutoRemove)
			{
				if(BackupConfig.minimunFreeSpace != -1)
				{
					diskSpaceCheck();
				}
				if(BackupConfig.maxfilesperbackupfolder != -1)
				{
					checkMaxFilesPerFolder();
				}
				if(BackupConfig.maxBackupLifespan != -1)
				{
					checkMaxFBackupLifespan();
				}
			}
		}
	}
	
	public static void checkMaxFBackupLifespan()
	{
		File[] folders = getFolderList(ModuleBackup.baseFolder);
		
		Long time = System.currentTimeMillis();
		
		for(File folder : folders)
		{
			for(File file : folder.listFiles())
			{
				if(time > file.lastModified() + (BackupConfig.maxBackupLifespan * 3600000))
				{
					OutputHandler.debug("Removed file: " + file.getAbsolutePath());
					file.delete();
				}
			}
		}
	}
	
	public static void checkMaxFilesPerFolder()
	{
		File[] folders = getFolderList(ModuleBackup.baseFolder);
		
		for(File folder : folders)
		{
			int trys = 0;
			while((folder.list().length > BackupConfig.maxfilesperbackupfolder) && trys < 5)
			{
				trys ++;
				File file = lastFileModified(folder);
				OutputHandler.debug("Removed file: " + file.getAbsolutePath());
				file.delete();
			}
		}
	}
	
	public static void diskSpaceCheck()
	{
		if((ModuleBackup.baseFolder.getFreeSpace() / 1024 / 1024 / 1024) < BackupConfig.minimunFreeSpace)
		{
			OutputHandler.warning("Low disk space. Removing old backups.");
			
			int trys = 0;
			while(((ModuleBackup.baseFolder.getFreeSpace() / 1024 / 1024 / 1024) < BackupConfig.minimunFreeSpace) && trys < 5)
			{
				trys ++;
				OutputHandler.debug("try " + trys);
				File[] folders = getFolderList(ModuleBackup.baseFolder);
				
				for(File folder : folders)
				{
					File file = lastFileModified(folder);
					OutputHandler.debug("Removed file: " + file.getAbsolutePath());
					file.delete();
				}
			}
		}
	}
	
	public static File[] getFolderList(File baseFolder)
	{
		return baseFolder.listFiles(new FileFilter() 
		{			
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
