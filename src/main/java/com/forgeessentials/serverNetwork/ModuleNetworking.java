package com.forgeessentials.serverNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.serverNetwork.server.FeNetworkClient;
import com.forgeessentials.serverNetwork.server.FeNetworkServer;
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

    public static final int channelVersion = 123456;

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

    protected String hostname;

    protected boolean localhostOnly;

    protected FeNetworkServer server;

    protected FeNetworkClient client;

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
        stopClient();
        stopServer();
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
        FEhostname = BUILDER.comment("Hostname of your server.").define("hostname",
                "localhost");
        FEport = BUILDER.comment("Port to connect remotes to").defineInRange("port", 27020, 0, 65535);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        localhostOnly = FElocalhostOnly.get();
        hostname = FEhostname.get();
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
    public FeNetworkServer getServer()
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
            server = new FeNetworkServer(bindAddress, port);
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
        if (server != null)
        {
            server.stopServer();
            server = null;
            return 0;
        }
        return 1;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return the client
     */
    public FeNetworkClient getClient()
    {
        return client;
    }

    /**
     * Starts up the networking client
     */
    public void startClient()
    {
        if (client != null)
            return;
        try
        {
            client = new FeNetworkClient("localhost", port);
            client.connect();;
        }
        catch (Exception e1)
        {
            LoggingHandler.felog.error("[FEnetworking] Unable to start server: " + e1.getMessage());
        }
    }

    /**
     * Stops the networking server
     */
    public void stopClient()
    {
        if (client != null)
        {
            client.disconnect();;
            client = null;
        }
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
