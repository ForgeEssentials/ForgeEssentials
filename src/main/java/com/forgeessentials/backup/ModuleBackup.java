package com.forgeessentials.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class)
public class ModuleBackup extends ConfigLoaderBase
{

    public static final String PERM = "fe.backup";
    public static final String PERM_NOTIFY = PERM + ".notify";

    public static final String CONFIG_CAT = "Backup";
    public static final String CONFIG_CAT_WORLDS = CONFIG_CAT + ".Worlds";

    public static final String WORLDS_HELP = "Add world configurations in the format \"B:1=true\"";

    private static final String EXCLUDE_PATTERNS_HELP = "Define file patterns (regex) that should be excluded from each backup";
    public static final String[] DEFAULT_EXCLUDE_PATTERNS = new String[] { "DIM-?\\d+", "FEMultiworld", "FEData_backup", "DimensionalDoors", };

    public static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

    /* ------------------------------------------------------------ */

    public static boolean backupDefault;

    public static int backupInterval;

    public static boolean backupOnUnload;

    public static boolean backupOnLoad;

    public static int keepBackups;

    public static int dailyBackups;

    public static int weeklyBackups;

    public static Map<Integer, Boolean> backupOverrides = new HashMap<>();

    public static List<Pattern> exludePatterns = new ArrayList<>();

    private static Runnable backupTask = new Runnable() {
        @Override
        public void run()
        {
            backupAll();
        }
    };

    private static Thread backupThread;

    /* ------------------------------------------------------------ */

    @FEModule.ModuleDir
    public static File moduleDir;

