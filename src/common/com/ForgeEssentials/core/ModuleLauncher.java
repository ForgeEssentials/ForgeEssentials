package com.ForgeEssentials.core;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.commands.CoreCommands;
import com.ForgeEssentials.permissions.ModulePermissions;
import com.ForgeEssentials.util.LibraryDetector;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Initialize modules here. Yes. HERE. NOT ForgeEssentials.java!
 */

public class ModuleLauncher implements IFEModule
{
	public CoreCommands corecmd;

	public ModuleCommands commands;
	public ModulePermissions permission;
	public ModuleWorldControl worldcontrol;

	public static boolean permsEnabled	= true;
	public static boolean cmdEnabled		= true;
	public static boolean wcEnabled		= true;

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		worldcontrol = new ModuleWorldControl();
		commands = new ModuleCommands();
		corecmd = new CoreCommands();
		permission = new ModulePermissions();
		
		corecmd.preLoad(e);
		
		if (wcEnabled && LibraryDetector.wepresent != true)
			worldcontrol.preLoad(e);
		
		if (cmdEnabled)
			commands.preLoad(e);
		
		if (permsEnabled)
			permission.preLoad(e);
	}

	public void load(FMLInitializationEvent e)
	{
		corecmd.load(e);
		
		if (wcEnabled && LibraryDetector.wepresent != true)
			worldcontrol.load(e);
		
		if (cmdEnabled)
			commands.load(e);
		
		if (permsEnabled)
			permission.load(e);
	}
	
	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		corecmd.serverStarting(e);
		
		if (wcEnabled && LibraryDetector.wepresent != true)
			worldcontrol.serverStarting(e);
		
		if (cmdEnabled)
			commands.serverStarting(e);
		
		if (permsEnabled)
			permission.serverStarting(e);
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		
	}

}
