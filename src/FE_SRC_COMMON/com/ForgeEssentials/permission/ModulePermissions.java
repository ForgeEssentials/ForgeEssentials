package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import cpw.mods.fml.common.registry.GameRegistry;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ModuleDir;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerStop;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerStopEvent;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.permission.mcoverride.OverrideManager;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions
{
	// public static ConfigPermissions config;
	public static PermissionsHandler pHandler;
	public static ZoneManager zManager;
	public static SqlHelper sql;
	
	@Config
	public static ConfigPermissions config;

	@ModuleDir
	public static File permsFolder;
	
	protected static DataDriver data;

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();

		MinecraftForge.EVENT_BUS.register(zManager);

		// testing DB.
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");
		PermissionRegistrationEvent permreg = new PermissionRegistrationEvent();
		MinecraftForge.EVENT_BUS.post(permreg);
		OutputHandler.SOP("Ending permissions registration period.");

		// setup SQL
		sql = new SqlHelper(config);
		sql.putRegistrationperms(permreg.registered);

		pHandler = new PermissionsHandler();
		PermissionsAPI.QUERY_BUS.register(pHandler);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		// load zones...
		data = DataStorageManager.getDriverOfName("ForgeConfig");
		zManager.loadZones();

		//init perms and vMC command overrides
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
		OverrideManager.regOverrides((FMLServerStartingEvent) e.getFMLEvent());
		
		// setup PlayerTracker
		GameRegistry.registerPlayerTracker(new PlayerTracker());
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.ZONE_ADMINS, "ForgeEssentials.permissions.zone.setparent", true);
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.perm", true);
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.zone", true);
		
		event.registerPerm(this, RegGroup.OWNERS, Permission.ALL, true);

		event.registerPerm(this, RegGroup.OWNERS, TeleportCenter.BYPASS_COOLDOWN, true);
		event.registerPerm(this, RegGroup.OWNERS, TeleportCenter.BYPASS_WARMUP, true);
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		// save all the zones
		for (Zone zone : ZoneManager.zoneMap.values())
			data.saveObject(zone);
	}

}
