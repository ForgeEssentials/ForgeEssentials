package com.forgeessentials.compat.worldedit;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// separate class from the main WEIntegration stuff so as to avoid nasty errors
@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class)
public class WEIntegration
{

    protected static boolean disable;
    public static boolean stop;

    WEIntegrationHandler handler;

    @SubscribeEvent
    public void postLoad(FEModuleServerAboutToStartEvent e)
    {
    	handler=new WEIntegrationHandler();
    	if(handler.postLoad()) {
    		handler = null;
            ModuleLauncher.instance.unregister("WEIntegrationTools");
            return;
    	}
        MinecraftForge.EVENT_BUS.register(new WEIntegrationHandler());
    }

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
            stop = true;
            return false;
        }

        if (!Environment.hasWorldEdit())
        {
            stop = true;
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
                stop = true;
                LoggingHandler.felog.error("ForgePermissionsProvider not found, are you using an old version of WorldEdit?");
                LoggingHandler.felog.error("The FE integration tools for WorldEdit will not be loaded as your version of WorldEdit may be too old.");
                return false;
            }
        }
        return true;
    }
}
