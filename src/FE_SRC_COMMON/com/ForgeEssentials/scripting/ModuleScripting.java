package com.ForgeEssentials.scripting;

import java.io.File;
import java.io.PrintWriter;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ModuleDir;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;

import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting {
	
	@ModuleDir
	public static File moduleDir;
	
	@FEModule.PreInit
	public void preInit(FEModulePreInitEvent e){
		startup();
		GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
	}
	
	public static void startup(){
		try{
			if (!ScriptPlayerTracker.scriptfolder.exists()){
				ScriptPlayerTracker.scriptfolder.mkdirs();
				
			}else if (!ScriptPlayerTracker.loginplayer.exists()){
				ScriptPlayerTracker.loginplayer.mkdirs();
			}else if (!ScriptPlayerTracker.logingroup.exists()){
				ScriptPlayerTracker.logingroup.mkdirs();
			}else if (!ScriptPlayerTracker.respawngroup.exists()){
				ScriptPlayerTracker.respawngroup.mkdirs();
			}else if (!ScriptPlayerTracker.respawnplayer.exists()){
				ScriptPlayerTracker.respawnplayer.mkdirs();
			}
		}catch (Exception e){
			OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
		}
	}

}
