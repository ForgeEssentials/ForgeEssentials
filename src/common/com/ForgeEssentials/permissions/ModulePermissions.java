package com.ForgeEssentials.permissions;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModulePermissions
{

	public FEPermissionHandler	pHandler;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{

	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		pHandler = new FEPermissionHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{

	}

}
