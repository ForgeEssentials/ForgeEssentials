package com.forgeessentials.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class)
public class ModuleBackup extends ConfigLoaderBase
{

    public static final String PERM = "fe.backup";
    public static final String PERM_NOTIFY = PERM + ".notify";

    public static final String CONFIG_CAT = "Backup";
    public static final String CONFIG_CAT_WORLDS = CONFIG_CAT + ".Worlds";

    private static ForgeConfigSpec BACKUP_CONFIG;
    private static final ConfigData data = new ConfigData("Backup", BACKUP_CONFIG, new ForgeConfigSpec.Builder());

    public static final String WORLDS_HELP = "Add world configurations in the format \"B:dim-true\"";

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

    public static Map<String, Boolean> backupOverrides = new HashMap<>();

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

    public ModuleBackup() {
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandBackup(true), dispatcher);

    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        APIRegistry.perms.registerPermission(PERM_NOTIFY, DefaultPermissionLevel.OP, "Backup notification permission");
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
        if (!FMLEnvironment.dist.isDedicatedServer() || !backupOnLoad)
            return;
        final ServerWorld world = (ServerWorld) event.getWorld();
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
        if (!FMLEnvironment.dist.isDedicatedServer() || !backupOnUnload)
            return;
        final ServerWorld world = (ServerWorld) event.getWorld();
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
    static ForgeConfigSpec.BooleanValue FEbackupDefault;
    static ForgeConfigSpec.IntValue FEbackupInterval;
    static ForgeConfigSpec.BooleanValue FEbackupOnLoad;
    static ForgeConfigSpec.BooleanValue FEbackupOnUnload;
    static ForgeConfigSpec.IntValue FEkeepBackups;
    static ForgeConfigSpec.IntValue FEdailyBackups;
    static ForgeConfigSpec.IntValue FEweeklyBackups;
    static ForgeConfigSpec.ConfigValue<String> FEbaseFolder;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEbackupOverrides;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEexludePatternValues;


