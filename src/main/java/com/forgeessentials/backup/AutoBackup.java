package com.forgeessentials.backup;

import com.forgeessentials.util.OutputHandler;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TimerTask;

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
                checkMaxFilesPerFolder(ModuleBackup.baseFolder);
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
                loopThroughFolder(folder);

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
                try
                {
                    Files.delete(file.toPath());
                    OutputHandler.felog.info("Deleted file " + file.getPath());
                }
                catch (IOException e)
                {
                    OutputHandler.felog.severe("Could not delete file");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void checkMaxFilesPerFolder(File directory)
    {
        File[] folders = getFolderList(directory);

        for (File folder : folders)
        {
            checkMaxFilesPerFolder(folder);
            LinkedList<File> fileList = new LinkedList<File>(Arrays.asList(sortByLastModified(folder)));

            int trys = 0;
            while (fileList.size() > BackupConfig.maxfilesperbackupfolder && trys < 5)
            {
                File toDelete = fileList.remove();
                if (toDelete.isDirectory())
                {
                    continue;
                }
                try
                {
                    Files.delete(toDelete.toPath());
                    OutputHandler.felog.info("Deleted file " + toDelete.getPath());
                }
                catch (IOException e)
                {
                    OutputHandler.felog.severe("Try #" + trys + "Removed file: " + toDelete.getAbsolutePath());
                    OutputHandler.felog.severe("Could not delete file");
                    trys++;
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

                OutputHandler.debug("try " + trys);
                File[] folders = getFolderList(ModuleBackup.baseFolder);

                for (File folder : folders)
                {

                    LinkedList<File> fileList = new LinkedList<File>(Arrays.asList(folder));
                    File toDelete = fileList.remove();
                    if (toDelete.isDirectory())
                    {
                        continue;
                    }
                    try
                    {
                        Files.delete(toDelete.toPath());
                        OutputHandler.felog.info("Deleted file " + toDelete.getPath());
                    }
                    catch (IOException e)
                    {
                        OutputHandler.felog.severe("Try #" + trys + "Removed file: " + toDelete.getAbsolutePath());
                        OutputHandler.felog.severe("Could not delete file");
                        e.printStackTrace();
                        trys++;
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

    /**
     * returns oldest files first
     *
     * @param folder
     * @return
     */
    public static File[] sortByLastModified(File folder)
    {
        File[] files = folder.listFiles();

        Arrays.sort(files, new Comparator<File>()
        {
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        return files;
    }
}
