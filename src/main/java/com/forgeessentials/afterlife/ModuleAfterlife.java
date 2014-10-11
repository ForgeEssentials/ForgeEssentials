package com.forgeessentials.afterlife;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permissions.PermissionsManager;
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
        PermissionsManager.registerPermission(BASEPERM, RegisteredPermValue.OP);

        PermissionsManager.registerPermission(RespawnDebuffHandler.BYPASSPOTION, RegisteredPermValue.OP);
        PermissionsManager.registerPermission(RespawnDebuffHandler.BYPASSSTATS, RegisteredPermValue.OP);

        PermissionsManager.registerPermission(Deathchest.PERMISSION_BYPASS, null);
        PermissionsManager.registerPermission(Deathchest.PERMISSION_MAKE, RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission(Deathchest.PERMISSION_MAKE, RegisteredPermValue.OP);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        deathchest.save();
    }
}
