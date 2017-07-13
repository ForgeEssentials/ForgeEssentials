package com.forgeessentials.compat.worldedit;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.sk89q.worldedit.forge.ForgeWorldEdit;

import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

// separate class from the main WEIntegration stuff so as to avoid nasty errors
@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class)
public class WEIntegration
{

    protected static boolean disable;
    private CUIComms cuiComms;

    private static boolean getDevOverride()
    {
        String prop = System.getProperty("forgeessentials.developermode.we");
        if (prop != null && prop.equals("true"))
        { // FOR DEVS ONLY! THAT IS WHY IT IS A PROPERTY!!!

            LoggingHandler.felog.error("Developer mode has been enabled, things may break.");
            return true;
        }
        else
        {
            return false;
        }
    }

    @Preconditions
    public boolean canLoad()
    {
        if (getDevOverride())
        {
            disable = true;
            return false;
        }

        if (!Environment.hasWorldEdit())
        {
            LoggingHandler.felog.error("The FE integration tools for WorldEdit will not work without installing WorldEdit Forge.");
            LoggingHandler.felog.error("You are highly recommended to install WorldEdit Forge for the optimal FE experience.");
            return false;
        }

        else
        {
            try
            {
                Class.forName("com.sk89q.worldedit.forge.ForgePermissionsProvider");
            }
            catch (ClassNotFoundException e)
            {
                LoggingHandler.felog.error("ForgePermissionsProvider not found, are you using an old version of WorldEdit?");
                LoggingHandler.felog.error("The FE integration tools for WorldEdit will not be loaded as your version of WorldEdit may be too old.");
                return false;
            }
        }
        return true;
    }

    @Method(modid = "worldedit")
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
        else
        {
            SelectionHandler.selectionProvider = new WESelectionHandler();
        }
    }

    @Method(modid = "worldedit")
    @SubscribeEvent
    public void serverStart(FEModuleServerInitEvent e)
    {
        cuiComms = new CUIComms();
        ForgeWorldEdit.inst.setPermissionsProvider(new PermissionsHandler());
        PermissionAPI.registerNode("worldedit.*", DefaultPermissionLevel.OP, "WorldEdit");
    }
}
