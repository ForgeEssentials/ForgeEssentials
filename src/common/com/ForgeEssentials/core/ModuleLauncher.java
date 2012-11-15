package com.ForgeEssentials.core;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.commands.Commands;
import com.ForgeEssentials.core.commands.CoreCommands;
import com.ForgeEssentials.permissions.FEPermissionHandler;
import com.ForgeEssentials.permissions.Permissions;

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

public class ModuleLauncher {
	
	public WorldControl worldcontrol;
	public Commands commands;
	public CoreCommands corecmd;
	public Permissions permission;
	public static boolean cmdenabled = true;
	public static boolean wcenabled = true;
	
	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		worldcontrol = new WorldControl();
		commands = new Commands();
		corecmd = new CoreCommands();
		permission = new Permissions();
		if (wcenabled = true){
		worldcontrol.preLoad(e);
		}
		if (cmdenabled = true){
		commands.preLoad(e);
		}
		corecmd.preLoad(e);
		permission.preLoad(e);
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		if (wcenabled = true){
		worldcontrol.load(e);
		}
		if (cmdenabled = true){
		commands.load(e);
		}
		corecmd.load(e);
		permission.load(e);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		if (cmdenabled = true){
		commands.serverStarting(e);
		}
		corecmd.serverStarting(e);
		if (wcenabled = true){
		worldcontrol.serverStarting(e);
		}
		permission.serverStarting(e);

	}

}
