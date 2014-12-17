package com.forgeessentials.remote;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Remote", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleRemote {

    private RemoteServer server;

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        try
        {
            int port = 27020;
            String hostname = "localhost";
            boolean useSSL = false;
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

}
