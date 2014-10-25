package com.forgeessentials.afterlife;

import java.io.File;

import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * This module handles Deathchest and respawn debuffs.
 *
 * @author Dries007
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class, configClass = ConfigAfterlife.class)
public class ModuleAfterlife {
    public static final String BASEPERM = "fe.afterlife";
    @FEModule.Config
    public static ConfigAfterlife conf;
    @FEModule.Instance
    public static ModuleAfterlife instance;
    @FEModule.ModuleDir
    public static File moduleDir;
    public Deathchest deathchest;
    public RespawnDebuffHandler respawnDebuff;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        deathchest = new Deathchest();
        respawnDebuff = new RespawnDebuffHandler();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        deathchest.load();
        FMLCommonHandler.instance().bus().register(respawnDebuff);
        APIRegistry.perms.registerPermission(BASEPERM, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(RespawnDebuffHandler.BYPASSPOTION, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(RespawnDebuffHandler.BYPASSSTATS, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(Deathchest.PERMISSION_BYPASS, null);
        APIRegistry.perms.registerPermission(Deathchest.PERMISSION_MAKE, RegisteredPermValue.TRUE, "Allows graves to spawn, if a player dies");
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        deathchest.save();
    }
}
