package com.ForgeEssentials.afterlife;

import java.io.File;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * This module handles Deathchest and respawn debuffs.
 * @author Dries007
 * 
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class, configClass = ConfigAfterlife.class)
public class ModuleAfterlife
{
	public static final String		BASEPERM	= "ForgeEssentials.Afterlife";
	@FEModule.Config
	public static ConfigAfterlife	conf;
	@FEModule.Instance
	public static ModuleAfterlife	instance;
	@FEModule.ModuleDir
	public static File				moduleDir;
	public Deathchest				deathchest;
	public RespawnDebuff			respawnDebuff;

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		deathchest = new Deathchest();
		respawnDebuff = new RespawnDebuff();
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		deathchest.load();
		GameRegistry.registerPlayerTracker(respawnDebuff);
	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		conf.loadDM();
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		deathchest.save();
	}

	@PermRegister
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(BASEPERM, RegGroup.OWNERS);

		event.registerPermissionLevel(RespawnDebuff.BYPASSPOTION, RegGroup.OWNERS);
		event.registerPermissionLevel(RespawnDebuff.BYPASSSTATS, RegGroup.OWNERS);

		event.registerPermissionLevel(Deathchest.PERMISSION_BYPASS, null);
		event.registerPermissionLevel(Deathchest.PERMISSION_MAKE, RegGroup.MEMBERS);
		event.registerPermissionLevel(Deathchest.PERMISSION_MAKE, RegGroup.OWNERS);
	}
}
