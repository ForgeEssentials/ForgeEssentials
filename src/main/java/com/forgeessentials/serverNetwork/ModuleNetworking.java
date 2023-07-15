package com.forgeessentials.serverNetwork;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.serverNetwork.client.FENetworkClient;
import com.forgeessentials.serverNetwork.server.FENetworkServer;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalClientData;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalServerData;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@FEModule(name = "FENetworking", parentMod = ForgeEssentials.class, canDisable = true)
/**
 * Module for all FENetworking connectivity
 * @author maximuslotro
 */
public class ModuleNetworking extends ConfigLoaderBase
{
    private static ForgeConfigSpec NETWORKING_CONFIG;
    private static final ConfigData data = new ConfigData("Networking", NETWORKING_CONFIG, new ForgeConfigSpec.Builder());

    private static final String CONFIG_CAT = "Networking";

    @ModuleDir
    public static File moduleDir;

    private static Map<String, ConnectedClientData> clients  = new HashMap<>();
    private static LocalClientData localClient;
    private static LocalServerData localServer;

    private static final int channelVersion = 123456;
    private static final String channelName = "FENetwork";

    public static final String PERM = "fe.networking";
    public static final String PERM_CONTROL = PERM + ".control";

    public static final GameProfile FAKEPLAYER = new GameProfile(new UUID(1451486139, 514649498), "$FE_NETWORK");

    /* ------------------------------------------------------------ */

    @FEModule.Instance
    protected static ModuleNetworking instance;

    protected int passkeyLength;
    protected boolean localhostOnly;
    protected boolean enableAutoStartServer;
    protected int serverPort;
    protected boolean enableAutoStartClient;
    protected String clientHostname;
    protected int clientPort;

    protected FENetworkServer server;

    protected FENetworkClient client;

    protected boolean mcServerStarted;

    /* ------------------------------------------------------------ */