    @Override
    public void bakeConfig(boolean reload)
    {
        backupDefault = FEbackupDefault.get();
        backupInterval = FEbackupInterval.get();
        backupOnLoad = FEbackupOnLoad.get();
        backupOnUnload = FEbackupOnUnload.get();
        keepBackups = FEkeepBackups.get();
        dailyBackups = FEdailyBackups.get();
        weeklyBackups = FEweeklyBackups.get();
        baseFolder = new File(FEbaseFolder.get());
        
        exludePatterns.clear();
        for (String pattern : FEexludePatternValues.get())
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
        for(String pair : FEbackupOverrides.get()) {
        	String[] parts = pair.split("-");
        	if(parts[0]==null||parts[1]==null||(!parts[1].toLowerCase().equals("true")&&!parts[1].toLowerCase().equals("false"))) {
            	throw new IllegalArgumentException("Expected diffrent from BackupOverrides field, please check your configs, got the value["+parts[1]+"]:key["+parts[0]+"]");
        	}
        	if(parts[1].toLowerCase().equals("true")) {
    			backupOverrides.put(parts[0], true);
    			continue;
        	}
        	if(parts[1].toLowerCase().equals("false")) {
        		backupOverrides.put(parts[0], false);
        		continue;
        	}
        }
        
        if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().isRunning())
            registerBackupTask();
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }

    @Override
    public void load(ForgeConfigSpec.Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("BackupModule configuration").push(CONFIG_CAT);
        FEbackupDefault = BUILDER.comment("Backup all worlds by default").define("backup_default", true);
        FEbackupInterval = BUILDER.comment("Automatic backup interval in minutes (0 to disable)").defineInRange("backup_interval", 60, 0, Integer.MAX_VALUE);
        FEbackupOnLoad = BUILDER.comment("Always backup worlds when loaded (server starts)").define("backup_on_load", true);
        FEbackupOnUnload = BUILDER.comment("Always backup when a world is unloaded").define("backup_on_unload", true);
        FEkeepBackups = BUILDER.comment("Keep at least this amount of last backups").defineInRange("keep_backups", 12, 0, Integer.MAX_VALUE);
        FEdailyBackups = BUILDER.comment("Keep at least one daily backup for this last number of last days").defineInRange("keep_daily_backups", 7, 0, Integer.MAX_VALUE);
        FEweeklyBackups = BUILDER.comment("Keep at least one daily backup for this last number of last days").defineInRange("keep_weekly_backups", 8, 0, Integer.MAX_VALUE);
        FEbaseFolder = BUILDER.comment("Folder to store the backups in. Can be anywhere writable in the file system.").define("base_folder", moduleDir.getPath());
        FEexludePatternValues = BUILDER.comment("Define file patterns (regex) that should be excluded from each backup").define("exclude_patterns", new ArrayList<String>(Arrays.asList(DEFAULT_EXCLUDE_PATTERNS)));
        BUILDER.pop();
        BUILDER.comment("World Overide Config").push(CONFIG_CAT_WORLDS);
        List<String> worlds = new ArrayList<String>();
        for(Iterator<Entry<String, Boolean>> it = new HashMap<String, Boolean>() {{put("overworld",true);}}.entrySet().iterator(); it.hasNext();) {
        	Entry<String, Boolean> world = it.next();
        	worlds.add(new String(world.getKey()+"-"+String.valueOf(world.getValue())));
        }
        FEbackupOverrides= BUILDER.comment(WORLDS_HELP).defineList("backupOverrides",worlds, ConfigBase.stringValidator);
        BUILDER.pop();

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
                    List<String> backupDims = new ArrayList<>();
                    List<ServerWorld> backupWorlds = new ArrayList<>();
                    for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels())
                        if (shouldBackup(world))
                        {
                            backupDims.add(world.dimension().location().toString());
                            backupWorlds.add(world);
                        }
                    ModuleBackup.notify(Translator.format("Starting backup of dimensions %s", StringUtils.join(backupDims, ", ")));
                    for (ServerWorld worldServer : backupWorlds)
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

    public static void backup(ServerWorld dimension)
    {
        if (backupThread != null)
        {
            ModuleBackup.notify("Backup still in progress");
            return;
        }
        final ServerWorld world = dimension;
        if (world == null)
        {
            ModuleBackup.notify(Translator.format("Dimension %s does not exist or is not loaded", dimension.dimension().toString()));
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

    protected static boolean shouldBackup(ServerWorld world)
    {
        Boolean shouldBackup = backupOverrides.get(world.dimension().location().toString());
        if (shouldBackup == null)
            return backupDefault;
        else
            return shouldBackup;
    }

    private static synchronized void backup(ServerWorld world, boolean notify)
    {
        //LoggingHandler.felog.info("Dim1"+world.dimension().location().toString());
        //LoggingHandler.felog.info("Dim2"+world.dimension().location().getPath());
        //LoggingHandler.felog.info("Dim2"+world.dimension().location().getNamespace());
        if (notify)
            notify(String.format("Starting backup of dim %s...", world.dimension().location().toString()));

        // Save world
        if (!saveWorld(world))
        {
            notify(String.format("Backup of dim %s failed: Could not save world", world.dimension().location().toString()));
            return;
        }

        // Prepare directory
        URI baseUri = ServerUtil.getWorldPath().toURI();
        File backupFile = getBackupFile(world);
        File backupDir = backupFile.getParentFile();
        if (!backupDir.exists())
            if (!backupDir.mkdirs())
            {
                notify(String.format("Backup of dim %s failed: Could not create backup directory", world.dimension().location().toString()));
                return;
            }

        // Save files
        try (FileOutputStream fileStream = new FileOutputStream(backupFile); //
                ZipOutputStream zipStream = new ZipOutputStream(fileStream);)
        {
            LoggingHandler.felog.debug(String.format("Listing files for backup of world %s", world.dimension().location().toString()));
            notify(String.format("Backup failed - Error %s : Could not do shit", "666-666-6666"));
            boolean stop = true;
            if(stop)
            	return;
            for (File file : enumWorldFiles(world, null , null ))//world.getChunkSaveLocation(), null))
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
            LoggingHandler.felog.error(String.format("Severe error during backup of dim %s", world.dimension().location().toString()));
            ex.printStackTrace();
            if (notify)
                notify(String.format("Error during backup of dim %s", world.dimension().location().toString()));
        }

        if (notify)
            notify("Backup finished");
    }

    private static List<File> enumWorldFiles(ServerWorld world, File dir, List<File> files)
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
            for (ServerWorld otherWorld : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                notify(String.format("Backup looping failed - Error %s : Could not do shit", "666-666-6666"));
                if (otherWorld.dimension() != world.dimension() )//&& otherWorld.getChunkSaveLocation().equals(file))
                    continue mainLoop;}
            for (Pattern pattern : exludePatterns)
                if (pattern.matcher(file.getName()).matches())
                    continue mainLoop;
            enumWorldFiles(world, file, files);
        }
        return files;
    }

    private static File getBackupFile(ServerWorld world)
    {
        return new File(baseFolder, String.format("%s/DIM_%s/%s.zip", //
        		world.dimension().location().getNamespace(), //
        		world.dimension().location().getPath(), //
                FILE_FORMAT.format(new Date())));
    }

    private static boolean saveWorld(ServerWorld world)
    {
        boolean oldLevelSaving = world.noSave;
        world.noSave = false;
        try
        {
            world.save((IProgressUpdate) null, oldLevelSaving, oldLevelSaving);
            return true;
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error(String.format("Could not save world %s", world.dimension().location().toString()));
            return false;
        }
        //catch (Exception e)
        //{
        //    LoggingHandler.felog.error("Error while saving world");
        //    return false;
        //}
        finally
        {
            world.noSave = oldLevelSaving;
        }
    }

    private static void cleanBackups()
    {
        File baseDir = new File(baseFolder, World.OVERWORLD.getRegistryName().getNamespace());
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
                    if (backup.getKey().after(oldestDailyBackup) || backup.getKey().after(nextDate))
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
        TextComponent messageComponent = ChatOutputHandler.notification(message);
        if (!ServerLifecycleHooks.getCurrentServer().isRunning())
            for (ServerPlayerEntity player : ServerUtil.getPlayerList())
                if (UserIdent.get(player).checkPermission(PERM_NOTIFY))
                    ChatOutputHandler.sendMessage(player.createCommandSourceStack(), messageComponent);
        ChatOutputHandler.sendMessage(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), messageComponent);
    }
}
