package com.forgeessentials.commands;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.shortcut.ShortcutCommands;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandRegistrar;
import com.forgeessentials.commands.util.ConfigCmd;
import com.forgeessentials.commands.util.EventHandler;
import com.forgeessentials.commands.util.MCStatsHelper;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.commands.util.PacketAnalyzerCmd;
import com.forgeessentials.commands.util.PlayerTrackerCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CompatMCStats;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.teleport.util.TickHandlerTP;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;

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
		TickRegistry.registerScheduledTickHandler(new TickHandlerTP(), Side.SERVER);
		CommandDataManager.load();
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		CommandRegistrar.registerPermissions(event);
		event.registerPermissionLevel("ForgeEssentials.BasicCommands._ALL_", RegGroup.OWNERS);
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		CommandDataManager.save();
	}

	
}
