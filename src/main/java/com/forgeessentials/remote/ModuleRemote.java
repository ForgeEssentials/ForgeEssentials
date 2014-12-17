package com.forgeessentials.remote;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

@FEModule(name = "Remote", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleRemote extends ConfigLoaderBase {

    private static final String CONFIG_CAT = "Remote";
    private RemoteServer server;
    private int port = 27020;
    private String hostname = "localhost";
    private boolean useSSL = false;

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        try
        {
            if (useSSL)
            {
                SSLContextHelper sslCtxHelper = new SSLContextHelper();
                sslCtxHelper.loadSSLCertificate("private.cert", "somepass", "someotherpass");
                server = new RemoteServer(port, hostname, sslCtxHelper.getSSLCtx());
            }
            else
            {
                server = new RemoteServer(port, hostname);
            }
        }
        catch (IOException | GeneralSecurityException e1)
        {
            OutputHandler.felog.severe("Unable to start remote-server: " + e1.getMessage());
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
        // useSSL = config.get(CONFIG_CAT, "useSSL", false, "Protect the communication with SSL").getBoolean();
    }

}
