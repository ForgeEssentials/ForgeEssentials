package com.forgeessentials.core;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import com.forgeessentials.core.preloader.classloading.FEClassLoader;
import cpw.mods.fml.common.event.*;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.VersionUtils;
import com.forgeessentials.compat.CompatReiMinimap;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.HelpFixer;
import com.forgeessentials.core.environment.CommandSetChecker;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TickTaskHandler;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigManager;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.core.network.S0PacketHandshake;
import com.forgeessentials.core.network.S1PacketSelectionUpdate;
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
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.selections.CommandDeselect;
import com.forgeessentials.util.selections.CommandExpand;
import com.forgeessentials.util.selections.CommandExpandY;
import com.forgeessentials.util.selections.CommandPos;
import com.forgeessentials.util.selections.CommandWand;
import com.forgeessentials.util.selections.SelectionEventHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import org.spongepowered.asm.mixin.MixinEnvironment;

/**
 * Main mod class
 */

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = ForgeEssentials.FEVERSION, acceptableRemoteVersions = "*", dependencies = "required-after:Forge@[10.13.2.1258,);after:WorldEdit")
public class ForgeEssentials extends ConfigLoaderBase
{

    public static final String CONFIG_CAT = "Core";
    public static final String CONFIG_CAT_MISC = "Core.Misc";
    public static final String CONFIG_CAT_MODULES = "Core.Modules";

    public static final String FEVERSION = "%VERSION%";

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

    public static File jarLocation;

    @SuppressWarnings("unused")
    private TaskRegistry tasks;

    @SuppressWarnings("unused")
    private SelectionEventHandler wandHandler;

    @SuppressWarnings("unused")
    private MiscEventHandler miscEventHandler;

    @SuppressWarnings("unused")
    private ForgeEssentialsEventFactory factory;

    @SuppressWarnings("unused")
    private TeleportHelper teleportHelper;

    @SuppressWarnings("unused")
    private TickTaskHandler tickTaskHandler;

    @SuppressWarnings("unused")
    private Questioner questioner;

    public static ASMDataTable asmData;

    public ForgeEssentials()
    {
        // Check environment
        Environment.check();
    }

