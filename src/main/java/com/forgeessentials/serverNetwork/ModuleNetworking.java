package com.forgeessentials.serverNetwork;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.events.RegisterPacketEvent;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet10ClientTransfer;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.serverNetwork.client.FENetworkClient;
import com.forgeessentials.serverNetwork.commands.CommandNetworking;
import com.forgeessentials.serverNetwork.commands.CommandTransferServer;
import com.forgeessentials.serverNetwork.dataManagers.NetworkDataManager;
import com.forgeessentials.serverNetwork.server.FENetworkServer;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalClientData;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalServerData;
import com.forgeessentials.serverNetwork.utils.ServerType;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Module for all FENetworking connectivity
 * @author maximuslotro
 */
@FEModule(name = ModuleNetworking.networkModule, parentMod = ForgeEssentials.class, canDisable = true, defaultModule = false)
public class ModuleNetworking extends ConfigLoaderBase
{
    public static final String networkModule = "FENetworking";
    private static ForgeConfigSpec NETWORKING_CONFIG;
    private static final ConfigData data = new ConfigData("Networking", NETWORKING_CONFIG, new ForgeConfigSpec.Builder());

    private static final String CONFIG_CAT = "Networking";

    @ModuleDir
    public static File moduleDir;

    private static Map<String, ConnectedClientData> clients  = new HashMap<>();
    private static LocalClientData localClient;
    private static LocalServerData localServer;

    private static final int channelVersion = 123000;
    private static final String channelName = "FENetwork";

    public static final String PERM = "fe.networking";
    public static final String PERM_CONTROL = PERM + ".control";

    public static final GameProfile FAKEPLAYER = new GameProfile(new UUID(1451486139, 514649498), "$FE_NETWORK");

    /* ------------------------------------------------------------ */

    @FEModule.Instance
    protected static ModuleNetworking instance;

    private boolean rootServer;
    private int passkeyLength;
    private boolean localhostOnly;
    private boolean enableAutoStartServer;
    private int serverPort;
    private boolean enableAutoStartClient;
    private String clientHostname;
    private int clientPort;

    private FENetworkServer server;

    private FENetworkClient client;

    private NetworkDataManager tranferManager;

    private ServerType serverType = ServerType.NONE;
    /* ------------------------------------------------------------ */

    public ModuleNetworking(){}

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandNetworking(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTransferServer(true), event.getDispatcher());
    }

    @SubscribeEvent
    public void registerPacket(RegisterPacketEvent event)
    {
        NetworkUtils.registerServerToClient(10, Packet10ClientTransfer.class, Packet10ClientTransfer::encode, Packet10ClientTransfer::decode, Packet10ClientTransfer::handler);
    }

    @SubscribeEvent
    public void serverAboutToStart(FEModuleServerAboutToStartEvent event)
    {
        tranferManager = new NetworkDataManager(event);
    }

    /**
     * Initialize passkeys, server and commands
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Unused");
        APIRegistry.perms.registerPermission(PERM_CONTROL, DefaultPermissionLevel.OP, "Unused");
        loadData();
        if(localServer==null&&rootServer) {
            localServer = new LocalServerData("ForgeEssentialsServer"+(new Random()).nextInt(100000));
        }
        if(localClient==null&&!rootServer) {
            localClient = new LocalClientData("ForgeEssentialsClient"+(new Random()).nextInt(100000));
        }
        if(enableAutoStartServer&&rootServer) {
            serverType = ServerType.ROOTSERVER;
            startServer();
        }
        if(enableAutoStartClient&&!rootServer) {
            serverType = ServerType.CLIENTSERVER;
            startClient();
        }
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

    static ForgeConfigSpec.BooleanValue FErootServer;
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
        FErootServer = BUILDER.comment("If true, sets this server as the root server and disables being a client server. If false, sets this server as a client server, and disables being a parent server.").define("enable", true);
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
        rootServer = FErootServer.get();
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
        if(!rootServer)
            return 1;
        try
        {
            serverType = ServerType.ROOTSERVER;
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
        if(rootServer)
            return 1;
        try
        {
            serverType = ServerType.CLIENTSERVER;
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

    private static File getRemoteClientDataFolder()
    {
        return new File(moduleDir, "RemoteFENetworkClientData");
    }
    private static File getLocalClientDataFile()
    {
        return new File(moduleDir, "LocalFENetworkClientData.json");
    }
    private static File getLocalServerDataFile()
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

    public NetworkDataManager getTranferManager()
    {
        return tranferManager;
    }

    public ServerType getServerType()
    {
        return serverType;
    }
    
}
