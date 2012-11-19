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
	public static FEPermissionConfig config;
	public static FEPermissionsHandler	pHandler;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		config = new FEPermissionConfig();
		Zone.GLOBAL = new Zone("__GLOBAL__");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		pHandler = new FEPermissionsHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{

	}

}
