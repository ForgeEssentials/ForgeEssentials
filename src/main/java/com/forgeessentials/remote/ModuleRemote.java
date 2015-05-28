package com.forgeessentials.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteManager;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.remote.command.CommandRemote;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.google.gson.Gson;

import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Remote", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleRemote extends ConfigLoaderBase implements RemoteManager
{

    public static class PasskeyMap extends HashMap<UserIdent, String>
    {
        private static final long serialVersionUID = -8268113844467318789L; /* default */
    };

    private static final String CONFIG_CAT = "Remote";

    private static String certificateFilename = "FeRemote.jks";
    private static String certificatePassword = "feremote";

    public static final char[] PASSKEY_CHARS;

    public static final String PERM = RemoteHandler.PERM_REMOTE;
    public static final String PERM_CONTROL = PERM + ".control";

    public static int passkeyLength = 6;

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

    /* ------------------------------------------------------------ */

    /**
     * Register remote module and basic handlers
     */
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.remoteManager = this;
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.OP, "Allows login to remote module");
        APIRegistry.perms.registerPermission(PERM_CONTROL, RegisteredPermValue.OP,
                "Allows to start / stop remote server and control users (regen passkeys, kick, block)");

        registerRemoteHandlers();
        FECommandManager.registerCommand(new CommandRemote());
    }

    private void registerRemoteHandlers()
    {
        for (ASMData asm : ForgeEssentials.asmData.getAll(FERemoteHandler.class.getName()))
        {
            try
            {
                Class<?> clazz = Class.forName(asm.getClassName());
                if (RemoteHandler.class.isAssignableFrom(clazz))
                {
                    RemoteHandler handler = (RemoteHandler) clazz.newInstance();
                    FERemoteHandler annot = handler.getClass().getAnnotation(FERemoteHandler.class);
                    registerHandler(handler, annot.id());
                }
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
            {
                OutputHandler.felog.fine("Could not load FERemoteHandler " + asm.getClassName());
            }
        }
    }

    /**
     * Initialize passkeys, server and commands
     */
    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        loadPasskeys();
        startServer();
    }

    /**
     * Stop remote server when the MC-server stops
     */
    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        stopServer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.forgeessentials.core.moduleLauncher.config.IConfigLoader#load(net.minecraftforge.common.config.Configuration,
     * boolean)
     */
    @Override
    public void load(Configuration config, boolean isReload)
    {
        localhostOnly = config.get(CONFIG_CAT, "localhostOnly", true, "Allow connections from the web").getBoolean();
        hostname = config.get(CONFIG_CAT, "hostname", "localhost", "Hostname of your server. Used for QR code generation.").getString();
        port = config.get(CONFIG_CAT, "port", 27020, "Port to connect remotes to").getInt();
        useSSL = config.get(CONFIG_CAT, "use_ssl", false,
                "Protect the communication against network sniffing by encrypting traffic with SSL (You don't really need it - believe me)").getBoolean();
        passkeyLength = config.get(CONFIG_CAT, "passkey_length", 6, "Length of the randomly generated passkeys").getInt();
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning())
            startServer();
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
                        OutputHandler.felog.severe("[remote] Unable to load SSL certificate: File not found");
                }
                catch (IOException | GeneralSecurityException e1)
                {
                    OutputHandler.felog.severe("[remote] Unable to load SSL certificate: " + e1.getMessage());
                }
            }
            else
            {
                server = new Server(port, bindAddress);
            }
        }
        catch (IOException e1)
        {
            OutputHandler.felog.severe("[remote] Unable to start remote-server: " + e1.getMessage());
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
     * @see com.forgeessentials.api.remote.RemoteManager#registerHandler(com.forgeessentials.api.remote.RemoteHandler)
     */
    @Override
    public void registerHandler(RemoteHandler handler, String id)
    {
        if (handlers.containsKey(id))
            throw new IllegalArgumentException(Translator.format(
                    "Tried to register handler \"%s\" with ID \"%s\", but handler \"%s\" is already registered with that ID.", handler.getClass().getName(),
                    id, handlers.get(id).getClass().getName()));

        handlers.put(id, handler);
        String perm = handler.getPermission();
        if (perm != null && APIRegistry.perms.getServerZone().getRootZone().getGroupPermission(Zone.GROUP_DEFAULT, perm) == null)
            APIRegistry.perms.registerPermission(perm, RegisteredPermValue.OP);
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
        Random rnd = new Random();
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
            passkeys.put(userIdent, passkey);
        DataManager.getInstance().save(passkeys, "passkeys");
    }

    /**
     * Load the passkeys from data-backend
     */
    public void loadPasskeys()
    {
        passkeys = DataManager.getInstance().load(PasskeyMap.class, "passkeys");
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
        return DataManager.getInstance().getGson();
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
        return String.format("%s@%s:%d|%s", userIdent.getUsernameOrUuid(), (useSSL ? "ssl:" : "") + hostname, port, getPasskey(userIdent));
    }

    /**
     * Get the instance of ModuleRemote
     */
    public static ModuleRemote getInstance()
    {
        return instance;
    }

}
