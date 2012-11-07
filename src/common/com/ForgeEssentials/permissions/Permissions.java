package com.ForgeEssentials.permissions;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.Module;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class Permissions extends Module{

	public FEPermissionHandler pHandler;
	
	@Override
	public void preLoad(FMLPreInitializationEvent e) {
		
	}

	@Override
	public void load(FMLInitializationEvent e) {
		pHandler = new FEPermissionHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) {
		//add commands here
	}

}
