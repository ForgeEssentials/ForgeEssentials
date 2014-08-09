package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePostInitEvent;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class, configClass = WEIntegrationToolsConfig.class)
public class WEIntegration {

    protected static int syncInterval;
    private static boolean disable;

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
            disable = true;
            return;
        }

        if (!EnvironmentChecker.worldEditInstalled)
        {
            OutputHandler.felog.severe("You cannot run the FE integration tools for WorldEdit without installing WorldEdit Forge.");
            e.getModuleContainer().isLoadable = false;
            return;
        }
        EnvironmentChecker.worldEditFEtoolsInstalled = true;
        TickRegistry.registerScheduledTickHandler(new SelectionSyncHandler(syncInterval), Side.SERVER);
    }

    @FEModule.PostInit
    public void postLoad(FEModulePostInitEvent e)
    {
        if (disable)
        {
            OutputHandler.felog.severe("Requested to force-disable WorldEdit.");
            if (Loader.isModLoaded("WorldEdit"))
                MinecraftForge.EVENT_BUS.unregister(ForgeWorldEdit.inst); //forces worldedit forge NOT to load
            e.getModuleContainer().isLoadable = false;
        }

    }

}
