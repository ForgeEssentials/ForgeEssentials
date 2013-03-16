package com.ForgeEssentials.questioner;

import java.io.File;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.AbstractDataDriver;

@FEModule(configClass = ConfigQuestioner.class, name = "QuestionerModule", parentMod = ForgeEssentials.class)
public class ModuleQuestioner
{
	@FEModule.Config
	public static ConfigQuestioner	conf;

	@FEModule.ModuleDir
	public static File				cmddir;

	public AbstractDataDriver		data;

	@Init
	public void load(FEModuleInitEvent e)
	{
		// MinecraftForge.EVENT_BUS.register(new EventHandler());
		// MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		// GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
		// CommandRegistrar.commandConfigs(conf.config);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		data = DataStorageManager.getReccomendedDriver();

		// CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
	}
	
	@PermRegister
	public static void registerPerms(IPermRegisterEvent event)
	{
		// TODO : register permissions
	}
}
