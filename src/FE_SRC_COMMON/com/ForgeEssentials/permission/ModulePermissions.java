package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.data.AbstractDataDriver;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.permission.autoPromote.AutoPromote;
import com.ForgeEssentials.permission.autoPromote.AutoPromoteManager;
import com.ForgeEssentials.permission.autoPromote.CommandAutoPromote;
import com.ForgeEssentials.permission.mcoverride.OverrideManager;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePostInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerStopEvent;

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

		DataStorageManager.registerSaveableType(new ClassContainer(Zone.class));
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		// setup SQL
		sql = new SqlHelper(config);

		DataStorageManager.registerSaveableType(Zone.class);
		DataStorageManager.registerSaveableType(AutoPromote.class);

		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@FEModule.PostInit
	public void postload(FEModulePostInitEvent e)
	{
		permLoader.loadAllPerms();
		permLoader.clearMethods();
		sql.putRegistrationPerms(permLoader.registerredPerms);

		PermissionsList list = new PermissionsList();
		if (list.shouldMake())
		{
			list.output(permLoader.perms);
		}
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
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.redefine._ALL_", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.remove._ALL_", RegGroup.OWNERS);
		event.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);
		event.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);

		event.registerPermissionLevel("ForgeEssentials.permissions.zone", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.setparent", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("ForgeEssentials.autoPromote", RegGroup.ZONE_ADMINS);

		event.registerPermissionLevel("ForgeEssentials.permissions.zone.info._ALL_", RegGroup.MEMBERS);
		event.registerPermissionLevel("ForgeEssentials.permissions.zone.list", RegGroup.MEMBERS);

		event.registerPermissionLevel("ForgeEssentials.BasicCommands.list", RegGroup.GUESTS);
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		// save all the zones
		for (Zone zone : ZoneManager.getZoneList())
		{
			if (zone == null || zone.isGlobalZone() || zone.isWorldZone())
			{
				continue;
			}
			data.saveObject(ZoneHelper.container, zone);
		}

		autoPromoteManager.stop();
	}
}
