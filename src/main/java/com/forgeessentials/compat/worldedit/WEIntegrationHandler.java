package com.forgeessentials.compat.worldedit;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.sk89q.worldedit.forge.ForgeWorldEdit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class WEIntegrationHandler
{

    private CUIComms cuiComms;

    @SubscribeEvent
    public void postLoad(FEModuleServerAboutToStartEvent e)
    {
        if (WEIntegration.disable)
        {
            LoggingHandler.felog.error("Requested to force-disable WorldEdit.");
            if (ModList.get().isLoaded("WorldEdit"))
            	MinecraftForge.EVENT_BUS.unregister(ForgeWorldEdit.inst); //forces worldedit forge NOT to load
            ModuleLauncher.instance.unregister("WEIntegrationTools");
        }
        else
        {
            SelectionHandler.selectionProvider = new WESelectionHandler();
        }
    }

    @SubscribeEvent
    public void serverStart(FEModuleServerStartingEvent e)
    {
        cuiComms = new CUIComms();
        ForgeWorldEdit.inst.setPermissionsProvider(new PermissionsHandler());
        PermissionAPI.registerNode("worldedit.*", DefaultPermissionLevel.OP, "WorldEdit");
    }

}
