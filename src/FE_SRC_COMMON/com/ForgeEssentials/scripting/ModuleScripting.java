package com.ForgeEssentials.scripting;

import java.io.File;

import com.ForgeEssentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ModuleDir;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting {
	
	@ModuleDir
	public static File moduleDir;
	
static File loginplayer = new File(moduleDir, "login/player/");
	static File logingroup = new File(moduleDir, "login/group/");
	static File respawngroup = new File(moduleDir, "respawn/group/");
	static File respawnplayer = new File(moduleDir, "respawn/player/");
	
	
	@FEModule.PreInit
	public void preInit(FEModulePreInitEvent e){
		startup();
		GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
	}
	
	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e){
		e.registerServerCommand(new CommandScript());
	}
	
	public static void startup(){
		try{loginplayer.mkdirs();
			logingroup.mkdirs();
			respawngroup.mkdirs();
			respawnplayer.mkdirs();
			
		}catch (Exception e){
			OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
		}
	}
	@PermRegister
	public void registerPerms(IPermRegisterEvent e){
		e.registerPermissionLevel("ForgeEssentials.Scripting.script", RegGroup.ZONE_ADMINS);
	}
}
