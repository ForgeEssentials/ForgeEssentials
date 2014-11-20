package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WEIntegrationHandler
{
    
    private FEPlatform platform;

    @SuppressWarnings("unused")
    private CUIComms cuiComms;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {

        ForgeEssentials.worldEditCompatilityPresent = true;
        PlayerInfo.selectionProvider = new WESelectionHandler();
    }

    @SubscribeEvent
    public void postLoad(FEModulePostInitEvent e)
    {
        if (WEIntegration.disable)
        {
            OutputHandler.felog.severe("Requested to force-disable WorldEdit.");
            if (Loader.isModLoaded("WorldEdit"))
                //MinecraftForge.EVENT_BUS.unregister(ForgeWorldEdit.inst); //forces worldedit forge NOT to load
            ModuleLauncher.instance.unregister("WEIntegrationTools");
        }
    }

    @SubscribeEvent
    public void serverStart(FEModuleServerInitEvent e)
    {
        this.platform = new FEPlatform();
        WorldEdit.getInstance().getPlatformManager().register(platform);
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent());
        
        cuiComms = new CUIComms();
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent());
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        WorldEdit.getInstance().getPlatformManager().unregister(platform);
    }

}
