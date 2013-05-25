package com.ForgeEssentials.commands;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PropQueryBlanketZone;
import com.ForgeEssentials.commands.shortcut.ShortcutCommands;
import com.ForgeEssentials.commands.util.CommandDataManager;
import com.ForgeEssentials.commands.util.CommandRegistrar;
import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.commands.util.EventHandler;
import com.ForgeEssentials.commands.util.MCStatsHelper;
import com.ForgeEssentials.commands.util.MobTypeLoader;
import com.ForgeEssentials.commands.util.PacketAnalyzerCmd;
import com.ForgeEssentials.commands.util.PlayerTrackerCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.compat.CompatMCStats;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerPostInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerStopEvent;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(configClass = ConfigCmd.class, name = "CommandsModule", parentMod = ForgeEssentials.class)
public class ModuleCommands
{
	@FEModule.Config
	public static ConfigCmd			conf;

	@FEModule.ModuleDir
	public static File				cmddir;

	public static EventHandler		eventHandler	= new EventHandler();
	private static MCStatsHelper	mcstats			= new MCStatsHelper();

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		MobTypeLoader.preLoad(e);
		GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(eventHandler);
		CommandRegistrar.commandConfigs(conf.config);
		ShortcutCommands.loadConfig(cmddir);
		CompatMCStats.registerStats(mcstats);
		new PacketAnalyzerCmd();
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
		ShortcutCommands.load();
	}
	
	@FEModule.Reload
	public void reload(ICommandSender sender)
	{
	    ShortcutCommands.parseConfig();
	    ShortcutCommands.load();
	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		TickRegistry.registerScheduledTickHandler(new TickHandlerCommands(), Side.SERVER);
		CommandDataManager.load();

		PropQueryBlanketZone query = new PropQueryBlanketZone(CommandSetSpawn.SPAWN_PROP, ZoneManager.getGLOBAL(), false);
		APIRegistry.perms.getPermissionProp(query);

		// nothing set for the global??
		if (!query.hasValue())
		{
			ChunkCoordinates point = FunctionHelper.getDimension(0).provider.getSpawnPoint();
			String val = "0;" + point.posX + ";" + point.posY + ";" + point.posZ;
			APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, CommandSetSpawn.SPAWN_PROP, val, ZoneManager.getGLOBAL().getZoneName());
		}
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		CommandRegistrar.registerPermissions(event);
		event.registerPermissionLevel("ForgeEssentials.BasicCommands._ALL_", RegGroup.OWNERS);

		// ensures on ServerStart
		// event.registerPermissionProp("ForgeEssentials.BasicCommands.spawnPoint", "0;0;0;0");
		event.registerPermissionProp(CommandSetSpawn.SPAWN_TYPE_PROP, "bed"); // bed, point, none
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		CommandDataManager.save();
	}

	
}
