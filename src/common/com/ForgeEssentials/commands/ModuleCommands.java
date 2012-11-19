package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Kindly register all commands in commands module here.
 */

public class ModuleCommands

{
	// implicit constructor Commands()
	
	// load.
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
	}
	
	// load.
	public void load(FMLInitializationEvent e)
	{
		
	}
	
	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		// commands
				e.registerServerCommand(new CommandMotd());
				e.registerServerCommand(new CommandRules());
				e.registerServerCommand(new CommandButcher());
				e.registerServerCommand(new CommandRemove());
				e.registerServerCommand(new CommandHome());
				e.registerServerCommand(new CommandRestart());
				e.registerServerCommand(new CommandServerDo());
				
	}
}