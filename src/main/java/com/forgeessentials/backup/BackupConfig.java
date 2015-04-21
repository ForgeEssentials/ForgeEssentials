package com.forgeessentials.backup;

import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.google.common.primitives.Ints;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BackupConfig extends ConfigLoaderBase {

    private static final String MAIN = "Backup";
    private static final String AUTOBACKUP = MAIN + ".autoBackup";
    private static final String AUTOREMOVE = MAIN + ".autoRemove";
    // Main
    public static String backupName;
    public static String backupDir;
    public static boolean backupOnWorldUnload;
    public static boolean backupIfUnloaded;
    public static boolean enableMsg;
    // AutoBackup
    public static Integer autoInterval;
    public static Integer worldSaveInterval;
    public static boolean worldSaving = true;// lock this
    public static List<Integer> whitelist;
    public static List<Integer> blacklist;
    public static List<String> extraFolders;
    // AutoRemove
    public static boolean enableAutoRemove;
    public static Integer minimunFreeSpace;
    public static Integer maxfilesperbackupfolder;
    public static Integer maxBackupLifespan;

    @Override
    public void load(Configuration config, boolean isReload)
    {
        /*
         * Main cat
         */
        config.addCustomCategoryComment(MAIN, "Configure the backup system.");
        backupName = config.get(MAIN, "name", "%name_%year-%month-%day_%hour-%min",
                "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").getString();
        backupDir = config.get(MAIN, "backupsDir", "ForgeEssentials/Backups", "The path to the backup folder.").getString();
        backupOnWorldUnload = config.get(MAIN, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").getBoolean(true);
        backupIfUnloaded = config.get(MAIN, "backupIfUnloaded", true, "Make backups if world is not loaded.").getBoolean(true);
        enableMsg = config.get(MAIN, "enableMsg", true, "Send a message to eveyone with Permission: \"ForgeEssentials.backup.msg\"").getBoolean(true);

        /*
         * Lang
         */

        String sub = MAIN + ".lang";
        config.addCustomCategoryComment(sub, "Configure messages here.");

        WorldSaver.start = config.get(sub + ".AutoWorldSave", "start", "Saving world %1$s").getString();
        WorldSaver.done = config.get(sub + ".AutoWorldSave", "done", "Done saving world %1$s").getString();
        WorldSaver.failed = config.get(sub + ".AutoWorldSave", "failed", "%1$s failed to save!").getString();

        ModuleBackup.baseFolder = new File(backupDir);

        /*
         * Subcat autoBackup
         */
        config.addCustomCategoryComment(AUTOBACKUP, "Settings for the scheduled backup system");

        autoInterval = config.get(AUTOBACKUP, "interval", 30, "Interval in minutes. 0 to disable").getInt();
        worldSaveInterval = config.get(AUTOBACKUP, "worldSaveInterval", 10, "Does a save-all every X minutes. 0 to disable").getInt();
        whitelist = Ints.asList(config.get(AUTOBACKUP, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").getIntList());
        blacklist = Ints.asList(config.get(AUTOBACKUP, "blacklist", new int[] {}, "Don't make automatic backups of these dims. Can still be done via command.")
                .getIntList());
        extraFolders = Arrays.asList(config.get(AUTOBACKUP, "extraFolders", new String[] { "" },
                "Make a backup of these folders every autoBackup. Relative to server.jar").getStringList());

        /*
         * Subcat autoRemove
         */
        config.addCustomCategoryComment(AUTOREMOVE, "Settings for the autoremoval of old backups. Some settings may not work, use with caution.");

        enableAutoRemove = config.get(AUTOREMOVE, "enable", true, "Automaticly remove old backups").getBoolean(true);
        minimunFreeSpace = config.get(AUTOREMOVE, "minimunFreeSpace", -1,
                "Minimum of free space that needs to remain on the HDD the server is on. Value in GB. -1 disables this criteria.").getInt();
        maxfilesperbackupfolder = config.get(AUTOREMOVE, "maxfilesperbackupfolder", -1,
                "Maximum amout of backups per folder or world. -1 to disable this criteria.").getInt();
        maxBackupLifespan = config.get(AUTOREMOVE, "maxBackupLifespan", 168, "Time in hours a backup may last. -1 to disable this criteria.").getInt();
    }

    @Override
    public void save(Configuration config)
    {
        /*
         * Main cat
         */
        config.addCustomCategoryComment(MAIN, "Configure the backup system.");
        config.get(MAIN, "name", "%name_%year-%month-%day_%hour-%min",
                "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %name").set(backupName);
        config.get(MAIN, "backupsDir", "ForgeEssentials/Backups", "The path to the backup folder.").set(backupDir);
        config.get(MAIN, "backupOnWorldUnload", true, "Make a backup when a dim unloads.").set(backupOnWorldUnload);
        config.get(MAIN, "backupIfUnloaded", true, "Make backups if world is not loaded.").set(backupIfUnloaded);
        config.get(MAIN, "enableMsg", true, "Send a message to eveyone with Permission: \"ForgeEssentials.backup.msg\"").set(enableMsg);

        ModuleBackup.baseFolder = new File(backupDir);

        /*
         * Subcat autoBackup
         */
        config.addCustomCategoryComment(AUTOBACKUP, "Settings for the scheduled backup system");

        config.get(AUTOBACKUP, "interval", 30, "Interval in minutes. 0 to disable").set(autoInterval);
        config.get(AUTOBACKUP, "worldSaveInterval", 10, "Does a save-all every X minutes. 0 to disable").set(worldSaveInterval);
        config.get(AUTOBACKUP, "whitelist", new int[] {}, "Always make a backup of these dims. Even when empty.").set(whitelist.toArray(new String[0]));
        config.get(AUTOBACKUP, "blacklist", new int[] {}, "Don't make automatic backups of these dims. Can still be done via command.").set(
                blacklist.toArray(new String[0]));
        config.get(AUTOBACKUP, "extraFolders", new String[] { "" }, "Make a backup of these folders every autoBackup. Relative to server.jar").set(
                extraFolders.toArray(new String[0]));

        /*
         * Subcat autoRemove
         */
        config.addCustomCategoryComment(AUTOREMOVE, "Settings for the autoremoval of old backups");

        config.get(AUTOREMOVE, "enable", true, "Automaticly remove old backups").set(enableAutoRemove);
        config.get(AUTOREMOVE, "minimunFreeSpace", -1,
                "Minimum of free space that needs to remain on the HDD the server is on. Value in GB. -1 disables this criteria.").set(minimunFreeSpace);
        config.get(AUTOREMOVE, "maxfilesperbackupfolder", -1, "Maximum amout of backups per folder or world. -1 to disable this criteria.").set(
                maxfilesperbackupfolder);
        config.get(AUTOREMOVE, "maxBackupLifespan", 168, "Time in hours a backup may last. -1 to disable this criteria.").set(maxBackupLifespan);
    }

}
