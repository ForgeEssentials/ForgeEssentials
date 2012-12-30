package com.ForgeEssentials.core;

import com.ForgeEssentials.WorldBorder.ConfigWorldBorder;
import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.backup.ModuleBackup;
import com.ForgeEssentials.chat.ConfigChat;
import com.ForgeEssentials.chat.ModuleChat;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.economy.ModuleEconomy;
import com.ForgeEssentials.permission.ModulePermissions;
import com.ForgeEssentials.playerLogger.ConfigPlayerLogger;
import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
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
 */

public class ModuleLauncher
{
	public ModuleCommands		commands;
	public ModuleBackup			backup;
	public ModulePermissions	permission;
	public ModuleWorldControl	worldcontrol;
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
		 * AbrarSyed: depends on the module.
		 */
	}
	
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		OutputHandler.SOP("If you would like to disable a module, please look in ForgeEssentials/core.cfg.");
		try
		{
			worldcontrol = new ModuleWorldControl();
			backup = new ModuleBackup();
			commands = new ModuleCommands();
			permission = new ModulePermissions();
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
		{worldcontrol.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{backup.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{commands.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{permission.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{worldborder.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{playerLogger.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{economy.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{chat.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{protection.preLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{snooper.preLoad(e);
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
		}
		catch (NoClassDefFoundError e3)
		{}
		try{backup.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{commands.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{permission.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{worldborder.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{playerLogger.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{economy.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{chat.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{protection.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{snooper.load(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		
	}
	
	public void postLoad(FMLPostInitializationEvent e)
	{
		try
		{worldcontrol.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{backup.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{commands.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{permission.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{worldborder.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{playerLogger.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{chat.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{protection.postLoad(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{snooper.postLoad(e);
		}
		catch (NoClassDefFoundError e7)
		{}

	}

	public void serverStarting(FMLServerStartingEvent e)
	{

		try
		{worldcontrol.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{backup.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{commands.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{permission.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{worldborder.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{playerLogger.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{economy.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{chat.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{protection.serverStarting(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{snooper.serverStarting(e);
		}
		catch (NoClassDefFoundError e4)
		{}
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		try
		{worldcontrol.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{backup.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{commands.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{permission.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{worldborder.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{playerLogger.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{chat.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{protection.serverStarted(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{snooper.serverStarted(e);
		}
		catch (NoClassDefFoundError e5)
		{}
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		
		try{worldcontrol.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{backup.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{commands.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{permission.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{worldborder.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{playerLogger.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{chat.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{protection.serverStopping(e);
		}
		catch (NoClassDefFoundError e3)
		{}
		try{snooper.serverStopping(e);
		}
		catch (NoClassDefFoundError e6)
		{}
	}
}
