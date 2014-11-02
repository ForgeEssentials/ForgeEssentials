package com.forgeessentials.afterlife;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.io.File;

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
    public void load(FMLInitializationEvent e)
    {
        deathchest = new Deathchest();
        respawnDebuff = new RespawnDebuffHandler();
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {
        deathchest.load();
        APIRegistry.perms.registerPermission(BASEPERM, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(RespawnDebuffHandler.BYPASSPOTION, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(RespawnDebuffHandler.BYPASSSTATS, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(Deathchest.PERMISSION_BYPASS, null);
        APIRegistry.perms.registerPermission(Deathchest.PERMISSION_MAKE, RegisteredPermValue.TRUE, "Allows graves to spawn, if a player dies");
    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent e)
    {
        deathchest.save();
    }
}
