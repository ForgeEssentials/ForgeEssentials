package com.forgeessentials.multiworld;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.multiworld.command.CommandGetDimension;
import com.forgeessentials.multiworld.command.CommandMultiworldCreate;
import com.forgeessentials.multiworld.command.CommandMultiworldDelete;
import com.forgeessentials.multiworld.command.CommandMultiworldList;
import com.forgeessentials.multiworld.command.CommandMultiworldProviders;
import com.forgeessentials.multiworld.command.CommandMultiworldTeleport;
import com.forgeessentials.multiworld.core.MultiworldManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * @author Olee
 */
@FEModule(name = "Multiworld", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleMultiworld extends ConfigLoaderBase {

    public static final String PERM_BASE = "fe.multiworld";
    public static final String PERM_CREATE = PERM_BASE + ".create";
    public static final String PERM_DELETE = PERM_BASE + ".delete";
    public static final String PERM_LIST = PERM_BASE + ".list";
    public static final String PERM_TELEPORT = PERM_BASE + ".teleport";

    private static MultiworldManager multiworldManager;

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {

    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        multiworldManager = new MultiworldManager();
        // DataManager.addDeserializer(Multiworld.class, new Multiworld.MultiworldDeserializer());
    }

    @SubscribeEvent
    public void postLoad(FEModulePostInitEvent e)
    {
        multiworldManager.loadWorldProviders();
        multiworldManager.loadWorldTypes();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        multiworldManager.load();
        
        // Register commands
        new CommandGetDimension().register();
        new CommandMultiworldCreate().register();
        new CommandMultiworldDelete().register();
        new CommandMultiworldList().register();
        new CommandMultiworldTeleport().register();
        new CommandMultiworldProviders().register();
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {

    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {

    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        multiworldManager.serverStopped();
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // persistenceBackend = config.get(CONFIG_CAT, "persistenceBackend", "flatfile", "Choose a permission persistence backend (flatfile, sql)").getString();
    }

    public static MultiworldManager getMultiworldManager()
    {
        return multiworldManager;
    }

}