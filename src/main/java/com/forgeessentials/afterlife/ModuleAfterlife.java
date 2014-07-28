package com.forgeessentials.afterlife;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;

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

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {
        deathchest = new Deathchest();
        respawnDebuff = new RespawnDebuffHandler();
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        deathchest.load();
        FMLCommonHandler.instance().bus().register(respawnDebuff);
        APIRegistry.permReg.registerPermissionLevel(BASEPERM, RegGroup.OWNERS);

        APIRegistry.permReg.registerPermissionLevel(RespawnDebuffHandler.BYPASSPOTION, RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel(RespawnDebuffHandler.BYPASSSTATS, RegGroup.OWNERS);

        APIRegistry.permReg.registerPermissionLevel(Deathchest.PERMISSION_BYPASS, null);
        APIRegistry.permReg.registerPermissionLevel(Deathchest.PERMISSION_MAKE, RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel(Deathchest.PERMISSION_MAKE, RegGroup.OWNERS);
    }

    @FEModule.ServerStop
    public void serverStopping(FEModuleServerStopEvent e)
    {
        deathchest.save();
    }
}
