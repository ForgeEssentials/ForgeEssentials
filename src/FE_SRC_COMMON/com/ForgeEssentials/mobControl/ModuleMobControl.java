package com.ForgeEssentials.mobControl;

import java.io.File;
import java.util.HashMap;

import net.minecraft.entity.EntityList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
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
	public static final String				BASEPERM	= "ForgeEssentials.MobControl";
	@FEModule.Config
	public static ConfigMobControl			conf;
	@FEModule.Instance
	public static ModuleMobControl			instance;
	@FEModule.ModuleDir
	public static File						moduleDir;
	public static HashMap<String, Boolean>	nameList	= new HashMap<String, Boolean>();

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		for (Object obj : EntityList.classToStringMapping.values())
		{
			String name = (String) obj;
			nameList.put(name, true);
		}
		conf.updateGlobal();
	}

	@PermRegister(ident = "MobControl")
	public void registerPermissions(IPermRegisterEvent event)
	{
		
	}

	@ForgeSubscribe
	public void handleSpawn(LivingSpawnEvent e)
	{
		if(!nameList.get(e.entity.getEntityName()))
		{
			System.out.println(e.entity.getEntityName() + " canceled.");
			e.setResult(Result.DENY);
		}
	}
}
