package com.forgeessentials.scripting;

import java.io.File;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting {
	
	public static File moduleDir = new File(ForgeEssentials.FEDIR, "scripting/");
	
static File loginplayer = new File(moduleDir, "login/player/");
	static File logingroup = new File(moduleDir, "login/group/");
	static File respawngroup = new File(moduleDir, "respawn/group/");
	static File respawnplayer = new File(moduleDir, "respawn/player/");
	
	
	@FEModule.PreInit
	public void preInit(FEModulePreInitEvent e){
		OutputHandler.felog.info("Scripts are being read from " + moduleDir.getAbsolutePath());
		startup();
		GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
	}
	
	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e){
		e.registerServerCommand(new CommandScript());
	}
	
	public static void startup(){
		try{moduleDir.mkdirs();
			loginplayer.mkdirs();
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
