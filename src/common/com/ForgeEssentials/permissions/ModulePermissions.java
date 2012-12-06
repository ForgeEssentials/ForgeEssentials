package com.ForgeEssentials.permissions;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;

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
	public static GroupManager							gManager;

	public static File									permsFolder		= new File(ForgeEssentials.FEDIR, "/permissions/");

	private ForgeEssentialsPermissionRegistrationEvent	permEvent;

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		if (!permsFolder.exists() || !permsFolder.isDirectory())
			permsFolder.mkdirs();

		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();
		gManager = new GroupManager();
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");

		MinecraftForge.EVENT_BUS.register(this);

		permEvent = new ForgeEssentialsPermissionRegistrationEvent();
		pHandler = new PermissionsHandler();
		PermissionsAPI.QUERY_BUS.register(pHandler);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		OutputHandler.SOP("Ending permissions registration period.");

		config = new ConfigPermissions();
		// TODO Auto-generated method stub

	}

	@Override
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
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.list", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.define", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.remove", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.redefine", true);
		event.registerPermissionDefault("ForgeEssentials.permissions.zone.setparent", true);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		// TODO Auto-generated method stub

	}

}
