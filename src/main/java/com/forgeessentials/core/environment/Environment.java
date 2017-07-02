package com.forgeessentials.core.environment;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class Environment
{

    private static boolean hasWorldEdit = false;

    private static boolean isClient = false;

    protected static boolean hasCauldron = false;

    protected static boolean hasSponge = false;

    private static boolean hasFTBU = false;

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
                LoggingHandler.felog.warn("Found WorldEdit Forge, but not FE WorldEdit-module. You cannot use WorldEdit for FE without it.");
            }
        }

        if (Loader.isModLoaded("ftbu"))
        {
            LoggingHandler.felog.warn("FTB Utilities is installed. Forge Essentials may not work as expected.");
            LoggingHandler.felog.warn("Please uninstall FTB Utilities to regain full FE functionality.");
            hasFTBU = true;
            MinecraftForge.EVENT_BUS.register(new FTBUNagHandler());
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

    public static boolean hasSponge()
    {
        return hasSponge;
    }

    public static void registerSpongeCompatPlugin(boolean isWESpongePresent)
    {
        LoggingHandler.felog.info("Sponge environment plugin found, enabling Sponge compat.");
        hasSponge = true;
        hasWorldEdit = isWESpongePresent;
    }
    public static class FTBUNagHandler
    {

        @SubscribeEvent
        public void playerLogIn(PlayerLoggedInEvent e)
        {
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(e.player.getGameProfile()))
            {
                ChatOutputHandler.chatWarning(e.player, "FTB Utilities is installed. Forge Essentials may not work as expected.");
                ChatOutputHandler.chatWarning(e.player, "Please uninstall FTB Utilities to regain full FE functionality.");
            }
        }
    }

}
