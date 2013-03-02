package com.ForgeEssentials.mobControl;

import java.io.File;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.core.ForgeEssentials;

/**
 * This module allows per zone mob spawn control
 * @author Dries007
 * 
 */

@FEModule(name = "MobControl", parentMod = ForgeEssentials.class, configClass = ConfigMobControl.class)
public class ModuleMobControl
{
	public static final String		BASEPERM	= "ForgeEssentials.MobControl";
	@FEModule.Config
	public static ConfigMobControl	conf;
	@FEModule.Instance
	public static ModuleMobControl	instance;
	@FEModule.ModuleDir
	public static File				moduleDir;

	@PermRegister(ident = "MobControl")
	public void registerPermissions(IPermRegisterEvent event)
	{

	}
}
