package com.forgeessentials.core;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.CommandFEDebug;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.HelpFixer;
import com.forgeessentials.core.commands.selections.CommandDeselect;
import com.forgeessentials.core.commands.selections.CommandExpand;
import com.forgeessentials.core.commands.selections.CommandExpandY;
import com.forgeessentials.core.commands.selections.CommandPos;
import com.forgeessentials.core.commands.selections.CommandWand;
import com.forgeessentials.core.commands.selections.SelectionEventHandler;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.compat.Environment;
import com.forgeessentials.core.compat.WorldEditNotifier;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.LoginMessage;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigManager;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.core.network.S0PacketHandshake;
import com.forgeessentials.core.network.S1PacketSelectionUpdate;
import com.forgeessentials.core.preloader.FEModContainer;
import com.forgeessentials.data.ForgeConfigDataDriver;
import com.forgeessentials.data.NBTDataDriver;
import com.forgeessentials.data.SQLDataDriver;
import com.forgeessentials.data.StorageManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.typeInfo.TypeInfoItemStack;
import com.forgeessentials.data.typeInfo.TypeInfoNBTCompound;
import com.forgeessentials.data.typeInfo.TypeInfoNBTTagList;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.FEChunkLoader;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.MiscEventHandler;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEModuleEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ForgeEssentialsEventFactory;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Main mod class
 */

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = FEModContainer.version, acceptableRemoteVersions = "*", dependencies = "required-after:Forge@[10.13.1.1219,)")
public class ForgeEssentials extends ConfigLoaderBase {

    public static final String CONFIG_CAT = "Core";
    public static final String CONFIG_CAT_MISC = "Core.Misc";
    public static final String CONFIG_CAT_MODULES = "Core.Modules";

    @Instance(value = "ForgeEssentials")
    public static ForgeEssentials instance;

    private File FEDIR;

    private ConfigManager configManager;

    private boolean debugMode = false;

    public static boolean versionCheck = true;

    public static boolean preload;

    public static String modlistLocation;

    public static boolean mcstats;

    public ModuleLauncher moduleLauncher;

    private TaskRegistry tasks;

    @SuppressWarnings("unused")
    private RespawnHandler respawnHandler;

    @SuppressWarnings("unused")
    private SelectionEventHandler wandHandler;

    @SuppressWarnings("unused")
    private MiscEventHandler miscEventHandler;

    @SuppressWarnings("unused")
    private ForgeEssentialsEventFactory factory;

    @SuppressWarnings("unused")
    private WorldEditNotifier worldEditNotifier;

    @SuppressWarnings("unused")
    private TeleportHelper teleportHelper;
    
    // static FE-module flags / variables
    public static boolean worldEditCompatilityPresent = false;

    public ForgeEssentials()
    {
        tasks = new TaskRegistry();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");
        OutputHandler.felog.info("Initializing ForgeEssentials version " + FEModContainer.version + " (configDir = " + FEDIR.getAbsolutePath() + ")");

        // Check environment
        Environment.check();

        // Load configuration
        configManager = new ConfigManager(FEDIR, "main");
        configManager.registerLoader(configManager.getMainConfigName(), this);
        configManager.registerLoader(configManager.getMainConfigName(), new OutputHandler());

        // Initialize data-API
        StorageManager storageManager = new StorageManager(configManager.getConfig("DataStorage"));
        DataStorageManager.manager = storageManager;
        DataStorageManager.registerDriver("ForgeConfig", ForgeConfigDataDriver.class);
        DataStorageManager.registerDriver("NBT", NBTDataDriver.class);
        DataStorageManager.registerDriver("SQL_DB", SQLDataDriver.class);
        registerDataTypes();
        storageManager.setupManager();

        // Load network packages
        FunctionHelper.netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");
        FunctionHelper.netHandler.registerMessage(S0PacketHandshake.class, S0PacketHandshake.class, 0, Side.SERVER);
        FunctionHelper.netHandler.registerMessage(S1PacketSelectionUpdate.class, S1PacketSelectionUpdate.class, 1, Side.CLIENT);

        // Misc
        miscEventHandler = new MiscEventHandler();
        LoginMessage.loadFile();

        // Load modules
        moduleLauncher = new ModuleLauncher();
        moduleLauncher.preLoad(e);
    }

