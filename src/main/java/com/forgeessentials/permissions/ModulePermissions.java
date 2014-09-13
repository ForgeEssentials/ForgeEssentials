package com.forgeessentials.permissions;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.autoPromote.AutoPromote;
import com.forgeessentials.permissions.autoPromote.AutoPromoteManager;
import com.forgeessentials.permissions.autoPromote.CommandAutoPromote;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandTestPermission;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.core.ConfigPermissions;
import com.forgeessentials.permissions.core.PermissionEventHandler;
import com.forgeessentials.permissions.core.PermissionsListWriter;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import com.forgeessentials.util.teleport.TeleportCenter;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions {

	@FEModule.Config
	public static ConfigPermissions config;
	
	@FEModule.ModuleDir
	public static File moduleFolder;

	public static AutoPromoteManager autoPromoteManager;

	public static ZonedPermissionHelper permissionHelper;

	public static PermissionEventHandler permissionEventHandler;

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);

		// Create permission manager
		permissionHelper = new ZonedPermissionHelper();
		permissionHelper.setPersistenceProvider(new FlatfileProvider(new File(moduleFolder, "flat")));
		
		// Register permission manager
		APIRegistry.perms = permissionHelper;
		PermissionsManager.setPermProvider(permissionHelper);
		
		// Register event handler
		permissionEventHandler = new PermissionEventHandler();
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		// Open database
		SqlHelper.getInstance();

		DataStorageManager.registerSaveableType(AutoPromote.class);

		MinecraftForge.EVENT_BUS.register(new PermissionEventHandler());
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		// Load permissions
		permissionHelper.load();

		// Register permissions
		registerPermissions();

		// Register commands
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandPermissions());
		e.registerServerCommand(new CommandTestPermission());
		e.registerServerCommand(new CommandAutoPromote());

		// Load auto-promote manager
		autoPromoteManager = new AutoPromoteManager();
	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		//new PermissionsListWriter().write(permissionHelper.enumAllPermissions());
		new PermissionsListWriter().write(permissionHelper.enumRegisteredPermissions());
		permissionHelper.save();
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		autoPromoteManager.stop();
		permissionHelper.save();
		permissionHelper.clear();
	}

	private void registerPermissions()
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
		PermissionsManager.registerPermission("fe.core.info", RegisteredPermValue.OP);
		// CommandSetChecker.regMCOverrides();
	}

}
