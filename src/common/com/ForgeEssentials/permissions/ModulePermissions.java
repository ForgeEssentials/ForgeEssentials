package com.ForgeEssentials.permissions;

import com.ForgeEssentials.api.permissions.FEPermissionRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsHandler;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModulePermissions implements IFEModule
{
	public static PermissionsConfig		config;
	public static PermissionsHandler	pHandler;
	public static ZoneManager			zManager;
	public static boolean				permsVerbose	= false;
	private FEPermissionRegisterEvent	permEvent;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();
		ZoneManager.GLOBAL = new Zone("__GLOBAL__");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");
		permEvent = new FEPermissionRegisterEvent();
		pHandler = new PermissionsHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		OutputHandler.SOP("Ending permissions registration period.");
		permEvent.endPermissionRegistration(this);
		config = new PermissionsConfig();
		// TODO Auto-generated method stub

	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{

	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		// TODO Auto-generated method stub

	}

}
