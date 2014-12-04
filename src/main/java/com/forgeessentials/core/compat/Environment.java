package com.forgeessentials.core.compat;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class Environment
{

    private static boolean hasWorldEdit = false;

    private static boolean isClient = false;

    private static final String ALLOW_CAULDRON = "forgeessentials.allowCauldron";

    public static void check()
    {
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
                Class.forName("com.forgeessentials.worldedit.compat.WEIntegration");
            }
            catch (ClassNotFoundException cnfe)
            {
                OutputHandler.felog.warning("Found WorldEdit, but not FE WorldEdit-module. You cannot use WorldEdit for FE without it.");
            }
        }

        if (Boolean.parseBoolean(System.getProperty("forgeessentials.developermode.we")))
        {
            OutputHandler.felog.warning("WorldEdit integration tools force disabled.");
            hasWorldEdit = false;
            ForgeEssentials.worldEditCompatilityPresent = false;
            return;
        }

        // ============================================================
        // Some additional checks

        // Check for Cauldron or LavaBukkit
        String modName = FMLCommonHandler.instance().getModName();
        if (modName.contains("cauldron"))
        {
            OutputHandler.felog.severe("You are attempting to run FE on Cauldron. This is completely unsupported.");

            // good luck setting this - i had a very hard time doing so during debugging
            if (System.getProperty(ALLOW_CAULDRON) != null && Boolean.parseBoolean(System.getProperty(ALLOW_CAULDRON)))
            {
                OutputHandler.felog.severe("Bad things may happen. By setting the environment variable you are proceeding at your own risk.");
                OutputHandler.felog.severe("DO NOT BOTHER ANYONE IF YOU RUN INTO ISSUES.");
                OutputHandler.felog.severe("You are highly recommended to uninstall FE and use bukkit plugins instead.");
                return;
            }

            OutputHandler.felog.severe("Bad things may happen. DO NOT BOTHER ANYONE ABOUT THIS CRASH - YOU WILL BE IGNORED");
            OutputHandler.felog.severe("Please uninstall FE from this Cauldron server installation. We recommend to use bukkit plugins instead.");
            OutputHandler.felog.severe("The server will now shut down as a precaution against data loss.");
            throw new RuntimeException("Sanity check failed: Detected Cauldron, bad things may happen to your server. Shutting down as a precaution.");
        }

        OutputHandler.felog.fine("Check passed, it's all good to go!");
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
