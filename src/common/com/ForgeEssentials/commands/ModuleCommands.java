package com.ForgeEssentials.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.core.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Kindly register all commands in commands module here.
 */

public class ModuleCommands
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