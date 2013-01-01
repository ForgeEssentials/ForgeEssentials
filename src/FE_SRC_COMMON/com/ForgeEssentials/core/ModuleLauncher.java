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

import net.minecraft.command.ICommandSender;

import java.util.ArrayList;

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
	public ArrayList<IFEModule>	modules;

	// note to self: if possible, make this classload.

	/*
	 * I put this here so we won't forget to add new modules.
	 */
	public static void ReloadConfigs(ICommandSender sender)
	{
		// requires new method in iFEModule.
	}

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		OutputHandler.SOP("If you would like to disable a module, please look in ForgeEssentials/core.cfg.");

		modules = new ArrayList<IFEModule>();
		IFEModule instance;

		/*
		 * commands = new ModuleCommands();
		 * permission = new ModulePermissions();
		 * worldborder = new ModuleWorldBorder();
		 * playerLogger = new ModulePlayerLogger();
		 * economy = new ModuleEconomy();
		 * chat = new ModuleChat();
		 * protection = new ModuleProtection();
		 * snooper = new ModuleSnooper();
		 */

		try
		{
			instance = new ModuleWorldControl();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleBackup();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleCommands();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModulePermissions();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleWorldBorder();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModulePlayerLogger();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleEconomy();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleChat();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleProtection();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}

		try
		{
			instance = new ModuleSnooper();
			modules.add(instance);
			OutputHandler.SOP("discoverred " + instance.getClass().toString());
		}
		catch (NoClassDefFoundError e1)
		{
			// Nothing to see here, carry on, carry on
		}
		
		for (IFEModule module : modules)
			module.preLoad(e);
	}

	public void load(FMLInitializationEvent e)
	{
		for (IFEModule module : modules)
			module.load(e);
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		for (IFEModule module : modules)
			module.postLoad(e);
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		for (IFEModule module : modules)
			module.serverStarting(e);
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		for (IFEModule module : modules)
			module.serverStarted(e);
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		for (IFEModule module : modules)
			module.serverStopping(e);
	}
}
