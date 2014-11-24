package com.forgeessentials.backup;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TimerTask;

import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.util.OutputHandler;

public class AutoBackup extends TimerTask
{
    public static boolean isBackingUp = false;

    public AutoBackup()
    {
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
            if (!folder.equals(""))
            {
                Backup backup = new Backup(new File(folder));
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

        for (File folder : folders)
        {
            if (folder.isDirectory())
            {
                for (File folder1 : folders)
                {
                    loopThroughFolder(folder1);
                }
            }
            loopThroughFolder(folder);
        }
    }

    private static void loopThroughFolder(File folder)
    {
        File[] files = folder.listFiles();

        Arrays.sort(files, new Comparator<File>()
        {
            @Override
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        for (File file : files)
        {
            if (System.currentTimeMillis() > file.lastModified() + BackupConfig.maxBackupLifespan * 3600000)
            {
                file.delete();
                OutputHandler.debug("Removed file: " + file.getAbsolutePath());
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
                    OutputHandler.felog.severe("Try #" + trys + "Removed file: " + file.getAbsolutePath());
                    OutputHandler.felog.severe("Why you no delete file?");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void diskSpaceCheck()
    {
        if (ModuleBackup.baseFolder.getFreeSpace() / 1024 / 1024 / 1024 < BackupConfig.minimunFreeSpace)
        {
            OutputHandler.felog.warning("Low disk space. Removing old backups.");

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
                        OutputHandler.felog.severe("Try #" + trys + "Removed file: " + file.getAbsolutePath());
                        OutputHandler.felog.severe("Why you no delete file?");
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
