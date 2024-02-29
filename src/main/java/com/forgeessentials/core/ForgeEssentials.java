package com.forgeessentials.core;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.events.RegisterPacketEvent;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet01SelectionUpdate;
import com.forgeessentials.commons.network.packets.Packet03PlayerPermissions;
import com.forgeessentials.commons.network.packets.Packet05Noclip;
import com.forgeessentials.compat.BaublesCompat;
import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.compat.worldedit.WEIntegration;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.CommandFEWorldInfo;
import com.forgeessentials.core.commands.CommandFeReload;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.CommandTest;
import com.forgeessentials.core.commands.CommandUuid;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.CommandPermissionManager;
import com.forgeessentials.core.misc.Packet0HandshakeHandler;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule.Instance;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.playerlogger.TestClass;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.player.PlayerPositionEventFactory;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.selections.CommandDeselect;
import com.forgeessentials.util.selections.CommandExpand;
import com.forgeessentials.util.selections.CommandExpandY;
import com.forgeessentials.util.selections.CommandPos1;
import com.forgeessentials.util.selections.CommandPos2;
import com.forgeessentials.util.selections.CommandWand;
import com.forgeessentials.util.selections.SelectionHandler;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Main mod class
 */
@Mod(ForgeEssentials.MODID)
@Mod.EventBusSubscriber(modid = ForgeEssentials.MODID)
public class ForgeEssentials
{

    public static final String MODID = "forgeessentials";
    public static final int CURRENT_MODULE_VERSION =1;

    public static final String FE_DIRECTORY = "ForgeEssentials";
    @Instance
    public static ForgeEssentials instance;
    public static IEventBus modMain;
    public static ModContainer MOD_CONTAINER;

    public static Random rnd = new Random();

    /* ------------------------------------------------------------ */

    public static final String PERM = "fe";
    public static final String PERM_CORE = PERM + ".core";
    public static final String PERM_INFO = PERM_CORE + ".info";
    public static final String PERM_RELOAD = PERM_CORE + ".reload";
    public static final String PERM_VERSIONINFO = PERM_CORE + ".versioninfo";

    /* ------------------------------------------------------------ */
    /* ForgeEssentials core submodules */

    protected static ConfigBase configManager;

    protected static ModuleLauncher moduleLauncher;

    protected static TaskRegistry tasks;

    protected static PlayerPositionEventFactory factory;

    protected static TeleportHelper teleportHelper;

    protected static Questioner questioner;

    protected static FECommandManager commandManager;

    /* ------------------------------------------------------------ */

    private static File feDirectory;

    // private static File moduleDirectory;

    private static File jarLocation;

    protected static boolean debugMode = true;

    protected static boolean safeMode = false;

    protected static boolean logCommandsToConsole;

    private RespawnHandler respawnHandler;

    private SelectionHandler selectionHandler;

    public static boolean isCubicChunksInstalled = false;

    /* ------------------------------------------------------------ */

    public ForgeEssentials()
    {
        TestClass.get();
        LoggingHandler.init();
        LoggingHandler.felog.info("ForgeEssentialsInt");
        instance = this;
        // Set mod as server only
        MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        modMain = FMLJavaModLoadingContext.get().getModEventBus();
        tasks = new TaskRegistry();

        List<ModInfo> mods = ModList.get().getMods();
        for (ModInfo mod : mods)
        {
            if (mod.getModId().equals("forgeessentials"))
            {
                jarLocation = mod.getOwningFile().getFile().getFilePath().toFile();
                break;
            }
        }
        BuildInfo.getBuildInfo(jarLocation);

        initConfiguration();
        Environment.check();
        MinecraftForge.EVENT_BUS.register(this);
        modMain.addListener(this::preInit);
        modMain.addListener(this::postLoad);
        moduleLauncher = new ModuleLauncher();
        NetworkUtils.init();
        // Load submodules
        moduleLauncher.init();
    }

