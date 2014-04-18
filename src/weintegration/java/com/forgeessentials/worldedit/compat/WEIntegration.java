package com.forgeessentials.worldedit.compat;

import java.io.File;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerInit;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "WEIntegrationTools", parentMod = ForgeEssentials.class, configClass = WEIntegrationToolsConfig.class)
public class WEIntegration{

	protected static int syncInterval;
	
	@FEModule.Config
	public static WEIntegrationToolsConfig config;
	
	@FEModule.ModuleDir
	public static File moduleDir;
	
	@Init
	//@ModuleEventHandler
	public void load(FEModuleInitEvent e){
		if (!EnvironmentChecker.worldEditInstalled){
			throw new RuntimeException("[ForgeEssentials] You cannot run the FE integration tools for WorldEdit without installing WorldEdit Forge.");
		}
		EnvironmentChecker.worldEditFEtoolsInstalled = true;
		TickRegistry.registerScheduledTickHandler(new SelectionSyncHandler(syncInterval), Side.SERVER);
	}
	
	@ServerInit
	//@ModuleEventHandler
	public void serverStart(FEModuleServerInitEvent e){
		
	}
	
	

}
