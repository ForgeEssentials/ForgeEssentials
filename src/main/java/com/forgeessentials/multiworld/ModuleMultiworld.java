package com.forgeessentials.multiworld;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.multiworld.command.CommandMultiworld;
import com.forgeessentials.multiworld.command.CommandMultiworldTeleport;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
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

    private static MultiworldManager multiworldManager = new MultiworldManager();
    
    @SubscribeEvent
    public void postLoad(FEModulePostInitEvent e)
    {
        multiworldManager.loadWorldProviders();
        multiworldManager.loadWorldTypes();
        
        FECommandManager.registerCommand(new CommandMultiworld());
        FECommandManager.registerCommand(new CommandMultiworldTeleport());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        multiworldManager.load();

        PermissionsManager.registerPermission(PERM_CREATE, RegisteredPermValue.OP);
        PermissionsManager.registerPermission(PERM_DELETE, RegisteredPermValue.OP);
        PermissionsManager.registerPermission(PERM_LIST, RegisteredPermValue.TRUE);
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