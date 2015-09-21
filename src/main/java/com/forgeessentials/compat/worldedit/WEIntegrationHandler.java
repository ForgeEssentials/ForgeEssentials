package com.forgeessentials.compat.worldedit;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.sk89q.worldedit.forge.ForgeWorldEdit;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WEIntegrationHandler
{

    @SuppressWarnings("unused")
    private CUIComms cuiComms;

    @SubscribeEvent
    public void postLoad(FEModulePostInitEvent e)
    {
        if (WEIntegration.disable)
        {
            LoggingHandler.felog.error("Requested to force-disable WorldEdit.");
            // if (Loader.isModLoaded("WorldEdit"))
            // MinecraftForge.EVENT_BUS.unregister(ForgeWorldEdit.inst); //forces worldedit forge NOT to load
            ModuleLauncher.instance.unregister("WEIntegrationTools");
        }
    }

    @SubscribeEvent
    public void serverStart(FEModuleServerInitEvent e)
    {
        cuiComms = new CUIComms();
        ForgeWorldEdit.inst.setPermissionsProvider(new PermissionsHandler());
        // WorldEdit.getInstance().getEventBus().register(this);
        
        PermissionManager.registerPermission("worldedit.selection.*", PermissionLevel.TRUE);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e)
    {
        SelectionHandler.selectionProvider = new WESelectionHandler();
    }

}
