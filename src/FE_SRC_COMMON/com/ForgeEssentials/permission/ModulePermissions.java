package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.DataStorageManager;
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
	// public static ConfigPermissions config;
	public static PermissionsHandler pHandler;
	public static ZoneManager zManager;
	public static SqlHelper sql;
	public static ConfigPermissions config;

	public static File permsFolder = new File(ForgeEssentials.FEDIR,
			"/permissions/");
	protected static DataDriver data;

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		if (!permsFolder.exists() || !permsFolder.isDirectory())
		{
			permsFolder.mkdirs();
		}

		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();

		MinecraftForge.EVENT_BUS.register(zManager);

		// testing DB.
		config = new ConfigPermissions();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");
		PermissionRegistrationEvent permreg = new PermissionRegistrationEvent();
		MinecraftForge.EVENT_BUS.post(permreg);
		OutputHandler.SOP("Ending permissions registration period.");

		// setup SQL
		sql = new SqlHelper(config);
		sql.putRegistrationperms(permreg.registerred);

		pHandler = new PermissionsHandler();
		PermissionsAPI.QUERY_BUS.register(pHandler);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		// load zones...
		data = DataStorageManager.getDriverOfName("ForgeConfig");
		zManager.loadZones();

		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.ZONE_ADMINS,
				"ForgeEssentials.permissions.zone.setparent", true);
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.perm", true);
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.zone", true);

		event.registerPerm(this, RegGroup.OWNERS,
				TeleportCenter.BYPASS_COOLDOWN, true);
		event.registerPerm(this, RegGroup.OWNERS, TeleportCenter.BYPASS_WARMUP,
				true);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		for (Zone zone : ZoneManager.zoneMap.values())
		{
			data.saveObject(zone);
		}
	}

	@Override
	public IModuleConfig getConfig()
	{
		return config;
	}

}