    public void preInit(FMLCommonSetupEvent event)
    {
        LoggingHandler.felog.info("ForgeEssentials CommonSetup");
        LoggingHandler.felog.info(String.format("Running ForgeEssentials %s (%s)-%s", BuildInfo.getCurrentVersion(),
                BuildInfo.getBuildHash(), BuildInfo.getBuildType()));
    	// Handle submodules parents, must be called after mod loading
        moduleLauncher.handleModuleParents();
        if (safeMode) {
            LoggingHandler.felog.warn("You are running FE in safe mode. Please only do so if requested to by the ForgeEssentials team.");
        }
        ForgeEssentials.getConfigManager().bakeAllRegisteredConfigs(false);
        // Set up logger level
        toggleDebug();

        // Register core submodules
        factory = new PlayerPositionEventFactory();
        teleportHelper = new TeleportHelper();
        questioner = new Questioner();
        respawnHandler = new RespawnHandler();
        selectionHandler = new SelectionHandler();
        // MinecraftForge.EVENT_BUS.register(new CompatReiMinimap());

        if(ModuleLauncher.getModuleList().contains(WEIntegration.weModule)) {
        	WEIntegration.instance.postLoad();
        }
    }

    public void postLoad(FMLLoadCompleteEvent e)
    {
        LoggingHandler.felog.info("ForgeEssentials LoadCompleteEvent");
        commandManager = new FECommandManager();
        isCubicChunksInstalled = ModList.get().isLoaded("cubicchunks");
    }

    /* ------------------------------------------------------------ */

    private void initConfiguration()
    {
        feDirectory = new File(FMLPaths.GAMEDIR.get().toFile(), FE_DIRECTORY);
        feDirectory.mkdirs();

        configManager = new ConfigBase();

        ConfigBase.getModuleConfig().loadModuleConfig();
        ConfigBase.getModuleConfig().saveConfig();
        configManager.registerSpecs(new FEConfig());
    }

    private void toggleDebug()
    {
        if (isDebug())
            LoggingHandler.setLevel(Level.DEBUG);
        else
            LoggingHandler.setLevel(Level.INFO);
    }

