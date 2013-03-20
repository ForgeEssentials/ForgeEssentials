package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.AbstractDataDriver;
import com.ForgeEssentials.permission.autoPromote.AutoPromote;
import com.ForgeEssentials.permission.autoPromote.AutoPromoteManager;
import com.ForgeEssentials.permission.autoPromote.CommandAutoPromote;
import com.ForgeEssentials.permission.mcoverride.OverrideManager;
import com.ForgeEssentials.util.TeleportCenter;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions
{
	public static SqlHelper				sql;

	@FEModule.Config
	public static ConfigPermissions		config;

	@FEModule.ModuleDir
	public static File					permsFolder;

	protected static AbstractDataDriver	data;

	// permission registrations here...
	protected PermRegLoader				permLoader;
	private AutoPromoteManager			autoPromoteManager;

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		ZoneManager.manager = new ZoneHelper();
		PermissionsAPI.manager = new PermissionsHelper();

		MinecraftForge.EVENT_BUS.register(ZoneManager.manager);
		permLoader = new PermRegLoader(e.getCallableMap().getCallable(PermRegister.class));
		permLoader.loadAllPerms();
		permLoader.clearMethods();

		DataStorageManager.registerSaveableType(new ClassContainer(Zone.class));
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		// setup SQL
		sql = new SqlHelper(config);
		sql.putRegistrationPerms(permLoader.registerredPerms);

		PermissionsList list = new PermissionsList();
		if (list.shouldMake())
		{
			list.output(permLoader.perms);
		}

		DataStorageManager.registerSaveableType(Zone.class);
		DataStorageManager.registerSaveableType(AutoPromote.class);
		
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		// load zones...
		data = DataStorageManager.getReccomendedDriver();
		((ZoneHelper) ZoneManager.manager).loadZones();

		if (config.importBool)
		{
			sql.importPerms(config.importDir);
		}

		// init perms and vMC command overrides
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
		e.registerServerCommand(new CommandAutoPromote());
		OverrideManager.regOverrides((FMLServerStartingEvent) e.getFMLEvent());

		autoPromoteManager = new AutoPromoteManager();
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.perm", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.perm._ALL_", RegGroup.OWNERS, true);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.define", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.redefine", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.remove", RegGroup.OWNERS);
		event.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);
		event.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);

		event.registerPermissionLevel("ForgeEssentials.permissions.zone", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.setparent", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("ForgeEssentials.autoPromote", RegGroup.ZONE_ADMINS);

		event.registerPermissionLevel("ForgeEssentials.permissions.zone.info", RegGroup.MEMBERS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.list", RegGroup.MEMBERS);

		event.registerPermissionLevel("ForgeEssentials.BasicCommands.list", RegGroup.GUESTS);
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		// save all the zones
		ClassContainer con = new ClassContainer(Zone.class);
		for (Zone zone : ZoneManager.getZoneList())
		{
			if (zone == null || zone.isGlobalZone() || zone.isWorldZone())
			{
				continue;
			}
			data.saveObject(con, zone);
		}

		autoPromoteManager.stop();
	}
}
