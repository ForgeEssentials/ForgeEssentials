package com.forgeessentials.core.environment;

import com.forgeessentials.core.ForgeEssentials;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class Environment
{

    private static boolean hasWorldEdit = false;

    private static boolean isClient = false;

    protected static boolean hasCauldron = false;

    private static final String ALLOW_CAULDRON = "forgeessentials.allowCauldron";

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
                ForgeEssentials.log.warn("Found WorldEdit, but not FE WorldEdit-module. You cannot use WorldEdit for FE without it.");
            }
        }

        if (Boolean.parseBoolean(System.getProperty("forgeessentials.developermode.we")))
        {
            ForgeEssentials.log.warn("WorldEdit integration tools force disabled.");
            hasWorldEdit = false;
            return;
        }

        // ============================================================
        // Some additional checks

        // Check for Cauldron or LavaBukkit
        String modName = FMLCommonHandler.instance().getModName();
        if (modName.contains("cauldron"))
        {
            ForgeEssentials.log.error("You are attempting to run FE on Cauldron. This is completely unsupported.");

            // good luck setting this - i had a very hard time doing so during debugging
            if (System.getProperty(ALLOW_CAULDRON) != null && Boolean.parseBoolean(System.getProperty(ALLOW_CAULDRON)))
            {
                ForgeEssentials.log.error("Bad things may happen. By setting the environment variable you are proceeding at your own risk.");
                ForgeEssentials.log.error("DO NOT BOTHER ANYONE IF YOU RUN INTO ISSUES.");
                ForgeEssentials.log.error("You are highly recommended to uninstall FE and use bukkit plugins instead.");
                hasCauldron = true;
                return;
            }

            ForgeEssentials.log.error("Bad things may happen. DO NOT BOTHER ANYONE ABOUT THIS CRASH - YOU WILL BE IGNORED");
            ForgeEssentials.log.error("Please uninstall FE from this Cauldron server installation. We recommend to use bukkit plugins instead.");
            ForgeEssentials.log.error("The server will now shut down as a precaution against data loss.");
            throw new RuntimeException("Sanity check failed: Detected Cauldron, bad things may happen to your server. Shutting down as a precaution.");
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
