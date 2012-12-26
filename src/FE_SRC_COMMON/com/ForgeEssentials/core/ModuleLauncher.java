package com.ForgeEssentials.core;

import java.util.logging.Level;

import com.ForgeEssentials.WorldBorder.ConfigWorldBorder;
import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.chat.ConfigChat;
import com.ForgeEssentials.chat.ModuleChat;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.economy.ModuleEconomy;
import com.ForgeEssentials.permission.ModulePermissions;
import com.ForgeEssentials.playerLogger.ConfigPlayerLogger;
import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
import com.ForgeEssentials.property.ModuleProperty;
import com.ForgeEssentials.protection.ConfigProtection;
import com.ForgeEssentials.protection.ModuleProtection;
import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.ModuleSnooper;
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
	public ModuleProtection		protection;
	public ModuleSnooper 		snooper;
	
	// note to self: if possible, make this classload.

	/*
	 * I put this here so we won't forget to add new modules.
	 */
	public static void ReloadConfigs()
	{
		ModuleCommands.conf = new ConfigCmd();
		ModuleChat.conf = new ConfigChat();
		ForgeEssentials.config = new CoreConfig();
		ModulePlayerLogger.config = new ConfigPlayerLogger();
		ModuleWorldBorder.config = new ConfigWorldBorder();
		ModuleWorldControl.doConfig();
		ModuleProtection.config = new ConfigProtection();
		ModuleSnooper.configSnooper = new ConfigSnooper();
		/*
		 * TODO: @AbarSyed Can the permissions be reloaded from file after launch? if so, you can add that here.
		 */
	}
	
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
			protection = new ModuleProtection();
			snooper = new ModuleSnooper();
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			worldcontrol.preLoad(e);
			commands.preLoad(e);
			permission.preLoad(e);
			property.preLoad(e);
			worldborder.preLoad(e);
			playerLogger.preLoad(e);
			economy.preLoad(e);
			chat.preLoad(e);
			protection.preLoad(e);
			snooper.preLoad(e);
		}
		catch (NoClassDefFoundError e2)
		{
			OutputHandler.SOP("One or more modules could not be found.");
		}
	}

	public void load(FMLInitializationEvent e)
	{

		try
		{
			worldcontrol.load(e);
			commands.load(e);
			permission.load(e);
			property.load(e);
			worldborder.load(e);
			playerLogger.load(e);
			economy.load(e);
			chat.load(e);
			protection.load(e);
			snooper.load(e);
		}
		catch (NoClassDefFoundError e3)
		{

		}
	}

	public void serverStarting(FMLServerStartingEvent e)
	{

		try
		{
			worldcontrol.serverStarting(e);
			commands.serverStarting(e);
			permission.serverStarting(e);
			property.serverStarting(e);
			worldborder.serverStarting(e);
			playerLogger.serverStarting(e);
			economy.serverStarting(e);
			chat.serverStarting(e);
			protection.serverStarting(e);
			snooper.serverStarting(e);
		}
		catch (NoClassDefFoundError e4)
		{

		}
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		try
		{
			worldcontrol.serverStarted(e);
			commands.serverStarted(e);
			permission.serverStarted(e);
			property.serverStarted(e);
			worldborder.serverStarted(e);
			playerLogger.serverStarted(e);
			chat.serverStarted(e);
			protection.serverStarted(e);
			snooper.serverStarted(e);
		}
		catch (NoClassDefFoundError e5)
		{

		}
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		try
		{
			playerLogger.serverStopping(e);
			protection.serverStopping(e);
			snooper.serverStopping(e);
		}
		catch (NoClassDefFoundError e6)
		{

		}
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		try
		{
			permission.postLoad(e);
			protection.postLoad(e);
			snooper.postLoad(e);
		}
		catch (NoClassDefFoundError e7)
		{

		}

	}
}
