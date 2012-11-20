package com.ForgeEssentials.permissions;

import com.ForgeEssentials.api.permissions.PermissionsHandler;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModulePermissions
{
	public static PermissionsConfig	config;
	public static PermissionsHandler pHandler;
	public static ZoneManager zManager;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Permissions module is enabled. Loading...");
		config = new PermissionsConfig();
		zManager = new ZoneManager();
		ZoneManager.GLOBAL = new Zone("__GLOBAL__");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		pHandler = new PermissionsHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{

	}

}