    public static File baseFolder;

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FECommandManager.registerCommand(new CommandBackup());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        APIRegistry.perms.registerPermission(PERM_NOTIFY, PermissionLevel.OP, "Backup notification permission");
        registerBackupTask();
        cleanBackups();
    }

    private void registerBackupTask()
    {
        TaskRegistry.remove(backupTask);
        if (backupInterval > 0)
            TaskRegistry.scheduleRepeated(backupTask, 1000 * 60 * backupInterval);
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event)
    {
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer() || !backupOnLoad)
            return;
        final WorldServer world = (WorldServer) event.world;
        if (shouldBackup(world))
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    backup(world, true);
                }
            });
            thread.start();
        }
    }

    @SubscribeEvent
    public void worldUnloadEvent(WorldEvent.Unload event)
    {
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer() || !backupOnUnload)
            return;
        final WorldServer world = (WorldServer) event.world;
        if (shouldBackup(world))
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    backup(world, true);
                }
            });
            thread.start();
        }
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        backupDefault = config.get(CONFIG_CAT, "backup_default", true, "Backup all worlds by default").getBoolean();
        backupInterval = config.get(CONFIG_CAT, "backup_interval", 60, "Automatic backup interval in minutes (0 to disable)").getInt();
        backupOnLoad = config.get(CONFIG_CAT, "backup_on_load", true, "Always backup worlds when loaded (server starts)").getBoolean();
        backupOnUnload = config.get(CONFIG_CAT, "backup_on_unload", true, "Always backup when a world is unloaded").getBoolean();
        keepBackups = config.get(CONFIG_CAT, "keep_backups", 12, "Keep at least this amount of last backups").getInt();
        dailyBackups = config.get(CONFIG_CAT, "keep_daily_backups", 7, "Keep at least one daily backup for this last number of last days").getInt();
        weeklyBackups = config.get(CONFIG_CAT, "keep_weekly_backups", 8, "Keep at least one weekly backup for this last number of weeks").getInt();
        baseFolder = new File(config.get(CONFIG_CAT, "base_folder", moduleDir.getPath(),
                "Folder to store the backups in. Can be anywhere writable in the file system.").getString());

        config.get(CONFIG_CAT_WORLDS, "0", true).getBoolean(); // Create default entry
        ConfigCategory worldCat = config.getCategory(CONFIG_CAT_WORLDS);
        worldCat.setComment(WORLDS_HELP);
        for (Entry<String, Property> world : worldCat.entrySet())
        {
            try
            {
                if (world.getValue().isBooleanValue())
                    backupOverrides.put(Integer.parseInt(world.getKey()), world.getValue().getBoolean());
            }
            catch (NumberFormatException e)
            {
                LoggingHandler.felog.error("Invalid backup override entry!");
            }
        }

        String[] exludePatternValues = config.get(CONFIG_CAT, "exclude_patterns", DEFAULT_EXCLUDE_PATTERNS, EXCLUDE_PATTERNS_HELP).getStringList();
        exludePatterns.clear();
        for (String pattern : exludePatternValues)
        {
            try
            {
                exludePatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
            }
            catch (PatternSyntaxException e)
            {
                LoggingHandler.felog.error(String.format("Invalid backup exclude pattern %s", pattern));
            }
        }

        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning())
            registerBackupTask();
    }

    /* ------------------------------------------------------------ */

    public static void backupAll()
    {
        if (backupThread != null)
            return;
        backupThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    List<Integer> backupDims = new ArrayList<>();
                    List<WorldServer> backupWorlds = new ArrayList<>();
                    for (WorldServer world : DimensionManager.getWorlds())
                        if (shouldBackup(world))
                        {
                            backupDims.add(world.provider.getDimensionId());
                            backupWorlds.add(world);
                        }
                    ModuleBackup.notify(Translator.format("Starting backup of dimensions %s", StringUtils.join(backupDims, ", ")));
                    for (WorldServer worldServer : backupWorlds)
                        backup(worldServer, false);
                    cleanBackups();
                    ModuleBackup.notify("Backup finished!");
                }
                finally
                {
                    backupThread = null;
                }
            }
        });
        backupThread.start();
    }

    public static void backup(int dimension)
    {
        if (backupThread != null)
        {
            ModuleBackup.notify("Backup still in progress");
            return;
        }
        final WorldServer world = DimensionManager.getWorld(dimension);
        if (world == null)
        {
            ModuleBackup.notify(Translator.format("Dimension %d does not exist or is not loaded", dimension));
            return;
        }
        backupThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    backup(world, true);
                    cleanBackups();
                }
                finally
                {
                    backupThread = null;
                }
            }
        });
        backupThread.start();
    }

    protected static boolean shouldBackup(WorldServer world)
    {
        Boolean shouldBackup = backupOverrides.get(world.provider.getDimensionId());
        if (shouldBackup == null)
            return backupDefault;
        else
            return shouldBackup;
    }

    private static synchronized void backup(WorldServer world, boolean notify)
    {
        if (notify)
            notify(String.format("Starting backup of dim %d...", world.provider.getDimensionId()));

        // Save world
        if (!saveWorld(world))
        {
            notify(String.format("Backup of dim %s failed: Could not save world", world.provider.getDimensionId()));
            return;
        }

        // Prepare directory
        URI baseUri = ServerUtil.getWorldPath().toURI();
        File backupFile = getBackupFile(world);
        File backupDir = backupFile.getParentFile();
        if (!backupDir.exists())
            if (!backupDir.mkdirs())
            {
                notify(String.format("Backup of dim %s failed: Could not create backup directory", world.provider.getDimensionId()));
                return;
            }

        // Save files
        try (FileOutputStream fileStream = new FileOutputStream(backupFile); //
                ZipOutputStream zipStream = new ZipOutputStream(fileStream);)
        {
            LoggingHandler.felog.info(String.format("Listing files for backup of world %d", world.provider.getDimensionId()));
            for (File file : enumWorldFiles(world, world.getChunkSaveLocation(), null))
            {
                String relativePath = baseUri.relativize(file.toURI()).getPath();
                try (FileInputStream in = new FileInputStream(file))
                {
                    ZipEntry ze = new ZipEntry(relativePath);
                    zipStream.putNextEntry(ze);
                    IOUtils.copy(in, zipStream);
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.warn(String.format("Unable to backup file %s", relativePath));
                }
            }
            zipStream.closeEntry();
        }
        catch (Exception ex)
        {
            LoggingHandler.felog.error(String.format("Severe error during backup of dim %d", world.provider.getDimensionId()));
            ex.printStackTrace();
            if (notify)
                notify(String.format("Error during backup of dim %d", world.provider.getDimensionId()));
        }

        if (notify)
            notify("Backup finished");
    }

    private static List<File> enumWorldFiles(WorldServer world, File dir, List<File> files)
    {
        if (files == null)
            files = new ArrayList<>();

        mainLoop: for (File file : dir.listFiles())
        {
            if (!file.isDirectory())
            {
                files.add(file);
                continue;
            }

            // Exclude directories of other worlds
            for (WorldServer otherWorld : DimensionManager.getWorlds())
                if (otherWorld.provider.getDimensionId() != world.provider.getDimensionId() && otherWorld.getChunkSaveLocation().equals(file))
                    continue mainLoop;
            for (Pattern pattern : exludePatterns)
                if (pattern.matcher(file.getName()).matches())
                    continue mainLoop;
            enumWorldFiles(world, file, files);
        }
        return files;
    }

    private static File getBackupFile(WorldServer world)
    {
        return new File(baseFolder, String.format("%s/DIM_%d/%s.zip", //
                world.getWorldInfo().getWorldName(), //
                world.provider.getDimensionId(), //
                FILE_FORMAT.format(new Date())));
    }

    private static boolean saveWorld(WorldServer world)
    {
        boolean oldLevelSaving = world.disableLevelSaving;
        world.disableLevelSaving = false;
        try
        {
            world.saveAllChunks(true, (IProgressUpdate) null);
            return true;
        }
        catch (MinecraftException e)
        {
            LoggingHandler.felog.error(String.format("Could not save world %d", world.provider.getDimensionId()));
            return false;
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("Error while saving world");
            return false;
        }
        finally
        {
            world.disableLevelSaving = oldLevelSaving;
        }
    }

    private static void cleanBackups()
    {
        File baseDir = new File(baseFolder, DimensionManager.getWorld(0).getWorldInfo().getWorldName());
        if (!baseDir.exists())
            return;
        for (File backupDir : baseDir.listFiles())
        {
            if (!backupDir.isDirectory())
                continue;
            SortedMap<Calendar, File> files = new TreeMap<>();
            for (File backupFile : backupDir.listFiles())
            {
                try
                {
                    Calendar date = Calendar.getInstance();
                    date.setTime(FILE_FORMAT.parse(FilenameUtils.getBaseName(backupFile.getName())));
                    files.put(date, backupFile);
                }
                catch (ParseException e)
                {
                    LoggingHandler.felog.error(String.format("Could not parse backup file %s", backupFile.getAbsolutePath()));
                }
            }

            Calendar now = Calendar.getInstance();

            Calendar oldestDailyBackup = Calendar.getInstance();
            oldestDailyBackup.set(Calendar.MILLISECOND, 0);
            oldestDailyBackup.set(Calendar.SECOND, 0);
            oldestDailyBackup.set(Calendar.HOUR_OF_DAY, 4);
            oldestDailyBackup.add(Calendar.DAY_OF_YEAR, dailyBackups <= 0 ? -1000 : -dailyBackups);

            Calendar oldestWeeklyBackup = Calendar.getInstance();
            oldestDailyBackup.set(Calendar.MILLISECOND, 0);
            oldestDailyBackup.set(Calendar.SECOND, 0);
            oldestDailyBackup.set(Calendar.HOUR_OF_DAY, 4);
            oldestWeeklyBackup.set(Calendar.DAY_OF_WEEK, 0);
            oldestWeeklyBackup.add(Calendar.WEEK_OF_YEAR, weeklyBackups <= 0 ? -1000 : -weeklyBackups);

            Calendar oldestBackup = oldestDailyBackup.before(oldestWeeklyBackup) ? oldestDailyBackup : oldestWeeklyBackup;
            Calendar currentDailyBackup = oldestDailyBackup;
            Calendar currentWeeklyBackup = oldestWeeklyBackup;

            int index = 0;
            for (Iterator<Entry<Calendar, File>> it = files.entrySet().iterator(); it.hasNext();)
            {
                Entry<Calendar, File> backup = it.next();
                if (index++ > files.size() - keepBackups)
                {
                    it.remove();
                }
                else if (backup.getKey().before(oldestBackup))
                {
                    LoggingHandler.felog.debug("Removing Old Backup {} {}", FILE_FORMAT.format(backup.getKey().getTime()), backup.getValue().getParentFile().getName());
                    if (!backup.getValue().delete())
                        LoggingHandler.felog.error(String.format("Could not delete backup file %s", backup.getValue().getAbsolutePath()));
                    it.remove();
                }
            }

            while (currentDailyBackup.before(now))
            {
                Calendar nextDate = (Calendar) currentDailyBackup.clone();
                nextDate.add(Calendar.DAY_OF_YEAR, 1);
                boolean first = true;
                for (Iterator<Entry<Calendar, File>> it = files.entrySet().iterator(); it.hasNext();)
                {
                    Entry<Calendar, File> backup = it.next();
                    if (backup.getKey().before(currentDailyBackup))
                        continue;
                    if (first)
                    {
                        first = false;
                        continue;
                    }
                    if (backup.getKey().after(nextDate))
                        break;
                    LoggingHandler.felog.debug("Removing Daily Backup {} {}", FILE_FORMAT.format(backup.getKey().getTime()), backup.getValue().getParentFile().getName());
                    if (!backup.getValue().delete())
                        LoggingHandler.felog.error(String.format("Could not delete backup file %s", backup.getValue().getAbsolutePath()));
                    it.remove();
                }
                currentDailyBackup = nextDate;
            }

            while (currentWeeklyBackup.before(now))
            {
                Calendar nextDate = (Calendar) currentWeeklyBackup.clone();
                nextDate.add(Calendar.WEEK_OF_YEAR, 1);
                boolean first = true;
                for (Iterator<Entry<Calendar, File>> it = files.entrySet().iterator(); it.hasNext();)
                {
                    Entry<Calendar, File> backup = it.next();
                    if (backup.getKey().before(currentWeeklyBackup))
                        continue;
                    if (first)
                    {
                        first = false;
                        continue;
                    }
                    if (backup.getKey().after(oldestDailyBackup) || backup.getKey().after(nextDate))
                        break;
                    LoggingHandler.felog.debug("Removing Weekly Backup {} {}", FILE_FORMAT.format(backup.getKey().getTime()), backup.getValue().getParentFile().getName());
                    if (!backup.getValue().delete())
                        LoggingHandler.felog.error(String.format("Could not delete backup file %s", backup.getValue().getAbsolutePath()));
                    it.remove();
                }
                currentWeeklyBackup = nextDate;
            }
        }
    }

    private static void notify(String message)
    {
        IChatComponent messageComponent = ChatOutputHandler.notification(message);
        if (!MinecraftServer.getServer().isServerStopped())
            for (EntityPlayerMP player : ServerUtil.getPlayerList())
                if (UserIdent.get(player).checkPermission(PERM_NOTIFY))
                    ChatOutputHandler.sendMessage(player, messageComponent);
        ChatOutputHandler.sendMessage(MinecraftServer.getServer(), messageComponent);
    }

}
