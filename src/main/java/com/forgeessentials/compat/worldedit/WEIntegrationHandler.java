package com.forgeessentials.compat.worldedit;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WEIntegrationHandler
{

    @SuppressWarnings("unused")
    private CUIComms cuiComms;

    @SubscribeEvent
    public void load(FEModulePreInitEvent e)
    {
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
        cuiComms = new CUIComms();
        ForgeWorldEdit.inst.setPermissionsProvider(new PermissionsHandler());
    }

}
