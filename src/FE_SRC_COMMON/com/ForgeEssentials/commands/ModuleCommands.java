package com.ForgeEssentials.commands;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.commands.util.CommandRegistrar;
import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.commands.util.EventHandler;
import com.ForgeEssentials.commands.util.PlayerTrackerCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.moduleLauncher.IFEModule;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.Warp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

public class ModuleCommands implements IFEModule
{
	public static ConfigCmd conf;
	public static boolean removeDuplicateCommands;
	public DataDriver data;

	public ModuleCommands()
	{

	}

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
		conf = new ConfigCmd();
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		DataStorage.load();

		data = DataStorageManager.getDriverOfName("ForgeConfig");

		CommandRegistrar.load(e);
		
		// Vanilla Override
		e.registerServerCommand(new CommandKill());
		e.registerServerCommand(new CommandGive());
		e.registerServerCommand(new CommandI());
		e.registerServerCommand(new CommandClearInventory());
		e.registerServerCommand(new CommandGameMode());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
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
				
				Set cmds = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), "commandSet", "b");
				
				for (Object cmdObj : cmds)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (!commandNames.add(cmd.getCommandName()))
					{
						OutputHandler.debug("Duplicate command found! Name:" + cmd.getCommandName());
						toRemoveNames.add(cmd.getCommandName());
					}
				}
				Set toRemove = new HashSet();
				for (Object cmdObj : cmds)
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
								toRemove.add(cmd.getCommandName());
							}
						}
						catch (Exception e)
						{
							OutputHandler.debug("Can't remove " + cmd.getCommandName());
							OutputHandler.debug(""+e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				cmds.removeAll(toRemove);
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), cmds, "commandSet", "b");
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

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
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

	@Override
	public IModuleConfig getConfig()
	{
		return conf;
	}
}