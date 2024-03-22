package com.forgeessentials.remote;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.objectweb.asm.Type;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteManager;
import com.forgeessentials.commons.events.RegisterPacketEvent;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet07Remote;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.remote.command.CommandRemote;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "Remote", parentMod = ForgeEssentials.class, canDisable = true, defaultModule = false, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleRemote extends ConfigLoaderBase implements RemoteManager
{
    private static ForgeConfigSpec REMOTE_CONFIG;
    private static final ConfigData data = new ConfigData("Remote", REMOTE_CONFIG, new ForgeConfigSpec.Builder());

    public static class PasskeyMap extends HashMap<UserIdent, String>
    {
        private static final long serialVersionUID = -8268113844467318789L; /* default */
    }

    private static final String CONFIG_CAT = "Remote";

    private static String certificateFilename = "FeRemote.jks";
    private static String certificatePassword = "feremote";

    public static final char[] PASSKEY_CHARS;

    public static final String PERM = RemoteHandler.PERM_REMOTE;
    public static final String PERM_CONTROL = PERM + ".control";

    public static final GameProfile FAKEPLAYER = new GameProfile(new UUID(1451412139, 514498498), "$FE_REMOTE");

    public static int passkeyLength = 6;

    static
    {
        // Build a character set with only easily distinguishable characters
        Set<Character> chars = new HashSet<>();
        for (char c = 'a'; c <= 'z'; c++)
            chars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            chars.add(c);
        for (char c = '0'; c <= '9'; c++)
            chars.add(c);
        chars.remove('o');
        chars.remove('O');
        chars.remove('0');
        chars.remove('I');
        PASSKEY_CHARS = new char[chars.size()];
        int idx = 0;
        for (Character c : chars)
            PASSKEY_CHARS[idx++] = c;
    }

    /* ------------------------------------------------------------ */

    @FEModule.Instance
    protected static ModuleRemote instance;

    protected int port;

    protected String hostname;

    protected boolean localhostOnly;

    protected boolean useSSL;

    protected Server server;

    protected Map<String, RemoteHandler> handlers = new HashMap<>();

    protected PasskeyMap passkeys = new PasskeyMap();

    protected boolean mcServerStarted;

    private static final Type MOD = Type.getType(FERemoteHandler.class);

    /* ------------------------------------------------------------ */

    /**
     * Register remote module and basic handlers
     */
    public ModuleRemote()
    {
        APIRegistry.remoteManager = this;
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandRemote(true), event.getDispatcher());
    }

    @SubscribeEvent
    public void registerPacket(RegisterPacketEvent event)
    {
        NetworkUtils.registerServerToClient(7, Packet07Remote.class, Packet07Remote::encode, Packet07Remote::decode,
                Packet07Remote::handler);
    }

    /**
     * Register FERemoteHandler types
     */
    public void getASMDataTable()
    {

        final List<ModFileScanData.AnnotationData> data = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations).flatMap(Collection::stream)
                .filter(a -> MOD.equals(a.annotationType())).collect(Collectors.toList());

        for (ModFileScanData.AnnotationData asm : data)
        {
            try
            {
                Class<?> clazz = Class.forName(asm.memberName());
                if (RemoteHandler.class.isAssignableFrom(clazz))
                {
                    RemoteHandler handler = (RemoteHandler) clazz.getDeclaredConstructor().newInstance();
                    FERemoteHandler annot = handler.getClass().getAnnotation(FERemoteHandler.class);
                    registerHandler(handler, annot.id());
                }
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
            {
                LoggingHandler.felog.debug("Could not load FERemoteHandler " + asm.getClass());
            }
            catch (IllegalArgumentException | SecurityException | NoSuchMethodException | InvocationTargetException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverAboutToStart(FEModuleServerAboutToStartEvent event)
    {
        getASMDataTable();
    }

    /**
     * Initialize passkeys, server and commands
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Allows login to remote module");
        APIRegistry.perms.registerPermission(PERM_CONTROL, DefaultPermissionLevel.OP,
                "Allows to start / stop remote server and control users (regen passkeys, kick, block)");
        loadPasskeys();
        startServer();
        mcServerStarted = true;
    }

    /**
     * Stop remote server when the MC-server stops
     */
    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent event)
    {
        stopServer();
        mcServerStarted = false;
    }

    static ForgeConfigSpec.BooleanValue FElocalhostOnly;
    static ForgeConfigSpec.ConfigValue<String> FEhostname;
    static ForgeConfigSpec.IntValue FEport;
    static ForgeConfigSpec.BooleanValue FEuseSSL;
    static ForgeConfigSpec.IntValue FEpasskeyLength;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push(CONFIG_CAT);
        FElocalhostOnly = BUILDER.comment("Allow connections from the web").define("localhostOnly", true);
        FEhostname = BUILDER.comment("Hostname of your server. Used for QR code generation.").define("hostname",
                "localhost");
        FEport = BUILDER.comment("Port to connect remotes to").defineInRange("port", 27020, 0, 65535);
        FEuseSSL = BUILDER.comment(
                "Protect the communication against network sniffing by encrypting traffic with SSL (You don't really need it - believe me)")
                .define("use_ssl", false);
        FEpasskeyLength = BUILDER.comment("Length of the randomly generated passkeys").defineInRange("passkey_length",
                6, 1, 256);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        getInstance().localhostOnly = FElocalhostOnly.get();
        getInstance().hostname = FEhostname.get();
        getInstance().port = FEport.get();
        getInstance().useSSL = FEuseSSL.get();
        passkeyLength = FEpasskeyLength.get();
        if (getInstance().mcServerStarted)
            getInstance().startServer();
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
    public Server getServer()
    {
        return server;
    }

    /**
     * Starts up the remote server
     */
    public void startServer()
    {
        if (server != null)
            return;
        try
        {
            String bindAddress = localhostOnly ? "localhost" : "0.0.0.0";
            if (useSSL)
            {
                try
                {
                    InputStream is = ClassLoader.getSystemResourceAsStream(certificateFilename);
                    if (is != null)
                    {
                        SSLContextHelper sslCtxHelper = new SSLContextHelper();
                        sslCtxHelper.loadSSLCertificate(is, certificatePassword, certificatePassword);
                        server = new Server(port, bindAddress, sslCtxHelper.getSSLCtx());
                    }
                    else
                        LoggingHandler.felog.error("[remote] Unable to load SSL certificate: File not found");
                }
                catch (IOException | GeneralSecurityException e1)
                {
                    LoggingHandler.felog.error("[remote] Unable to load SSL certificate: " + e1.getMessage());
                }
            }
            else
            {
                server = new Server(port, bindAddress);
            }
        }
        catch (IOException e1)
        {
            LoggingHandler.felog.error("[remote] Unable to start remote-server: " + e1.getMessage());
        }
    }

    /**
     * Stops the remote server
     */
    public void stopServer()
    {
        if (server != null)
        {
            server.close();
            server = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteManager#registerHandler(com. forgeessentials.api.remote.RemoteHandler)
     */
    @Override
    public void registerHandler(RemoteHandler handler, String id)
    {
        if (handlers.containsKey(id))
            throw new IllegalArgumentException(Translator.format(
                    "Tried to register handler \"%s\" with ID \"%s\", but handler \"%s\" is already registered with that ID.",
                    handler.getClass().getName(), id, handlers.get(id).getClass().getName()));

        handlers.put(id, handler);
        String perm = handler.getPermission();
        if (perm != null
                && APIRegistry.perms.getServerZone().getRootZone().getGroupPermission(Zone.GROUP_DEFAULT, perm) == null)
            APIRegistry.perms.registerPermission(perm, DefaultPermissionLevel.OP, perm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteManager#getHandler(java.lang.String)
     */
    @Override
    public RemoteHandler getHandler(String id)
    {
        return handlers.get(id);
    }

    /**
     * Get all registered remote-handlers
     */
    public Map<String, RemoteHandler> getHandlers()
    {
        return handlers;
    }

    /**
     * Remote server port
     */
    public int getPort()
    {
        return port;
    }

    /* ------------------------------------------------------------ */

    /**
     * Generates a new random passkey
     */
    public String generatePasskey()
    {
        StringBuilder passkey = new StringBuilder();
        Random rnd;
        try
        {
            rnd = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e)
        {
            rnd = new SecureRandom();
        }
        for (int i = 0; i < passkeyLength; i++)
            passkey.append(PASSKEY_CHARS[rnd.nextInt(PASSKEY_CHARS.length)]);
        return passkey.toString();
    }

    /**
     * Get stored passkey for user or generate a new one and save it
     * 
     * @param userIdent
     */
    public String getPasskey(UserIdent userIdent)
    {
        if (passkeys.containsKey(userIdent))
            return passkeys.get(userIdent);
        String passkey = generatePasskey();
        setPasskey(userIdent, passkey);
        return passkey;
    }

    private static File getSaveFile()
    {
        return new File(DataManager.getInstance().getBasePath(), "RemotePasskeys.json");
    }

    /**
     * Set and save a new passkey for a user
     * 
     * @param userIdent
     * @param passkey
     */
    public void setPasskey(UserIdent userIdent, String passkey)
    {
        if (passkey == null)
            passkeys.remove(userIdent);
        else
        {
            // TODO: Think about hashes passkeys
            // passkey = hashPasskey(passkey);
            passkeys.put(userIdent, passkey);
        }
        DataManager.save(passkeys, getSaveFile());
    }

    public static String hashPasskey(String passkey)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passkey.getBytes());
            passkey = Hex.encodeHexString(md.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            /* do nothing */
        }
        return passkey;
    }

    /**
     * Load the passkeys from data-backend
     */
    public void loadPasskeys()
    {
        passkeys = DataManager.load(PasskeyMap.class, getSaveFile());
        if (passkeys == null)
            passkeys = new PasskeyMap();
    }

    /* ------------------------------------------------------------ */

    /**
     * Get Gson instance used for remote module
     */
    @Override
    public Gson getGson()
    {
        return DataManager.getGson();
    }

    /**
     * Tries to get the hostname
     */
    public String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            return "localhost";
        }
    }

    /**
     * Generates a remote-connect string for the given user
     * 
     * @param userIdent
     */
    public String getConnectString(UserIdent userIdent)
    {
        if (!userIdent.hasUuid())
            return null;
        return String.format("%s@%s:%d|%s", userIdent.getUsernameOrUuid(), (useSSL ? "ssl:" : "") + hostname, port,
                getPasskey(userIdent));
    }

    /**
     * Get the instance of ModuleRemote
     */
    public static ModuleRemote getInstance()
    {
        return instance;
    }
}
