package com.ForgeEssentials.core;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.commands.Commands;
import com.ForgeEssentials.permissions.FEPermissionHandler;
import com.ForgeEssentials.permissions.Permissions;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Add all the module init stuff here.
 * @author luacs1998
 */

public class Module {
	
	public WorldControl worldcontrol;
	public Commands commands;
	public Permissions perms;
	    // load.
		public void preLoad(FMLPreInitializationEvent e)
		{
			
			worldcontrol = new WorldControl();
			worldcontrol.preLoad(e);	
		}
		// load.
		public void load(FMLInitializationEvent e)
		{
			worldcontrol.load(e);
			perms.load(e);
		}
		// load.
		public void serverStarting(FMLServerStartingEvent e)
		{
			commands.serverStarting(e);
			worldcontrol.serverStarting(e);
		}
}