    public void registerDataTypes()
    {
        // Register data types
        DataStorageManager.registerSaveableType(PlayerInfo.class);

        DataStorageManager.registerSaveableType(Point.class);
        DataStorageManager.registerSaveableType(WorldPoint.class);
        DataStorageManager.registerSaveableType(WarpPoint.class);

        DataStorageManager.registerSaveableType(TypeInfoItemStack.class, new ClassContainer(ItemStack.class));
        DataStorageManager.registerSaveableType(TypeInfoNBTCompound.class, new ClassContainer(NBTTagCompound.class));
        DataStorageManager.registerSaveableType(TypeInfoNBTTagList.class, new ClassContainer(NBTTagList.class));
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        // MinecraftForge.EVENT_BUS.register(this);
        // FMLCommonHandler.instance().bus().register(this);

        // other stuff
        factory = new ForgeEssentialsEventFactory();
        respawnHandler = new RespawnHandler();
        wandHandler = new SelectionEventHandler();
        teleportHelper = new TeleportHelper();

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleInitEvent(e));
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModulePostInitEvent(e));
    }

    @EventHandler
    public void serverPreInit(FMLServerAboutToStartEvent e)
    {
        DataManager.setInstance(new DataManager(new File(FunctionHelper.getWorldPath(), "FEData/json")));
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleServerPreInitEvent(e));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        // load up DataAPI
        ((StorageManager) DataStorageManager.manager).serverStart(e);

        BlockModListFile.makeModList();

        // commands
        e.registerServerCommand(new HelpFixer());

        new CommandFEInfo().register();
        new CommandFEDebug().register();

        if (!worldEditCompatilityPresent)
        {
            new CommandPos(1).register();
            new CommandPos(2).register();
            new CommandWand().register();
            new CommandDeselect().register();
            new CommandExpand().register();
            new CommandExpandY().register();
        }

        worldEditNotifier = new WorldEditNotifier();

        tasks.onServerStart();

        ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerInitEvent(e));
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent e)
    {
        CommandSetChecker.remove();

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerPostInitEvent(e));
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent e)
    {
        tasks.onServerStop();
        PlayerInfo.saveAll();
        PlayerInfo.clear();

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerStopEvent(e));
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleServerStoppedEvent(e));
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CONFIG_CAT, "Configure ForgeEssentials Core.");
        config.addCustomCategoryComment(CONFIG_CAT_MODULES, "Enable/disable modules here.");

        versionCheck = config.get(CONFIG_CAT, "versionCheck", true, "Check for newer versions of ForgeEssentials on load?").getBoolean(true);
        configManager.setUseCanonicalConfig(config.get(CONFIG_CAT, "canonicalConfigs", false, 
                "For modules that support it, place their configs in this file.").getBoolean(false));
        modlistLocation = config.get(CONFIG_CAT, "modlistLocation", "modlist.txt",
                "Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.").getString();
        debugMode = config.get(CONFIG_CAT, "debug", false, "Activates developer debug mode. Spams your FML logs.").getBoolean(false);

        // ----------------------------------------
        // Other global configurations options
        
        CommandSetChecker.removeDuplicateCommands = config.get(CONFIG_CAT, "removeDuplicateCommands", true,
                "Remove commands from the list if they already exist outside of FE.").getBoolean(true);
        PlayerInfo.persistSelections = config.get(CONFIG_CAT, "persistSelections", false,
                "Switch to true if you want selections to persist between user sessions. Has no effect when WEIntegrationTools is installed.").getBoolean(false);
        MiscEventHandler.MajoritySleep = config.get(CONFIG_CAT_MISC, "MajoritySleep", true, "If +50% of players sleep, make it day.").getBoolean(true);
    }

    public static boolean canLoadModule(String moduleName)
    {
        return getConfigManager().getMainConfig().get("Core.Modules", moduleName, true).getBoolean(true);
    }

    public static ConfigManager getConfigManager()
    {
        return instance.configManager;
    }

    public static File getFEDirectory()
    {
        return instance.FEDIR;
    }

    public static boolean isDebugMode()
    {
        return instance.debugMode;
    }

    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
    }

}
