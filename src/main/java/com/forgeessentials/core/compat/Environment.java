package com.forgeessentials.core.compat;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.Loader;

public class Environment {
    
    private static boolean hasWorldEdit = false;
    
    private static boolean isClient = false;

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

        if (Loader.isModLoaded("WorldEdit"))
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
        
        // Check for BukkitForge
        if (Loader.isModLoaded("BukkitForge"))
        {
            OutputHandler.felog.severe("Sanity check failed: Detected BukkitForge, bad things may happen, proceed at your own risk.");
        }

        // Check for Fihgu's mods
        if (Loader.isModLoaded("fihgu's Core Mod"))
        {
            OutputHandler.felog.severe("Sanity check failed: Detected Fihgu's mods, bad things may happen, proceed at your own risk.");
        }

        // Check for Cauldron or LavaBukkit
        try
        {
            Class.forName("org.bukkit.craftbukkit.Main");
            OutputHandler.felog.severe("Sanity check failed: Detected a ForgeBukkit server implementation, bad things may happen, proceed at your own risk.");
        }
        catch (ClassNotFoundException e)
        {
            // class not found
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
