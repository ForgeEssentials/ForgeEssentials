package com.forgeessentials.compat.worldedit;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.sk89q.worldedit.forge.ForgeWorldEdit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class WEIntegrationHandler
{

    public CUIComms cuiComms;

    public boolean postLoad()
    {
        if (WEIntegration.disable)
        {
            LoggingHandler.felog.error("Requested to force-disable WorldEdit.");
            if (ModList.get().isLoaded("worldedit"))
            {
                try
                {
                    MinecraftForge.EVENT_BUS.unregister(ForgeWorldEdit.inst); // forces worldedit forge NOT to load
                }
                catch (IllegalArgumentException e1)
                {
                    LoggingHandler.felog.error("WorldEdit not found, unregistering WEIntegrationTools");
                    return true;
                }
            }
            return true;
        }
        else
        {
            if (ModList.get().isLoaded("worldedit"))
            {
                SelectionHandler.selectionProvider = new WESelectionHandler();
            }
            else
            {
                LoggingHandler.felog.error("WorldEdit not found, unregistering WEIntegrationTools");
                return true;
            }
        }
        return false;
    }

    public void fixWEPErms()
    {
        if (!WEIntegration.disable)
        {
            try
            {
            	ForgeWorldEdit.inst.setPermissionsProvider(new PermissionsHandler());
                cuiComms = new CUIComms();
            }
            catch (SecurityException | IllegalArgumentException e1)
            {
                e1.printStackTrace();
            }
            APIRegistry.perms.registerNode("worldedit.*", DefaultPermissionLevel.OP, "WorldEdit");
        }

    }

}
