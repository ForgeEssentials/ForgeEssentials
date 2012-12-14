package com.ForgeEssentials.core;

import java.util.logging.Level;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.chat.ModuleChat;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.economy.ModuleEconomy;
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
	public ModuleEconomy		economy;
	public ModuleChat			chat;

	public static boolean		chatEnabled		= true;
	public static boolean		permsEnabled	= true;
	public static boolean		cmdEnabled		= true;
	public static boolean		wcEnabled		= true;
	public static boolean		propEnabled		= true;
	public static boolean		economyEnabled	= true;
	public static boolean		borderEnabled	= false;
	public static boolean		loggerEnabled	= false;

	// note to self: if possible, make this classload.

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		OutputHandler.SOP("If you would like to disable a module, please look in ForgeEssentials/core.cfg.");
		try
		{
			worldcontrol = new ModuleWorldControl();
			commands = new ModuleCommands();
			permission = new ModulePermissions();
			property = new ModuleProperty();
			worldborder = new ModuleWorldBorder();
			playerLogger = new ModulePlayerLogger();
			economy = new ModuleEconomy();
			chat = new ModuleChat();
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			if (wcEnabled)
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

			if (economyEnabled)
				economy.preLoad(e);

			if (chatEnabled)
				chat.preLoad(e);
		}
		catch (NullPointerException e2)
		{
			OutputHandler.felog.log(Level.SEVERE, "A Module has errored!!", e2);
			throw new RuntimeException("ForgeEssentials ModuleLoading error");
		}
	}

	public void load(FMLInitializationEvent e)
	{

		try
		{
			if (wcEnabled)
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

			if (economyEnabled)
				economy.load(e);

			if (chatEnabled)
				chat.load(e);
		}
		catch (NullPointerException e3)
		{

		}
	}

	public void serverStarting(FMLServerStartingEvent e)
	{

		try
		{
			if (wcEnabled)
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

			if (economyEnabled)
				economy.serverStarting(e);

			if (chatEnabled)
				chat.serverStarting(e);
		}
		catch (NullPointerException e4)
		{

		}
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		try
		{
			if (wcEnabled)
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

			if (chatEnabled)
				chat.serverStarted(e);
		}
		catch (NullPointerException e5)
		{

		}
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		try
		{
			if (loggerEnabled)
				playerLogger.serverStopping(e);
		}
		catch (NullPointerException e6)
		{

		}
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		try
		{
			if (permsEnabled)
				permission.postLoad(e);
		}
		catch (NullPointerException e7)
		{

		}

	}
}
