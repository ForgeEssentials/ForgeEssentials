package com.forgeessentials.remote;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteManager;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.remote.handler.PushChatHandler;
import com.forgeessentials.remote.handler.QueryPlayerHandler;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Remote", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleRemote extends ConfigLoaderBase implements RemoteManager {

    private static final String CONFIG_CAT = "Remote";

    private static String certificateFilename = "com/forgeessentials/remote/FeRemote.jks";
    private static String certificatePassword = "feremote";

    @FEModule.Instance
    private static ModuleRemote instance;

    private int port;

    private String hostname;

    private boolean useSSL;

    private boolean allowUnauthenticatedAccess;

    private Server server;

    private Map<String, RemoteHandler> handlers = new HashMap<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Get the instance of ModuleRemote
     */
    public static ModuleRemote getInstance()
    {
        return instance;
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.remoteManager = this;

        new QueryPlayerHandler().register();
        new PushChatHandler().register();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        try
        {
            if (useSSL)
            {
                try
                {
                    InputStream is = ClassLoader.getSystemResourceAsStream(certificateFilename);
                    if (is != null)
                    {
                        SSLContextHelper sslCtxHelper = new SSLContextHelper();
                        sslCtxHelper.loadSSLCertificate(is, certificatePassword, certificatePassword);
                        server = new Server(port, hostname, sslCtxHelper.getSSLCtx());
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
                server = new Server(port, hostname);
            }
        }
        catch (IOException e1)
        {
            OutputHandler.felog.severe("[remote] Unable to start remote-server: " + e1.getMessage());
        }
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        if (server != null)
        {
            server.close();
            server = null;
        }
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        hostname = config.get(CONFIG_CAT, "hostname", "localhost", "Hostname of the minecraft server").getString();
        port = config.get(CONFIG_CAT, "port", 27020, "Port to connect remotes to").getInt();
        useSSL = config.get(CONFIG_CAT, "useSSL", false,
                "Protect the communication against network sniffing by encrypting traffic with SSL (You don't really need it - believe me)").getBoolean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteManager#registerHandler(com.forgeessentials.api.remote.RemoteHandler)
     */
    @Override
    public void registerHandler(RemoteHandler handler)
    {
        final String id = handler.getID();
        if (handlers.containsKey(id))
            throw new IllegalArgumentException(String.format("Handler with ID \"%s\" already registerd", id));
        handlers.put(id, handler);
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
     * Check, if unauthenticated access is allowed
     */
    public boolean allowUnauthenticatedAccess()
    {
        return allowUnauthenticatedAccess;
    }

    /**
     * Get Gson instance used for remote module
     */
    @Override
    public Gson getGson()
    {
        return gson;
    }

}
