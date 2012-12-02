package com.ForgeEssentials.commands;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.permissions.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModuleCommands implements IFEModule
{

	public ModuleCommands()
	{
		if (!ModuleLauncher.cmdEnabled)
			return;
	}

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
	}

	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandRules());
		e.registerServerCommand(new CommandButcher());
		e.registerServerCommand(new CommandRemove());
		e.registerServerCommand(new CommandKill());
		e.registerServerCommand(new CommandSmite());
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandSpawn());
		e.registerServerCommand(new CommandBack());
		e.registerServerCommand(new CommandRestart());
		e.registerServerCommand(new CommandServerDo());
		e.registerServerCommand(new CommandModlist());
		e.registerServerCommand(new CommandWarp());
		e.registerServerCommand(new CommandBackup());
		e.registerServerCommand(new CommandBurn());
		e.registerServerCommand(new CommandRepair());
		e.registerServerCommand(new CommandHeal());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
	}

	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerGlobalPermission("ForgeEssentials.commands", true);
		event.registerGlobalPermission("ForgeEssentials.commands.remove", true);
		event.registerGlobalPermission("ForgeEssentials.commands.restart", true);
		event.registerGlobalPermission("ForgeEssentials.commands.rules", true);
		event.registerGlobalPermission("ForgeEssentials.commands.serverdo", true);
		event.registerGlobalPermission("ForgeEssentials.commands.smite", true);
		event.registerGlobalPermission("ForgeEssentials.commands.kill", true);
		event.registerGlobalPermission("ForgeEssentials.commands.modlist", true);
		event.registerGlobalPermission("ForgeEssentials.commands.motd", true);
		event.registerGlobalPermission("ForgeEssentials.commands.burn", true);
		event.registerGlobalPermission("ForgeEssentials.commands.list", true);
		event.registerGlobalPermission("ForgeEssentials.commands.compass", true);
		event.registerGlobalPermission("ForgeEssentials.commands.repair", true);
		event.registerGlobalPermission("ForgeEssentials.commands.heal", true);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e) {
		// TODO Auto-generated method stub
		
	}
}