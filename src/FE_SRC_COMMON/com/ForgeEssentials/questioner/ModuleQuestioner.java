package com.ForgeEssentials.questioner;

import java.io.File;

import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.AbstractDataDriver;
import com.ForgeEssentials.util.events.PermissionSetEvent;

@FEModule(configClass = ConfigQuestioner.class, name = "QuestionerModule", parentMod = ForgeEssentials.class)
public class ModuleQuestioner
{
	@FEModule.Config
	public static ConfigQuestioner	conf;

	@FEModule.ModuleDir
	public static File				cmddir;

	public AbstractDataDriver		data;

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{

	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		// MinecraftForge.EVENT_BUS.register(new EventHandler());
		// MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		// GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
		// CommandRegistrar.commandConfigs(conf.config);
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		data = DataStorageManager.getReccomendedDriver();

		// CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{

	}

	@ForgeSubscribe
	public void registerPermissions(PermissionSetEvent event)
	{
		// event.registerPerm(this, RegGroup.OWNERS,
		// "ForgeEssentials.BasicCommands", true);
		// event.registerPerm(this, RegGroup.MEMBERS,
		// "ForgeEssentials.BasicCommands.compass", true);
		// event.registerPerm(this, RegGroup.GUESTS,
		// "ForgeEssentials.BasicCommands.list", true);
		// event.registerPerm(this, RegGroup.GUESTS,
		// "ForgeEssentials.BasicCommands.rules", true);
		// event.registerPerm(this, RegGroup.GUESTS,
		// "ForgeEssentials.BasicCommands.motd", true);
		// event.registerPerm(this, RegGroup.GUESTS,
		// "ForgeEssentials.BasicCommands.tps", true);
		// event.registerPerm(this, RegGroup.GUESTS,
		// "ForgeEssentials.BasicCommands.modlist", true);
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{

	}
}
