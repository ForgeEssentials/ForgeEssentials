package com.ForgeEssentials.questioner;

import java.io.File;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerStop;
import com.ForgeEssentials.data.AbstractDataDriver;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerStopEvent;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "Questioner", parentMod = ForgeEssentials.class)
public class ModuleQuestioner
{
	@FEModule.ModuleDir
	public static File				cmddir;
	
	public static ModuleQuestioner instance;

	public AbstractDataDriver		data;

	@Init
	public void load(FEModuleInitEvent e)
	{
		
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		data = DataStorageManager.getReccomendedDriver();

		TickRegistry.registerScheduledTickHandler(new QuestionCenter(), Side.SERVER);
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		
	}
}