    @EventHandler
    public void classLoad(FMLConstructionEvent e)
    {
        new FEClassLoader().extractLibs(Launch.minecraftHome, Launch.classLoader);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        asmData = e.getAsmData();
        jarLocation = e.getSourceFile();
        
        FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");
        OutputHandler.felog.info("Initializing ForgeEssentials version " + FEVERSION + " (configDir = " + FEDIR.getAbsolutePath() + ")");
        OutputHandler.felog.info("Build information: Build number is: " + VersionUtils.getBuildNumber(jarLocation) + ", build hash is: " + VersionUtils.getBuildHash(jarLocation));

        // Load configuration
        configManager = new ConfigManager(FEDIR, "main");
        configManager.registerLoader(configManager.getMainConfigName(), this);
        configManager.registerLoader(configManager.getMainConfigName(), new OutputHandler());

        tasks = new TaskRegistry();

        // Load network packages
        FunctionHelper.netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");
        FunctionHelper.netHandler.registerMessage(S0PacketHandshake.class, S0PacketHandshake.class, 0, Side.SERVER);
        FunctionHelper.netHandler.registerMessage(S1PacketSelectionUpdate.class, S1PacketSelectionUpdate.class, 1, Side.CLIENT);

        // Misc
        miscEventHandler = new MiscEventHandler();

        // Load modules
        moduleLauncher = new ModuleLauncher();
        moduleLauncher.preLoad(e);
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        FMLCommonHandler.instance().bus().register(this);

        Translator.load();

        // other stuff
        factory = new ForgeEssentialsEventFactory();
        wandHandler = new SelectionEventHandler();
        teleportHelper = new TeleportHelper();
        tickTaskHandler = new TickTaskHandler();
        FunctionHelper.FE_INTERNAL_EVENTBUS.register(new CompatReiMinimap());

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleInitEvent(e));
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModulePostInitEvent(e));
    }

    /* ------------------------------------------------------------ */

    @EventHandler
    public void serverPreInit(FMLServerAboutToStartEvent e)
    {
        questioner = new Questioner();
        DataManager.setInstance(new DataManager(new File(FunctionHelper.getWorldPath(), "FEData/json")));
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleServerPreInitEvent(e));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        BlockModListFile.makeModList();
        BlockModListFile.dumpFMLRegistries();

        // commands
        e.registerServerCommand(new HelpFixer());

        new CommandFEInfo().register();
        new CommandWand().register();

        if (!ModuleLauncher.getModuleList().contains("WEIntegrationTools"))
        {
            new CommandPos(1).register();
            new CommandPos(2).register();
            new CommandDeselect().register();
            new CommandExpand().register();
            new CommandExpandY().register();
        }

        ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerInitEvent(e));

        registerPermissions();
    }

    protected void registerPermissions()
    {
        APIRegistry.perms.registerPermission("mc.help", RegisteredPermValue.TRUE, "Help command");

        // Teleport
        APIRegistry.perms.registerPermissionProperty(TeleportHelper.TELEPORT_COOLDOWN, "5", "Allow bypassing teleport cooldown");
        APIRegistry.perms.registerPermissionProperty(TeleportHelper.TELEPORT_WARMUP, "3", "Allow bypassing teleport warmup");
        APIRegistry.perms.registerPermissionPropertyOp(TeleportHelper.TELEPORT_COOLDOWN, "0");
        APIRegistry.perms.registerPermissionPropertyOp(TeleportHelper.TELEPORT_WARMUP, "0");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_FROM, RegisteredPermValue.TRUE, "Allow bypassing teleport cooldown");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_TO, RegisteredPermValue.TRUE, "Allow bypassing teleport warmup");
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
        PlayerInfo.saveAll();
        PlayerInfo.clear();
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerStopEvent(e));
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleServerStoppedEvent(e));
        Translator.save();
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        UserIdent.register(event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerLoggedOutEvent event)
    {
        UserIdent.unregister(event.player);
        PlayerInfo.discardInfo(event.player.getPersistentID());
    }

    /* ------------------------------------------------------------ */

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CONFIG_CAT, "Configure ForgeEssentials Core.");
        config.addCustomCategoryComment(CONFIG_CAT_MODULES, "Enable/disable modules here.");

        versionCheck = config.get(CONFIG_CAT, "versionCheck", true, "Check for newer versions of ForgeEssentials on load?").getBoolean(true);
        configManager.setUseCanonicalConfig(config.get(CONFIG_CAT, "canonicalConfigs", false, "For modules that support it, place their configs in this file.")
                .getBoolean(false));
        modlistLocation = config.get(CONFIG_CAT, "modlistLocation", "modlist.txt",
                "Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.").getString();
        debugMode = config.get(CONFIG_CAT, "debug", false, "Activates developer debug mode. Spams your FML logs.").getBoolean(false);

        // ----------------------------------------
        // Other global configurations options

        CommandSetChecker.removeDuplicateCommands = config.get(CONFIG_CAT, "removeDuplicateCommands", true,
                "Remove commands from the list if they already exist outside of FE.").getBoolean(true);
        PlayerInfo.persistSelections = config.get(CONFIG_CAT, "persistSelections", false,
                "Switch to true if you want selections to persist between user sessions. Has no effect when WEIntegrationTools is installed.")
                .getBoolean(false);
        MiscEventHandler.MajoritySleep = config.get(CONFIG_CAT_MISC, "MajoritySleep", true, "If a majority of players sleep, make it day.").getBoolean(true);
        MiscEventHandler.majoritySleepThreshold = config.get(CONFIG_CAT_MISC, "MajoritySleepThreshold", 50,
                "Define the percentage of players that constitutes a majority for MajoritySleep to kick in.").getInt(50);
        MiscEventHandler.checkSpacesInNames = config.get(CONFIG_CAT_MISC, "CheckSpacesInNames", true,
                "Check if a player's name contains spaces (can gum up some things in FE)").getBoolean();
    }

    /* ------------------------------------------------------------ */

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
