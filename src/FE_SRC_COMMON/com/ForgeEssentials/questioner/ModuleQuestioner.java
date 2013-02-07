package com.ForgeEssentials.questioner;

import java.io.File;

import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ModuleDir;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerPostInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerStop;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerStopEvent;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.OutputHandler;

@FEModule(configClass = ConfigQuestioner.class, name = "QuestionerModule", parentMod = ForgeEssentials.class)
public class ModuleQuestioner
{
	@Config
	public static ConfigQuestioner conf;
	
	@ModuleDir
	public static File cmddir;

	public DataDriver		data;

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		OutputHandler.SOP("Questioner module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
//		MinecraftForge.EVENT_BUS.register(new EventHandler());
//		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
//		GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
//		CommandRegistrar.commandConfigs(conf.config);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		DataStorage.load();

		data = DataStorageManager.getReccomendedDriver();
		
//		CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
	}

	@ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		
	}
	
	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
//		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.BasicCommands", true);
//		event.registerPerm(this, RegGroup.MEMBERS, "ForgeEssentials.BasicCommands.compass", true);
//		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.list", true);
//		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.rules", true);
//		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.motd", true);
//		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.tps", true);
//		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.modlist", true);
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		
	}
}
