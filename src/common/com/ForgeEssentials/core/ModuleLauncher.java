package com.ForgeEssentials.core;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.commands.CoreCommands;
import com.ForgeEssentials.permissions.FEPermissionHandler;
import com.ForgeEssentials.permissions.ModulePermissions;

import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Initialize modules here. Yes. HERE. NOT ForgeEssentials.java!
 */

public class ModuleLauncher
{

	public CoreCommands			corecmd;

	public ModuleCommands		commands;
	public ModulePermissions	permission;
	public ModuleWorldControl	worldcontrol;

	public static boolean		permsEnabled	= true;
	public static boolean		cmdEnabled		= true;
	public static boolean		wcEnabled		= true;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		worldcontrol = new ModuleWorldControl();
		commands = new ModuleCommands();
		corecmd = new CoreCommands();
		permission = new ModulePermissions();
		
		wcEnabled = ForgeEssentials.instance.config.isModuleEnabled("WorldControl");
		cmdEnabled = ForgeEssentials.instance.config.isModuleEnabled("Commands");
		permsEnabled = ForgeEssentials.instance.config.isModuleEnabled("Permissions");
		
		
		corecmd.preLoad(e);
		
		if (wcEnabled)
			worldcontrol.preLoad(e);
		
		if (cmdEnabled)
			commands.preLoad(e);
		
		if (permsEnabled)
			permission.preLoad(e);
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		corecmd.load(e);
		
		if (wcEnabled)
			worldcontrol.load(e);
		
		if (cmdEnabled)
			commands.load(e);
		
		if (permsEnabled)
			permission.load(e);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		corecmd.serverStarting(e);
		
		if (wcEnabled)
			worldcontrol.serverStarting(e);
		
		if (cmdEnabled)
			commands.serverStarting(e);
		
		if (permsEnabled)
			permission.serverStarting(e);
	}

}
