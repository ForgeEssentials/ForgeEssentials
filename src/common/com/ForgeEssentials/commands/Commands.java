package com.ForgeEssentials.commands;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Kindly register all commands in commands module here.
 */

public class Commands

{
	// implicit constructor Commands()
	
	// load.
	public void preLoad(FMLPreInitializationEvent event)
	{
		
	}
	
	// load.
	public void load(FMLInitializationEvent event)
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
				
	}
}