package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerInit;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import java.io.File;

@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class, configClass = WEIntegrationToolsConfig.class)
public class WEIntegration {

    protected static int syncInterval;

    @FEModule.Config
    public static WEIntegrationToolsConfig config;

    @FEModule.ModuleDir
    public static File moduleDir;

    private boolean getDevOverride()
    {
        String prop = System.getProperty("forgeessentials.developermode.we");
        if (prop != null && prop.equals("true"))
        { // FOR DEVS ONLY! THAT IS WHY IT IS A PROPERTY!!!

            OutputHandler.felog.severe("Developer mode has been enabled, things may break.");
            return true;
        }
        else
        {
            return false;
        }
    }

    @Init
    //@ModuleEventHandler
    public void load(FEModuleInitEvent e)
    {
        if (getDevOverride())
        {
            e.getModuleContainer().isLoadable = false;
            return;
        }

        if (!EnvironmentChecker.worldEditInstalled)
        {
            OutputHandler.felog.severe("You cannot run the FE integration tools for WorldEdit without installing WorldEdit Forge.");
            e.getModuleContainer().isLoadable = false;
        }
        EnvironmentChecker.worldEditFEtoolsInstalled = true;
        TickRegistry.registerScheduledTickHandler(new SelectionSyncHandler(syncInterval), Side.SERVER);
    }

    @ServerInit
    //@ModuleEventHandler
    public void serverStart(FEModuleServerInitEvent e)
    {

    }

}
