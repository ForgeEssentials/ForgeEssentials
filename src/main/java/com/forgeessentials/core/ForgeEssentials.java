package com.forgeessentials.core;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.NetworkUtils.NullMessageHandler;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.commons.network.Packet7Remote;
import com.forgeessentials.compat.CompatReiMinimap;
import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.CommandUuid;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.config.ConfigManager;
import com.forgeessentials.core.preloader.FELaunchHandler;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.FEChunkLoader;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ForgeEssentialsEventFactory;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.selections.CommandDeselect;
import com.forgeessentials.util.selections.CommandExpand;
import com.forgeessentials.util.selections.CommandExpandY;
import com.forgeessentials.util.selections.CommandPos;
import com.forgeessentials.util.selections.CommandWand;
import com.forgeessentials.util.selections.SelectionEventHandler;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Main mod class
 */

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = BuildInfo.VERSION, acceptableRemoteVersions = "*", dependencies = "required-after:Forge@[10.13.2.1258,);after:WorldEdit")
public class ForgeEssentials extends ConfigLoaderBase
{

    @Instance(value = "ForgeEssentials")
    public static ForgeEssentials instance;

    /* ------------------------------------------------------------ */

    public static final String PERM = "fe";
    public static final String PERM_CORE = PERM + ".core";
    public static final String PERM_INFO = PERM_CORE + ".info";
    public static final String PERM_VERSIONINFO = PERM_CORE + ".versioninfo";

    /* ------------------------------------------------------------ */
    /* ForgeEssentials core submodules */

    private ConfigManager configManager;

    private ModuleLauncher moduleLauncher;

    @SuppressWarnings("unused")
    private TaskRegistry tasks = new TaskRegistry();

    @SuppressWarnings("unused")
    private SelectionEventHandler wandHandler;

    @SuppressWarnings("unused")
    private ForgeEssentialsEventFactory factory;

    @SuppressWarnings("unused")
    private TeleportHelper teleportHelper;

    @SuppressWarnings("unused")
    private Questioner questioner;

    @SuppressWarnings("unused")
    private FECommandManager commandManager;

    /* ------------------------------------------------------------ */

    private File configDirectory;

    private boolean debugMode = false;

    /* ------------------------------------------------------------ */