    private void registerNetworkMessages()
    {
        LoggingHandler.felog.info("ForgeEssentials registering network Packets");
        // Load network packages
        NetworkUtils.registerClientToServer(0, Packet0HandshakeHandler.class, Packet0HandshakeHandler::encode, Packet0HandshakeHandler::decode,
                Packet0HandshakeHandler::handler);
        NetworkUtils.registerServerToClient(1, Packet01SelectionUpdate.class, Packet01SelectionUpdate::encode, Packet01SelectionUpdate::decode,
                Packet01SelectionUpdate::handler);
        // NetworkUtils.registerServerToClient(2, Packet2Reach.class, Packet2Reach::decode); old times
        NetworkUtils.registerServerToClient(3, Packet03PlayerPermissions.class, Packet03PlayerPermissions::encode, Packet03PlayerPermissions::decode,
                Packet03PlayerPermissions::handler);
        // NetworkUtils.registerServerToClient(2, Packet4Economy.class, Packet4Economy::decode); old times
        NetworkUtils.registerServerToClient(5, Packet05Noclip.class, Packet05Noclip::encode, Packet05Noclip::decode, Packet05Noclip::handler);
        // Packet6AuthLogin is registered in the Auth Module
        // Packet7Remote is registered in the Remote Module
        MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent());
        // Packet8AuthReply is registered in the Remote Module
        // Packet9AuthRequest is registered in the Remote Module

    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void registerCommandEvent(final RegisterCommandsEvent event)
    {
        LoggingHandler.felog.debug("ForgeEssentials Register Commands Event");
        FECommandManager.registerCommand(new CommandFEInfo(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandFeReload(true), event.getDispatcher());

        CommandFeSettings settings = new CommandFeSettings(true);
        FECommandManager.registerCommand(settings, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(settings);

        FECommandManager.registerCommand(new CommandTest(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandWand(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandUuid(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandFEWorldInfo(true), event.getDispatcher());
        if (!ModuleLauncher.getModuleList().contains(WEIntegration.weModule))
        {
            FECommandManager.registerCommand(new CommandPos1(true), event.getDispatcher());
            FECommandManager.registerCommand(new CommandPos2(true), event.getDispatcher());
            FECommandManager.registerCommand(new CommandDeselect(true), event.getDispatcher());
            FECommandManager.registerCommand(new CommandExpand(true), event.getDispatcher());
            FECommandManager.registerCommand(new CommandExpandY(true), event.getDispatcher());
        }
    }

    @SubscribeEvent
    public void serverPreInit(FMLServerAboutToStartEvent e)
    {
        BuildInfo.startVersionChecks(MODID);
    	LoggingHandler.felog.info("Registered " + FECommandManager.getTotalCommandNumber() + " commands");
        LoggingHandler.felog.info("ForgeEssentials ServerAboutToStart");

        //Register Server-Client Packets
        registerNetworkMessages();

        // Initialize data manager once server begins to start
        DataManager.setInstance(new DataManager(new File(ServerUtil.getWorldPath(), "FEData/json")));

        //Load ConfigurableCommands after DataManager is created
        FECommandManager.loadConfigurableCommand();

        MinecraftForge.EVENT_BUS.post(new FEModuleServerAboutToStartEvent(e));
        new BaublesCompat();
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {
        LoggingHandler.felog.info("ForgeEssentials ServerStarting");
        BlockModListFile.makeModList();
        BlockModListFile.dumpFMLRegistries();
        // TODO REIMPLEMENT
        // ForgeChunkManager.setForcedChunkLoadingCallback(ForgeEssentials.MODID, new FEChunkLoader());

        registerPermissions();
        FECommandManager.aliaseManager.saveData();

        MinecraftForge.EVENT_BUS.post(new FEModuleServerStartingEvent(e));
        if(BuildInfo.isOutdated()) {
        	LoggingHandler.felog.warn("-------------------------------------------------------------------------------------");
        	LoggingHandler.felog.warn(Translator.format("WARNING! Using ForgeEssentials build #%s, latest build is #%s",BuildInfo.getCurrentVersion(), BuildInfo.getLatestVersion()));
        	LoggingHandler.felog.warn("We highly recommend updating asap to get the latest security and bug fixes");
        	LoggingHandler.felog.warn("-------------------------------------------------------------------------------------");
        }
    }

    @SubscribeEvent
    public void serverStarted(FMLServerStartedEvent e)
    {
        LoggingHandler.felog.info("ForgeEssentials ServerStarted");
        MinecraftForge.EVENT_BUS.post(new FEModuleServerStartedEvent(e));

        // Do permission registration in first server tick.
        MinecraftForge.EVENT_BUS.register(new CommandPermissionRegistrationHandler());
    }

    public static final class CommandPermissionRegistrationHandler
    {
        @SubscribeEvent
        public void serverTickEvent(TickEvent.ServerTickEvent event)
        {
            CommandPermissionManager.registerCommandPermissions();
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent e)
    {
        LoggingHandler.felog.info("ForgeEssentials ServerStopping");
        MinecraftForge.EVENT_BUS.post(new FEModuleServerStoppingEvent(e));
        PlayerInfo.discardAll();
    }

    @SubscribeEvent
    public void serverStopped(FMLServerStoppedEvent e)
    {
        LoggingHandler.felog.info("ForgeEssentials ServerStopped");
        try
        {
            MinecraftForge.EVENT_BUS.post(new FEModuleServerStoppedEvent(e));
            FECommandManager.clearRegisteredCommands();
            Translator.save();
            ConfigBase.getModuleConfig().saveConfig();
        }
        catch (RuntimeException ex)
        {
            LoggingHandler.felog.fatal("Caught Runtime Exception During Server Stop event! Suppressing Fire!", ex);
        }
    }

    protected void registerPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_VERSIONINFO, DefaultPermissionLevel.OP,
                "Shows notification to the player if FE version is outdated");

        CommandPermissionManager.registerCommandPermission("help", DefaultPermissionLevel.ALL);
        CommandPermissionManager.registerCommandPermission("config", DefaultPermissionLevel.OP);
        CommandPermissionManager.registerCommandPermission("forge", DefaultPermissionLevel.OP);
        CommandPermissionManager.registerCommandPermission("trigger", DefaultPermissionLevel.OP);

        // Teleport
        APIRegistry.perms.registerPermissionProperty(TeleportHelper.TELEPORT_COOLDOWN, "5",
                "Allow bypassing teleport cooldown");
        APIRegistry.perms.registerPermissionProperty(TeleportHelper.TELEPORT_WARMUP, "3",
                "Allow bypassing teleport warmup");
        APIRegistry.perms.registerPermissionPropertyOp(TeleportHelper.TELEPORT_COOLDOWN, "0");
        APIRegistry.perms.registerPermissionPropertyOp(TeleportHelper.TELEPORT_WARMUP, "0");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_FROM, DefaultPermissionLevel.ALL,
                "Allow teleporting cross-dimensionally from a dimension");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_TO, DefaultPermissionLevel.ALL,
                "Allow teleporting cross-dimensionally to a dimension");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_PORTALFROM, DefaultPermissionLevel.ALL,
                "Allow teleporting cross-dimensionally from a dimension via a portal");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_CROSSDIM_PORTALTO, DefaultPermissionLevel.ALL,
                "Allow teleporting cross-dimensionally to a dimension via a portal (target coordinates are origin for vanilla portals)");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_FROM, DefaultPermissionLevel.ALL,
                "Allow being teleported from a certain location / dimension");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_TO, DefaultPermissionLevel.ALL,
                "Allow being teleported to a certain location / dimension");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_PORTALFROM, DefaultPermissionLevel.ALL,
                "Allow being teleported from a certain location / dimension via a portal");
        APIRegistry.perms.registerPermission(TeleportHelper.TELEPORT_PORTALTO, DefaultPermissionLevel.ALL,
                "Allow being teleported to a certain location / dimension via a portal");
        APIRegistry.perms.registerPermission(ModuleProtection.COMMANDBLOCK_PERM, DefaultPermissionLevel.OP,
                "Lowest node for restricting access to CommandBlocks");

        CommandFeSettings.addSetting("Teleport", "warmup", TeleportHelper.TELEPORT_WARMUP);
        CommandFeSettings.addSetting("Teleport", "cooldown", TeleportHelper.TELEPORT_COOLDOWN);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            UserIdent.login(player);
            PlayerInfo.login(player.getGameProfile().getId());
            try
            {
                PlayerInfo.login(player.getGameProfile().getId());
            }
            catch (JsonParseException e)
            {
                player.connection.disconnect(new StringTextComponent(
                        "Unable to Parse PlayerInfo file, please contact your admin for assistance and ask them to check the log!"));
                LoggingHandler.felog.fatal(
                        "Unable to Parse PlayerInfo file!  If this is date related, please check S:format_gson_compat in your main.cfg file!",
                        e);
            }
            if (FEConfig.checkSpacesInNames)
            {
                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(player.getGameProfile().getName());
                if (matcher.find())
                {
                    String msg = Translator.format("Invalid name \"%s\" containing spaces. Please change your name!",
                            event.getPlayer().getDisplayName().getString());
                    Entity entity = event.getEntity();
                    if (!(entity instanceof ServerPlayerEntity))
                    {
                        return;
                    }

                    ServerPlayerEntity serverplayer = (ServerPlayerEntity) entity;
                    serverplayer.connection.disconnect(new StringTextComponent(msg));
                }
            }

            // Show version notification
            if (BuildInfo.isOutdated() && UserIdent.get(player).checkPermission(PERM_VERSIONINFO))
                ChatOutputHandler.chatWarning(player, Translator.format(
                        "ForgeEssentials server build #%s is outdated. The current build is #%s. Consider updating to get latest security and bug fixes.",
                        BuildInfo.getCurrentVersion(), BuildInfo.getLatestVersion()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            PlayerInfo.logout(event.getPlayer().getGameProfile().getId());
            UserIdent.logout((PlayerEntity) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            UserIdent.get((PlayerEntity) event.getPlayer());
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void commandEvent(CommandEvent event) throws CommandSyntaxException
    {
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        boolean perm = false;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        if (info.getSource().getEntity() instanceof ServerPlayerEntity)
        {
            perm = checkPerms("command." + info.getPermissionNode(), CommandUtils.getServerPlayer(info.getSource()));
        }
        else
        {
            perm = true;
        }

        if (logCommandsToConsole)
        {
            LoggingHandler.felog
                    .info(String.format("Player \"%s\" %s command \"%s %s\"", info.getSource().getTextName(),
                            perm ? "used" : "tried to use", info.getCommandName(), info.getActualArgsString()));
        }

        if (!perm)
        {
            event.setCanceled(true);
            info.getSource().sendFailure(new StringTextComponent("You dont have permission to use this command!"));
        }
    }

    public boolean checkPerms(String commandPermissionNode, ServerPlayerEntity sender)
    {
    	if(isDebug()) {
    		LoggingHandler.felog.debug("Checking command perm: " + commandPermissionNode);
    	}
        return APIRegistry.perms.checkUserPermission(UserIdent.get(sender), commandPermissionNode);
    }

    /* ------------------------------------------------------------ */

    static ForgeConfigSpec.BooleanValue FEcheckVersion;
    static ForgeConfigSpec.BooleanValue FEdebugMode;
    static ForgeConfigSpec.BooleanValue FEsafeMode;
    static ForgeConfigSpec.BooleanValue FEhideWorldEditCommands;
    static ForgeConfigSpec.BooleanValue FElogCommandsToConsole;

    public static Builder load(Builder BUILDER, boolean isReload)
    {
        FEcheckVersion = BUILDER.comment("Check for newer versions of ForgeEssentials on load?").define("versionCheck",
                true);
        // configManager.setUseCanonicalConfig(SERVER_BUILDER.comment("For modules that
        // support it, place their configs in this file.").define("canonicalConfigs",
        // false).get());
        FEdebugMode = BUILDER.comment("Activates developer debug mode. Spams your FML logs.").define("debug", false);
        FEsafeMode = BUILDER
                .comment("Activates safe mode with will ignore some errors which would normally crash the game."
                        + "Please only enable this after being instructed to do so by FE team in response to an issue on GitHub!")
                .define("safeMode", false);
        FEhideWorldEditCommands = BUILDER
                .comment("Hide WorldEdit commands from /help and only show them in //help command")
                .define("hide_worldedit_help", true);
        FElogCommandsToConsole = BUILDER.comment("Log commands to console").define("logCommands", false);
        return BUILDER;
    }

    public static void bakeConfig(boolean reload)
    {
        if (reload)
            Translator.translations.clear();
        Translator.load();
        BuildInfo.needCheckVersion = FEcheckVersion.get();
        // configManager.setUseCanonicalConfig(SERVER_BUILDER.comment("For modules that
        // support it, place their configs in this file.").define("canonicalConfigs",
        // false).get());
        debugMode = FEdebugMode.get();
        safeMode = FEsafeMode.get();
        HelpFixer.hideWorldEditCommands = FEhideWorldEditCommands.get();
        logCommandsToConsole = FElogCommandsToConsole.get();
    }

    /* ------------------------------------------------------------ */

    public static ConfigBase getConfigManager()
    {
        return configManager;
    }

    public static File getFEDirectory()
    {
        return feDirectory;
    }

    public static boolean isDebug()
    {
        return debugMode;
    }

    public static boolean isSafeMode()
    {
        return safeMode;
    }

    public static File getJarLocation()
    {
        return jarLocation;
    }

    public RespawnHandler getRespawnHandler()
    {
        return respawnHandler;
    }

    public SelectionHandler getSelectionHandler()
    {
        return selectionHandler;
    }
}
