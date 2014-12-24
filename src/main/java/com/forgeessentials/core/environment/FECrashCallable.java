package com.forgeessentials.core.environment;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.VersionUtils;
import cpw.mods.fml.common.ICrashCallable;

/**
 * Adds FE debug info to crash reports
 */
public class FECrashCallable implements ICrashCallable
{
    @Override
    public String getLabel()
    {
        return "ForgeEssentials";
    }

    @Override
    public String call() throws Exception
    {
        String modules = "";

        Boolean firstEntry = true;
        for (String id : ModuleLauncher.getModuleList())
        {
            modules = modules + (firstEntry ? id : ", " + id);
            firstEntry = false;
        }
        String n = System.getProperty("line.separator");
        String returned =  "Build information: Build number is: " + VersionUtils.getBuildNumber()
                + ", Build hash is: " + VersionUtils.getBuildHash()
                + ", Modules loaded: " + modules;

        if (Environment.hasCauldron)
        {
            returned = returned + n + "Cauldron detected - DO NOT REPORT THIS CRASH TO FE OR CAULDRON.";
        }

        return returned;
    }
}
