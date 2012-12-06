package com.ForgeEssentials.core;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.permission.ModulePermissions;
import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
import com.ForgeEssentials.property.ModuleProperty;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * Initialize modules here. Yes. HERE. NOT ForgeEssentials.java! This is the springboard...
 * TODO trap NoClassDefFound if a certain module is not found, right now all are required and you use the config.
 */

public class ModuleLauncher
{
	public ModuleCommands		commands;
	public ModulePermissions	permission;
	public ModuleWorldControl	worldcontrol;
	public ModuleProperty		property;
	public ModuleWorldBorder	worldborder;
	public ModulePlayerLogger	playerLogger;

	public static boolean		permsEnabled	= true;
	public static boolean		cmdEnabled		= true;
	public static boolean		wcEnabled		= true;
	public static boolean		propEnabled		= true;
	public static boolean		borderEnabled	= false;
	public static boolean		loggerEnabled	= false;
	public static boolean		skEnabled		= false;

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		OutputHandler.SOP("If you would like to disable a module, please look in ForgeEssentials/core.cfg.");
		worldcontrol = new ModuleWorldControl();
		commands = new ModuleCommands();
		permission = new ModulePermissions();
		property = new ModuleProperty();
		worldborder = new ModuleWorldBorder();
		playerLogger = new ModulePlayerLogger();

		if (wcEnabled && skEnabled != true)
			worldcontrol.preLoad(e);

		if (cmdEnabled)
			commands.preLoad(e);

		if (permsEnabled)
			permission.preLoad(e);

		if (propEnabled)
			property.preLoad(e);

		if (borderEnabled)
			worldborder.preLoad(e);

		if (loggerEnabled)
			playerLogger.preLoad(e);
	}

	public void load(FMLInitializationEvent e)
	{

		if (wcEnabled && skEnabled != true)
			worldcontrol.load(e);

		if (cmdEnabled)
			commands.load(e);

		if (permsEnabled)
			permission.load(e);

		if (propEnabled)
			property.load(e);

		if (borderEnabled)
			worldborder.load(e);

		if (loggerEnabled)
			playerLogger.load(e);
	}

	public void serverStarting(FMLServerStartingEvent e)
	{

		if (wcEnabled && skEnabled != true)
			worldcontrol.serverStarting(e);

		if (cmdEnabled)
			commands.serverStarting(e);

		if (permsEnabled)
			permission.serverStarting(e);

		if (propEnabled)
			property.serverStarting(e);

		if (borderEnabled)
			worldborder.serverStarting(e);

		if (loggerEnabled)
			playerLogger.serverStarting(e);
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		if (wcEnabled && skEnabled != true)
			worldcontrol.serverStarted(e);

		if (cmdEnabled)
			commands.serverStarted(e);

		if (permsEnabled)
			permission.serverStarted(e);

		if (propEnabled)
			property.serverStarted(e);

		if (borderEnabled)
			worldborder.serverStarted(e);

		if (loggerEnabled)
			playerLogger.serverStarted(e);
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		if (loggerEnabled)
			playerLogger.serverStopping(e);
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		if (permsEnabled)
			permission.postLoad(e);

	}
}
