package com.ForgeEssentials.permission;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.permission.mcoverride.OverrideManager;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions
{
	// public static ConfigPermissions config;
	public static PermissionsHandler pHandler;
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
		ZoneManager.manager = new ZoneHelper();
		PermissionsAPI.manager = new PermissionsHelper();

		MinecraftForge.EVENT_BUS.register(ZoneManager.manager);

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
		data = DataStorageManager.getReccomendedDriver();
		((ZoneHelper) ZoneManager.manager).loadZones();
		
		if (config.importBool)
			sql.importPerms(config.importDir);

		//init perms and vMC command overrides
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
		OverrideManager.regOverrides((FMLServerStartingEvent) e.getFMLEvent());
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
		for (Zone zone : ZoneManager.getZoneList())
		{
			if (zone == null || zone.isGlobalZone() || zone.isWorldZone())
				continue;
			data.saveObject(zone);			
		}
	}

}
