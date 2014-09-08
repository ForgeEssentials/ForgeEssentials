package com.forgeessentials.permissions;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.autoPromote.AutoPromote;
import com.forgeessentials.permissions.autoPromote.AutoPromoteManager;
import com.forgeessentials.permissions.autoPromote.CommandAutoPromote;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandTestPermission;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.core.ZonedPermissionManager;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions {

	@FEModule.Config
	public static ConfigPermissions config;

	@FEModule.ModuleDir
	public static File permsFolder;

	private AutoPromoteManager autoPromoteManager;

	public static void regPerms()
	{
		PermissionsManager.registerPermission("fe.perm", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.perm.*", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.perm.zone.define", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.perm.zone.redefine.*", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.perm.zone.remove.*", RegisteredPermValue.OP);
		PermissionsManager.registerPermission(TeleportCenter.BYPASS_COOLDOWN, RegisteredPermValue.OP);
		PermissionsManager.registerPermission(TeleportCenter.BYPASS_COOLDOWN, RegisteredPermValue.OP);

		PermissionsManager.registerPermission("fe.perm.zone", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.perm.zone.setparent", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.perm.autoPromote", RegisteredPermValue.OP);

		PermissionsManager.registerPermission("fe.perm.zone.info.*", RegisteredPermValue.TRUE);
		PermissionsManager.registerPermission("fe.perm.zone.list", RegisteredPermValue.TRUE);

		PermissionsManager.registerPermission("fe.perm.list", RegisteredPermValue.TRUE);

		//CommandSetChecker.regMCOverrides();
		PermissionsManager.registerPermission("fe.core.info", RegisteredPermValue.OP);
	}

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);

		//DataStorageManager.registerSaveableType(new ClassContainer(Zone.class));
		
		APIRegistry.permissionManager = new ZonedPermissionManager();
		PermissionsManager.setPermProvider(APIRegistry.permissionManager);
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		// Open database
		SqlHelper.getInstance();
		
		DataStorageManager.registerSaveableType(Zone.class);
		DataStorageManager.registerSaveableType(AutoPromote.class);

		MinecraftForge.EVENT_BUS.register(new PermsEventHandler());
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		// load zones...
		//((ZoneHelper) APIRegistry.permissionManager).loadZones();

//		if (config.importBool)
//		{
//			sql.importPerms(config.importDir);
//		}

		// init perms and vMC command overrides
		e.registerServerCommand(new CommandZone());

		e.registerServerCommand(new CommandPermissions());
		e.registerServerCommand(new CommandTestPermission());
		e.registerServerCommand(new CommandAutoPromote());

		autoPromoteManager = new AutoPromoteManager();

		regPerms();

	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		// TODO: PERMS
		//sql.putRegistrationPerms(APIRegistry.permissionManager.getRegisteredPerms());
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		autoPromoteManager.stop();
		
		// TODO: PERMS
		
		// save all the zones
//		for (Zone zone : APIRegistry.permissionManager.getZoneList())
//		{
//			if (zone == null || zone.isGlobalZone() || zone.isWorldZone())
//			{
//				continue;
//			}
//			DataStorageManager.getReccomendedDriver().saveObject(ZoneHelper.container, zone);
//		}
	}
}
