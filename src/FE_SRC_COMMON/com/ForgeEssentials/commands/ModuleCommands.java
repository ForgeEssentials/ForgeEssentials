package com.ForgeEssentials.commands;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.commands.util.CommandRegistrar;
import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.commands.util.EventHandler;
import com.ForgeEssentials.commands.util.PlayerTrackerCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ModuleDir;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PostInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerPostInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerStop;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePostInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerStopEvent;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.Warp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

@FEModule(configClass = ConfigCmd.class, name = "CommandsModule", parentMod = ForgeEssentials.class)
public class ModuleCommands
{
	@Config
	public static ConfigCmd	conf;
	
	@ModuleDir
	public static File cmddir;

	public static boolean	removeDuplicateCommands;
	public DataDriver		data;

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		DataStorage.load();

		data = DataStorageManager.getReccomendedDriver();

		CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());

		// Vanilla Override
		e.registerServerCommand(new CommandKill());
		e.registerServerCommand(new CommandGive());
		e.registerServerCommand(new CommandI());
		e.registerServerCommand(new CommandClearInventory());
		e.registerServerCommand(new CommandGameMode());
	}

	@ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		loadWarps();
		TickRegistry.registerScheduledTickHandler(new TickHandlerCommands(), Side.SERVER);
		if (removeDuplicateCommands)
		{
			removeDuplicateCommands(FMLCommonHandler.instance().getMinecraftServerInstance());
		}
	}

	private void removeDuplicateCommands(MinecraftServer server)
	{
		if (server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				Set<String> commandNames = new HashSet<String>();
				Set<String> toRemoveNames = new HashSet<String>();

				Set cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandSet", "b");
				OutputHandler.debug("commandSet size: " + cmdList.size());

				for (Object cmdObj : cmdList)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (!commandNames.add(cmd.getCommandName()))
					{
						OutputHandler.debug("Duplicate command found! Name:" + cmd.getCommandName());
						toRemoveNames.add(cmd.getCommandName());
					}
				}
				Set toRemove = new HashSet();
				for (Object cmdObj : cmdList)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (toRemoveNames.contains(cmd.getCommandName()))
					{
						try
						{
							Class<?> cmdClass = cmd.getClass();
							Package pkg = cmdClass.getPackage();
							if (pkg == null || !pkg.getName().contains("ForgeEssentials"))
							{
								OutputHandler.debug("Removing command '" + cmd.getCommandName() + "' from class: " + cmdClass.getName());
								toRemove.add(cmd);
							}
						}
						catch (Exception e)
						{
							OutputHandler.debug("Can't remove " + cmd.getCommandName());
							OutputHandler.debug("" + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				cmdList.removeAll(toRemove);
				OutputHandler.debug("commandSet size: " + cmdList.size());
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b");

			}
			catch (Exception e)
			{
				OutputHandler.debug("Something broke: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.BasicCommands", true);
		event.registerPerm(this, RegGroup.MEMBERS, "ForgeEssentials.BasicCommands.compass", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.list", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.rules", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.motd", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.tps", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.modlist", true);
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		saveWarps();
	}

	private void saveWarps()
	{
		for (Warp warp : TeleportCenter.warps.values())
		{
			data.saveObject(warp);
		}
	}

	private void loadWarps()
	{
		Object[] objs = data.loadAllObjects(Warp.class);
		for (Object obj : objs)
		{
			Warp warp = ((Warp) obj);
			TeleportCenter.warps.put(warp.getName(), warp);
		}
	}
}