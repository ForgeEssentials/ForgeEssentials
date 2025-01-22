package com.forgeessentials.core.environment;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;

import net.minecraftforge.fml.common.ICrashCallable;

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
        String modules = StringUtils.join(ModuleLauncher.getModuleList(), ", ");
        String n = System.getProperty("line.separator");
        String returned = String.format("Running ForgeEssentials %s (%s)", BuildInfo.getCurrentVersion(), BuildInfo.getBuildHash());
        returned += ". Modules loaded: " + modules;

        if (Environment.hasCauldron)
        {
            returned = returned + n + "Cauldron detected - DO NOT REPORT THIS CRASH TO FE OR CAULDRON.";
        }

        return returned;
    }

}
