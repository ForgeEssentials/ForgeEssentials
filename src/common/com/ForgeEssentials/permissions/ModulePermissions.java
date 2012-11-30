package com.ForgeEssentials.permissions;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;

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
	public static ConfigPermissions		config;
	public static PermissionsHandler	pHandler;
	public static ZoneManager			zManager;
	public static boolean				permsVerbose	= false;
	
	public static File permsFolder = new File(ForgeEssentials.FEDIR, "/permissions/");
	
	private ForgeEssentialsPermissionRegistrationEvent	permEvent;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		if (!permsFolder.exists() || !permsFolder.isDirectory())
			permsFolder.mkdirs();
		
		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();
		ZoneManager.GLOBAL = new Zone("__GLOBAL__");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");
		
		MinecraftForge.EVENT_BUS.register(this);
		
		permEvent = new ForgeEssentialsPermissionRegistrationEvent();
		pHandler = new PermissionsHandler();
		
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		OutputHandler.SOP("Ending permissions registration period.");
		
		MinecraftForge.EVENT_BUS.post(null);
		
		config = new ConfigPermissions();
		// TODO Auto-generated method stub

	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		// TODO Auto-generated method stub

	}

	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.list", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.define", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.remove", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.redefine", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.setparent", true);
	}

}
