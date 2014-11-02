package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.compat.Environment;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class)
public class WEIntegration {

    protected static int syncInterval;
    private static boolean disable;
    private FEPlatform platform;

    @FEModule.ModuleDir
    public static File moduleDir;

    private boolean getDevOverride()
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

    @SubscribeEvent
    public void load(FMLInitializationEvent e)
    {
        if (getDevOverride())
        {
            disable = true;
            return;
        }

        if (!Environment.hasWorldEdit)
        {
            OutputHandler.felog.severe("You cannot run the FE integration tools for WorldEdit without installing WorldEdit Forge.");
            ModuleLauncher.instance.unregister("WEIntegrationTools");
            return;
        }
        ForgeEssentials.worldEditCompatilityPresent = true;
        PlayerInfo.selectionProvider = new WESelectionHandler();
    }

    @SubscribeEvent
    public void postLoad(FMLPostInitializationEvent e)
    {
        if (disable)
        {
            OutputHandler.felog.severe("Requested to force-disable WorldEdit.");
            if (Loader.isModLoaded("WorldEdit"))
                MinecraftForge.EVENT_BUS.unregister(ForgeWorldEdit.inst); //forces worldedit forge NOT to load
            ModuleLauncher.instance.unregister("WEIntegrationTools");
        }

    }

    @SubscribeEvent
    public void serverStart(FMLServerStartingEvent e)
    {
        this.platform = new FEPlatform();
        WorldEdit.getInstance().getPlatformManager().register(platform);

    }

    @SubscribeEvent
    public void serverStarted(FMLServerStartedEvent e)
    {
        for (ForgeEssentialsCommandBase cmd : FEPlatform.commands)
        {
            FunctionHelper.registerServerCommand(cmd);
        }
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent());
    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent e)
    {
        WorldEdit.getInstance().getPlatformManager().unregister(platform);
    }

}
