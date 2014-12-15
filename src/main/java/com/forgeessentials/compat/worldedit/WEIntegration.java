package com.forgeessentials.compat.worldedit;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.environment.Environment;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

// separate class from the main WEIntegration stuff so as to avoid nasty errors
@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class)
public class WEIntegration
{

    protected static boolean disable;

    private static boolean getDevOverride()
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
            OutputHandler.felog.severe("You cannot run the FE integration tools for WorldEdit without installing WorldEdit Forge.");
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.register(new WEIntegrationHandler());
    }
}