    public ModuleNetworking(){}

    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandNetworking(true), dispatcher);
    }

    /**
     * Initialize passkeys, server and commands
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        //APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Allows login to remote module");
        //APIRegistry.perms.registerPermission(PERM_CONTROL, DefaultPermissionLevel.OP, "Allows to start / stop remote server and control users (regen passkeys, kick, block)");
        loadData();
        if(localServer==null) {
            localServer = new LocalServerData("ForgeEssentialsServer"+(new Random()).nextInt(100000));
        }
        if(localClient==null) {
            localClient = new LocalClientData("ForgeEssentialsClient"+(new Random()).nextInt(100000));
        }
        if(enableAutoStartServer) {
            startServer();
        }
        if(enableAutoStartClient) {
            startClient();
        }
        mcServerStarted = true;
    }

    /**
     * Stop remote server when the MC-server stops
     */
    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent event)
    {
        saveData();
        if(getServer()!=null) {
            stopServer();
        }
        if(getClient()!=null) {
            stopClient();
        }
        mcServerStarted = false;
    }

    public static ModuleNetworking getInstance() {
        return instance;
    }

    public static LocalClientData getLocalClient() {
        return localClient;
    }

    public static LocalServerData getLocalServer() {
        return localServer;
    }

    public static Map<String, ConnectedClientData> getClients()
    {
        return clients;
    }

    static ForgeConfigSpec.IntValue FEpasskeyLength;
    static ForgeConfigSpec.BooleanValue FElocalhostOnly;
    static ForgeConfigSpec.BooleanValue FEenableServer;
    static ForgeConfigSpec.IntValue FEport;
    static ForgeConfigSpec.BooleanValue FEenableClient;
    static ForgeConfigSpec.ConfigValue<String> FEclienthostname;
    static ForgeConfigSpec.IntValue FEclientport;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push(CONFIG_CAT+"_General");
        FEpasskeyLength = BUILDER.comment("Length of the randomly generated privateKeys").defineInRange("passkey_length", 6, 1, 256);
        BUILDER.pop();
        BUILDER.push(CONFIG_CAT+"_Server");
        FEenableServer = BUILDER.comment("Enable autoStartup of this FENetworkServer?").define("enable", false);
        FElocalhostOnly = BUILDER.comment("Disallow connections to this FENetworkServer from the web").define("localhostOnly", true);
        FEport = BUILDER.comment("Port for FENetworkClient's to connect to").defineInRange("port", 27020, 0, 65535);
        BUILDER.pop();
        BUILDER.push(CONFIG_CAT+"_Client");
        FEenableClient = BUILDER.comment("Enable autoStartup of this FENetworkClient?").define("enable", false);
        FEclienthostname = BUILDER.comment("Hostname fo this FENetworkClient to connect to").define("hostname-ip", "localhost");
        FEclientport = BUILDER.comment("Port this FENetworkClient's to connect to").defineInRange("port", 27020, 0, 65535);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        passkeyLength = FEpasskeyLength.get();
        enableAutoStartServer = FEenableServer.get();
        localhostOnly = FElocalhostOnly.get();
        serverPort = FEport.get();
        enableAutoStartClient = FEenableClient.get();
        clientHostname = FEclienthostname.get();
        clientPort = FEclientport.get();
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return the server
     */
    public FENetworkServer getServer()
    {
        return server;
    }

    /**
     * Starts up the networking server
     */
    public int startServer()
    {
        if (server != null && server.isChannelOpen())
            return 1;
        try
        {
            String bindAddress = localhostOnly ? "localhost" : "0.0.0.0";
            server = new FENetworkServer(bindAddress, serverPort, channelName, channelVersion);
            return server.startServer();
        }
        catch (Exception e1)
        {
            LoggingHandler.felog.error("[FEnetworking] Unable to start server: " + e1.getMessage());
            return 1;
        }
    }

    /**
     * Stops the networking server
     */
    public int stopServer()
    {
        if (server != null && server.isChannelOpen())
        {
            return server.stopServer();
        }
        return 1;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return the client
     */
    public FENetworkClient getClient()
    {
        return client;
    }

    /**
     * Starts up the networking client
     */
    public int startClient()
    {
        if (client != null && client.isChannelOpen())
            return 1;
        try
        {
            client = new FENetworkClient(clientHostname, clientPort, channelName, channelVersion);
            return client.connect();
        }
        catch (Exception e1)
        {
            LoggingHandler.felog.error("[FEnetworking] Unable to start client: " + e1.getMessage());
            return 1;
        }
    }

    /**
     * Stops the networking server
     */
    public int stopClient()
    {
        if (client != null && client.isChannelOpen())
        {
            return client.disconnect();
        }
        return 1;
    }

    /* ------------------------------------------------------------ */

    public static File getRemoteClientDataFolder()
    {
        return new File(moduleDir, "RemoteFENetworkClientData");
    }
    public static File getLocalClientDataFile()
    {
        return new File(moduleDir, "LocalFENetworkClientData.json");
    }
    public static File getLocalServerDataFile()
    {
        return new File(moduleDir, "LocalFENetworkServerData.json");
    }

    /**
     * Load the clientData from data-backend
     */
    public void loadData()
    {
        getRemoteClientDataFolder().mkdirs();
        clients = DataManager.loadAll(ConnectedClientData.class, getRemoteClientDataFolder());
        localClient = DataManager.load(LocalClientData.class, getLocalClientDataFile());
        localServer = DataManager.load(LocalServerData.class, getLocalServerDataFile());
    }
    /**
     * Save the clientData to data-backend
     */
    public void saveData()
    {
        getRemoteClientDataFolder().mkdirs();
        if(!clients.isEmpty()) {
            DataManager.saveAll(clients, getRemoteClientDataFolder());
        }
        if(localClient!=null) {
            DataManager.save(localClient, getLocalClientDataFile());
        }
        if(localServer!=null) {
            DataManager.save(localServer, getLocalServerDataFile());
        }
    }

    public int getPasskeyLength()
    {
        return passkeyLength;
    }
    
}
