package com.forgeessentials.core;

import java.io.File;

import com.forgeessentials.compat.HelpFixer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.VersionUtils;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.compat.CompatReiMinimap;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.CommandUuid;
import com.forgeessentials.core.environment.CommandSetChecker;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TickTaskHandler;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.config.ConfigManager;
import com.forgeessentials.core.preloader.FELaunchHandler;
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
import cpw.mods.fml.common.Loader;
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
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Main mod class
 */

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = VersionUtils.FEVERSION, acceptableRemoteVersions = "*", dependencies = "required-after:Forge@[10.13.2.1258,);after:WorldEdit")
public class ForgeEssentials extends ConfigLoaderBase
{

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

    @SuppressWarnings("unused")
    private TaskRegistry tasks = new TaskRegistry();

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

    @SuppressWarnings("unused")
    private FECommandManager commandManager;

    public static VersionUtils version;

    public static ASMDataTable asmData;

    /* ------------------------------------------------------------ */

    public ForgeEssentials()
    {
        // Check environment
        Environment.check();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        asmData = e.getAsmData();

        version = new VersionUtils(FELaunchHandler.jarLocation);

        FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");
        OutputHandler.felog.info("Initializing ForgeEssentials version " + VersionUtils.FEVERSION + " (configDir = " + FEDIR.getAbsolutePath() + ")");
        OutputHandler.felog.info("Build information: Build number is: " + version.getBuildNumber() + ", build hash is: " + version.getBuildHash());

        // Load configuration
        configManager = new ConfigManager(FEDIR, "main");
        configManager.registerLoader(configManager.getMainConfigName(), this);
        configManager.registerLoader(configManager.getMainConfigName(), new OutputHandler());

        // Load network packages
        NetworkUtils.netHandler.registerMessage(new IMessageHandler<Packet0Handshake, IMessage>() {
            @Override
            public IMessage onMessage(Packet0Handshake message, MessageContext ctx)
            {
                System.out.println("Received handshake packet");
                PlayerInfo.get(ctx.getServerHandler().playerEntity).setHasFEClient(true);
                return null;
            }
        }, Packet0Handshake.class, 0, Side.SERVER);

        if (!Loader.isModLoaded("ForgeEssentialsClient"))
        {
            NetworkUtils.initServerNullHandlers();
        }

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
        questioner = new Questioner();
        FunctionHelper.FE_INTERNAL_EVENTBUS.register(new CompatReiMinimap());

        // Register commands
        FECommandManager.registerCommand(new CommandFEInfo());
        FECommandManager.registerCommand(new CommandWand());
        FECommandManager.registerCommand(new CommandUuid());
        if (!ModuleLauncher.getModuleList().contains("WEIntegrationTools"))
        {
            FECommandManager.registerCommand(new CommandPos(1));
            FECommandManager.registerCommand(new CommandPos(2));
            FECommandManager.registerCommand(new CommandDeselect());
            FECommandManager.registerCommand(new CommandExpand());
            FECommandManager.registerCommand(new CommandExpandY());
        }

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleInitEvent(e));
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModulePostInitEvent(e));
        commandManager = new FECommandManager();
    }

    /* ------------------------------------------------------------ */

    @EventHandler
    public void serverPreInit(FMLServerAboutToStartEvent e)
    {
        DataManager.setInstance(new DataManager(new File(FunctionHelper.getWorldPath(), "FEData/json")));
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleServerPreInitEvent(e));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        BlockModListFile.makeModList();
        BlockModListFile.dumpFMLRegistries();
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerInitEvent(e));

        FunctionHelper.replaceCommand("help", new HelpFixer()); // Will be overwritten again by commands module
        FECommandManager.registerCommands();
        
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
        PlayerInfo.discardAll();
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerStopEvent(e));
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleServerStoppedEvent(e));
        Translator.save();
        FECommandManager.clearRegisteredCommands();
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        UserIdent.login(event.player);
        PlayerInfo.login(event.player.getPersistentID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerLoggedOutEvent event)
    {
        PlayerInfo.logout(event.player.getPersistentID());
        UserIdent.logout(event.player);
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
