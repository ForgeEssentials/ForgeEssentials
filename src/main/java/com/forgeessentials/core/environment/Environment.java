package com.forgeessentials.core.environment;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class Environment
{

    private static boolean hasWorldEdit = false;

    private static boolean isClient = false;

    protected static boolean hasCauldron = false;
    public static void check()
    {
        FMLCommonHandler.instance().registerCrashCallable(new FECrashCallable());
        // Check if dedicated or integrated server
        try
        {
            Class.forName("net.minecraft.client.Minecraft");
            isClient = true;
        }
        catch (ClassNotFoundException e)
        {
            isClient = false;
        }

        if (Loader.isModLoaded("worldedit"))
        {
            hasWorldEdit = true;
            try
            {
                Class.forName("com.forgeessentials.compat.worldedit.WEIntegration");
            }
            catch (ClassNotFoundException cnfe)
            {
                LoggingHandler.felog.warn("Found WorldEdit, but not FE WorldEdit-module. You cannot use WorldEdit for FE without it.");
            }
        }

        if (Boolean.parseBoolean(System.getProperty("forgeessentials.developermode.we")))
        {
            LoggingHandler.felog.warn("WorldEdit integration tools force disabled.");
            hasWorldEdit = false;
            return;
        }

        // ============================================================
        // Some additional checks

        // Check for Cauldron or LavaBukkit
        String modName = FMLCommonHandler.instance().getModName();
        if (modName.contains("cauldron"))
        {
            LoggingHandler.felog.error("You are attempting to run FE on Cauldron. This is completely unsupported.");

            LoggingHandler.felog.error("Bad things may happen. DO NOT BOTHER ANYONE ABOUT THIS CRASH - YOU WILL BE IGNORED");
            LoggingHandler.felog.error("Please uninstall FE from this Cauldron server installation. We recommend to use bukkit plugins instead.");
            if (!ForgeEssentials.isSafeMode())
            {
                LoggingHandler.felog.error("The server will now shut down as a precaution against data loss.");
                throw new RuntimeException("Sanity check failed: Detected Cauldron, bad things may happen to your server. Shutting down as a precaution.");
            }
            LoggingHandler.felog.error("FE safe mode has been enabled, you are proceeding at your own risk.");
            LoggingHandler.felog.error("Sanity check failed: Detected Cauldron, bad things may happen to your server.");
        }
    }

    public static boolean isClient()
    {
        return isClient;
    }

    public static boolean hasWorldEdit()
    {
        return hasWorldEdit;
    }

}
