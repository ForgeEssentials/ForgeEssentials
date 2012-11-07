package com.ForgeEssentials.core;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.commands.Commands;
import com.ForgeEssentials.permissions.FEPermissionHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Add all the module init stuff here.
 * @author luacs1998
 */

public class Module {
	public FEPermissionHandler pHandler;
	public WorldControl worldcontrol;
	public Commands commands;
	    // load.
		public void preLoad(FMLPreInitializationEvent e)
		{
			FEConfig.loadConfig();
			Version.checkVersion();
			worldcontrol = new WorldControl();
			worldcontrol.preLoad(e);	
		}
		// load.
		public void load(FMLInitializationEvent e)
		{
			worldcontrol.load(e);
			pHandler = new FEPermissionHandler();
			MinecraftForge.EVENT_BUS.register(pHandler);
		}
		// load.
		public void serverStarting(FMLServerStartingEvent e)
		{
			commands.serverStarting(e);
			worldcontrol.serverStarting(e);
		}
}
