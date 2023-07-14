package com.forgeessentials.serverNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.serverNetwork.client.FENetworkClient;
import com.forgeessentials.serverNetwork.server.FENetworkServer;
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

@FEModule(name = "Networking", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleNetworking extends ConfigLoaderBase
{
    private static ForgeConfigSpec NETWORKING_CONFIG;
    private static final ConfigData data = new ConfigData("Networking", NETWORKING_CONFIG, new ForgeConfigSpec.Builder());

    private static final String CONFIG_CAT = "Networking";

    private static final int channelVersion = 123456;
    private static final String channelName = "FENetwork";

    public static final char[] PASSKEY_CHARS;

    public static final String PERM = "fe.networking";
    public static final String PERM_CONTROL = PERM + ".control";

    public static final GameProfile FAKEPLAYER = new GameProfile(new UUID(1451486139, 514649498), "$FE_NETWORK");

    static
    {
        // Build a character set with only easily distinguishable characters
        Set<Character> chars = new HashSet<Character>();
        for (char c = 'a'; c <= 'z'; c++)
            chars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            chars.add(c);
        for (char c = '0'; c <= '9'; c++)
            chars.add(c);
        chars.add('+');
        chars.add('-');
        chars.add(':');
        PASSKEY_CHARS = new char[chars.size()];
        int idx = 0;
        for (Character c : chars)
            PASSKEY_CHARS[idx++] = c;
    }

    /* ------------------------------------------------------------ */

    @FEModule.Instance
    protected static ModuleNetworking instance;

    protected int port;

    protected boolean localhostOnly;

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
        //loadPasskeys();
        startServer();
        mcServerStarted = true;
    }

    /**
     * Stop remote server when the MC-server stops
     */
    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent event)
    {
        if(getServer()!=null&&getClient()!=null) {
            stopServer(false);
            stopClient(false);
        }else {
            stopServer(true);
            stopClient(true);
        }
        mcServerStarted = false;
    }

    static ForgeConfigSpec.BooleanValue FElocalhostOnly;
    static ForgeConfigSpec.ConfigValue<String> FEhostname;
    static ForgeConfigSpec.IntValue FEport;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push(CONFIG_CAT);
        FElocalhostOnly = BUILDER.comment("Allow connections from the web").define("localhostOnly", true);
        FEport = BUILDER.comment("Port to connect remotes to").defineInRange("port", 27020, 0, 65535);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        localhostOnly = FElocalhostOnly.get();
        port = FEport.get();
        if (mcServerStarted)
            startServer();
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
        if (server != null)
            return 1;
        try
        {
            String bindAddress = localhostOnly ? "localhost" : "0.0.0.0";
            server = new FENetworkServer(bindAddress, port, channelName, channelVersion);
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
    public int stopServer(boolean sendClosePacket)
    {
        if (server != null)
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
        if (client != null)
            return 1;
        try
        {
            client = new FENetworkClient("localhost", port, channelName, channelVersion);
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
    public int stopClient(boolean sendClosePacket)
    {
        if (client != null)
        {
            return client.disconnect();
        }
        return 1;
    }

//    /* ------------------------------------------------------------ */
//
//    /**
//     * Tries to get the hostname
//     */
//    public String getHostName()
//    {
//        try
//        {
//            return InetAddress.getLocalHost().getHostName();
//        }
//        catch (UnknownHostException e)
//        {
//            return "localhost";
//        }
//    }
//
//    public static File getSaveFile()
//    {
//        return new File(DataManager.getInstance().getBasePath(), "FENetworkPasskeys.json");
//    }
//
//    /**
//     * Load the passkeys from data-backend
//     */
//    public void loadPasskeys()
//    {
//        passkeys = DataManager.load(ServerPasskeys.class, getSaveFile());
//        if (passkeys == null)
//            passkeys = new ServerPasskeys();
//    }
}