    public ForgeEssentials()
    {
        BuildInfo.getBuildInfo(FELaunchHandler.jarLocation);

        Environment.check();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LoggingHandler.felog = event.getModLog();

        LoggingHandler.felog.info(String.format("Running ForgeEssentials %s #%d (%s)", //
                BuildInfo.VERSION, BuildInfo.getBuildNumber(), BuildInfo.getBuildHash()));

        // Initialize core configuration
        initializeConfigurationManager();
        registerNetworkMessages();
        Translator.load();

        // Set up logger level
        if (debugMode)
            ((Logger) LoggingHandler.felog).setLevel(Level.DEBUG);
        else
            ((Logger) LoggingHandler.felog).setLevel(Level.INFO);

        // Register core submodules
        factory = new ForgeEssentialsEventFactory();
        wandHandler = new SelectionEventHandler();
        teleportHelper = new TeleportHelper();
        questioner = new Questioner();

        // Load submodules
        moduleLauncher = new ModuleLauncher();
        moduleLauncher.preLoad(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        registerCommands();

        FMLCommonHandler.instance().bus().register(this);
        APIRegistry.getFEEventBus().register(new CompatReiMinimap());

        if (BuildInfo.isOutdated())
        {
            LoggingHandler.felog.warn("-------------------------------------------------------------------------------------");
            LoggingHandler.felog.warn(String.format("WARNING! Using ForgeEssentials build #%d, latest build is #%d",//
                    BuildInfo.getBuildNumber(), BuildInfo.getBuildNumberLatest()));
            LoggingHandler.felog.warn("We highly recommend updating asap to get the latest security and bug fixes");
            LoggingHandler.felog.warn("-------------------------------------------------------------------------------------");
        }

        APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleInitEvent(e));
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent e)
    {
        APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModulePostInitEvent(e));
        commandManager = new FECommandManager();
    }

    /* ------------------------------------------------------------ */

    private void initializeConfigurationManager()
    {
        configDirectory = new File(ServerUtil.getBaseDir(), "/ForgeEssentials");
        configManager = new ConfigManager(configDirectory, "main");
        configManager.registerLoader(configManager.getMainConfigName(), this);
        configManager.registerLoader(configManager.getMainConfigName(), new FEConfig());
        configManager.registerLoader(configManager.getMainConfigName(), new ChatOutputHandler());
    }

    private void registerNetworkMessages()
    {
        // Load network packages
        NetworkUtils.registerMessage(new IMessageHandler<Packet0Handshake, IMessage>() {
            @Override
            public IMessage onMessage(Packet0Handshake message, MessageContext ctx)
            {
                PlayerInfo.get(ctx.getServerHandler().playerEntity).setHasFEClient(true);
                return null;
            }
        }, Packet0Handshake.class, 0, Side.SERVER);
        NetworkUtils.registerMessageProxy(Packet1SelectionUpdate.class, 1, Side.CLIENT, new NullMessageHandler<Packet1SelectionUpdate>() {
        });
        NetworkUtils.registerMessageProxy(Packet5Noclip.class, 5, Side.CLIENT, new NullMessageHandler<Packet5Noclip>() {
        });
        NetworkUtils.registerMessageProxy(Packet7Remote.class, 7, Side.CLIENT, new NullMessageHandler<Packet7Remote>() {
        });

        if (!Loader.isModLoaded("ForgeEssentialsClient"))
        {
            // NetworkUtils.initServerNullHandlers();
        }
    }

    private void registerCommands()
    {
        FECommandManager.registerCommand(new CommandFEInfo());
        FECommandManager.registerCommand(new CommandFeSettings());
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
    }

    /* ------------------------------------------------------------ */

    @EventHandler
    public void serverPreInit(FMLServerAboutToStartEvent e)
    {
        // Initialize data manager once server begins to start
        DataManager.setInstance(new DataManager(new File(ServerUtil.getWorldPath(), "FEData/json")));
        APIRegistry.getFEEventBus().post(new FEModuleServerPreInitEvent(e));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        BlockModListFile.makeModList();
        BlockModListFile.dumpFMLRegistries();
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());

        ServerUtil.replaceCommand("help", new HelpFixer()); // Will be overwritten again by commands module

        registerPermissions();

        APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleServerInitEvent(e));
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent e)
    {
        // TODO: what the fuck? I don't think we should just go and delete all commands colliding with ours!
        // CommandSetChecker.remove();
        FECommandManager.registerCommands();

        APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleServerPostInitEvent(e));
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent e)
    {
        APIRegistry.getFEEventBus().post(new FEModuleEvent.FEModuleServerStopEvent(e));
        PlayerInfo.discardAll();
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent e)
    {
        APIRegistry.getFEEventBus().post(new FEModuleServerStoppedEvent(e));
        FECommandManager.clearRegisteredCommands();
        Translator.save();
    }

    protected void registerPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_VERSIONINFO, PermissionLevel.OP, "Shows notification to the player if FE version is outdated");

        APIRegistry.perms.registerPermission("mc.help", PermissionLevel.TRUE, "Help command");

        // Teleport
        APIRegistry.perms.registerPermissionProperty(TeleportHelper.TELEPORT_COOLDOWN, "5", "Allow bypassing teleport cooldown");
        APIRegistry.perms.registerPermissionProperty(TeleportHelper.TELEPORT_WARMUP, "3", "Allow bypassing teleport warmup");
        APIRegistry.perms.registerPermissionPropertyOp(TeleportHelper.TELEPORT_COOLDOWN, "0");
        APIRegistry.perms.registerPermissionPropertyOp(TeleportHelper.TELEPORT_WARMUP, "0");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_FROM, PermissionLevel.TRUE,
                "Allow being teleported from a certain location / dimension");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_TO, PermissionLevel.TRUE, "Allow being teleported to a certain location / dimension");

        CommandFeSettings.addAlias("teleport_warmup", TeleportHelper.TELEPORT_WARMUP);
        CommandFeSettings.addAlias("teleport_cooldown", TeleportHelper.TELEPORT_COOLDOWN);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        UserIdent.login(event.player);
        PlayerInfo.login(event.player.getPersistentID());

        if (FEConfig.checkSpacesInNames)
        {
            Pattern pattern = Pattern.compile("\\s");
            Matcher matcher = pattern.matcher(event.player.getGameProfile().getName());
            if (matcher.find())
            {
                String msg = Translator.format("Invalid name \"%s\" containing spaces. Please change your name!", event.player.getCommandSenderName());
                ((EntityPlayerMP) event.player).playerNetServerHandler.kickPlayerFromServer(msg);
            }
        }

        // Show version notification
        if (BuildInfo.isOutdated() && UserIdent.get(event.player).checkPermission(PERM_VERSIONINFO))
            ChatOutputHandler.chatWarning(event.player,
                    String.format("ForgeEssentials build #%d outdated. Current build is #%d. Consider updating to get latest security and bug fixes.", //
                            BuildInfo.getBuildNumber(), BuildInfo.getBuildNumberLatest()));
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
        if (!config.get(FEConfig.CONFIG_CAT, "versionCheck", true, "Check for newer versions of ForgeEssentials on load?").getBoolean())
            BuildInfo.cancelVersionCheck();
        configManager.setUseCanonicalConfig(config.get(FEConfig.CONFIG_CAT, "canonicalConfigs", false,
                "For modules that support it, place their configs in this file.").getBoolean(false));
        debugMode = config.get(FEConfig.CONFIG_CAT, "debug", false, "Activates developer debug mode. Spams your FML logs.").getBoolean(false);
    }

    /* ------------------------------------------------------------ */

    public static ConfigManager getConfigManager()
    {
        return instance.configManager;
    }

    public static File getFEDirectory()
    {
        return instance.configDirectory;
    }

    public static boolean isDebug()
    {
        return instance.debugMode;
    }

}
