package com.ForgeEssentials.questioner;

import java.io.File;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.AbstractDataDriver;

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
