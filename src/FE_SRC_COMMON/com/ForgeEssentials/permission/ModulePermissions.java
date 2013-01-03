package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModulePermissions implements IFEModule
{
	public static ConfigPermissions						config;
	public static PermissionsHandler					pHandler;
	public static ZoneManager							zManager;
	public static SqlLiteHelper							sql; 

	public static File									permsFolder	= new File(ForgeEssentials.FEDIR, "/permissions/");

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		if (!permsFolder.exists() || !permsFolder.isDirectory())
			permsFolder.mkdirs();

		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();
		
		MinecraftForge.EVENT_BUS.register(zManager);
		
		// testing DB.
		//sql = new SqlLiteHelper();
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");

		MinecraftForge.EVENT_BUS.register(this);

		MinecraftForge.EVENT_BUS.post(new ForgeEssentialsPermissionRegistrationEvent());
		
		pHandler = new PermissionsHandler();
		PermissionsAPI.QUERY_BUS.register(pHandler);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		OutputHandler.SOP("Ending permissions registration period.");

		config = new ConfigPermissions();
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandZone());
		//e.registerServerCommand(new CommandPermSet());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
	}

	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerPermissionDefault("ForgeEssentials.permissions.zone", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.list", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.define", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.remove", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.redefine", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.setparent", true);

		event.registerPermissionDefault("ForgeEssentials.permissions.permissions.set", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.groups.create", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.groups.delete", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.groups.addplayer", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.player.setgroup", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.player.setsuperperm", true);

		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_ZONE_ADMINS, "ForgeEssentials.permissions.zone.setparent", true);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_DEFAULT, "ForgeEssentials.permissions.zone", false);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_MEMBERS, "ForgeEssentials.permissions.zone", false);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_OWNERS, "ForgeEssentials.permissions", true);

		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_DEFAULT, TeleportCenter.BYPASS_COOLDOWN, false);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_DEFAULT, TeleportCenter.BYPASS_WARMUP, false);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_OWNERS, TeleportCenter.BYPASS_COOLDOWN, true);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_OWNERS, TeleportCenter.BYPASS_WARMUP, true);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		/*
		for (Zone zone : ZoneManager.zoneMap.values())
			DataDriver.saveObject(zone);
			*/
	}

}
