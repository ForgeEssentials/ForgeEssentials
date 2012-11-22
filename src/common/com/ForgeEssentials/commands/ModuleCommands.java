package com.ForgeEssentials.commands;

import com.ForgeEssentials.api.permissions.FEPermissionRegisterEvent;
import com.ForgeEssentials.api.permissions.IPermissionsRegister;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Kindly register all commands in commands module here.
 */

public class ModuleCommands implements IFEModule, IPermissionsRegister
{

	public ModuleCommands()
	{
		if (!ModuleLauncher.cmdEnabled)
			return;
	}

	// load.
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
	}

	// load.
	public void load(FMLInitializationEvent e)
	{
		
	}
	
	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		// commands
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandRules());
		e.registerServerCommand(new CommandButcher());
		e.registerServerCommand(new CommandRemove());
		e.registerServerCommand(new CommandKill());
		e.registerServerCommand(new CommandSmite());
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandSpawnAt());
		e.registerServerCommand(new CommandBack());
		e.registerServerCommand(new CommandRestart());
		e.registerServerCommand(new CommandServerDo());
		e.registerServerCommand(new CommandModlist());
		e.registerServerCommand(new CommandWarp());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerPermissions(FEPermissionRegisterEvent event)
	{
		event.registerGlobalPermission("ForgeEssentials.commands.remove", true);
		event.registerGlobalPermission("ForgeEssentials.commands.restart", true);
		event.registerGlobalPermission("ForgeEssentials.commands.rules", true);
		event.registerGlobalPermission("ForgeEssentials.commands.serverdo", true);
		event.registerGlobalPermission("ForgeEssentials.commands.smite", true);
		event.registerGlobalPermission("ForgeEssentials.commands.kill", true);
		event.registerGlobalPermission("ForgeEssentials.commands.modlist", true);
		event.registerGlobalPermission("ForgeEssentials.commands.motd", true);
	}
}