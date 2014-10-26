package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.autoPromote.AutoPromote;
import com.forgeessentials.permissions.autoPromote.AutoPromoteManager;
import com.forgeessentials.permissions.autoPromote.CommandAutoPromote;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandTestPermission;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.permissions.core.ConfigPermissions;
import com.forgeessentials.permissions.core.PermissionEventHandler;
import com.forgeessentials.permissions.core.PermissionsListWriter;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.util.events.FEModuleEvent.*;
import com.forgeessentials.util.teleport.TeleportCenter;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.io.File;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions {

	@FEModule.Config
	public static ConfigPermissions config;
	
	@FEModule.ModuleDir
	public static File moduleFolder;

	public static AutoPromoteManager autoPromoteManager;

	public static ZonedPermissionHelper permissionHelper;

	public static PermissionEventHandler permissionEventHandler;

	@SubscribeEvent
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

	@SubscribeEvent
	public void load(FEModuleInitEvent e)
	{
		// Open database
		SqlHelper.getInstance();

		DataStorageManager.registerSaveableType(AutoPromote.class);

		MinecraftForge.EVENT_BUS.register(new PermissionEventHandler());
	}

	@SubscribeEvent
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

	@SubscribeEvent
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		new PermissionsListWriter().write(permissionHelper.getRegisteredPermissions());
		permissionHelper.save();
	}

	@SubscribeEvent
	public void serverStopping(FEModuleServerStopEvent e)
	{
		autoPromoteManager.stop();
		permissionHelper.save();
		// permissionHelper.clear();
	}

	private void registerPermissions()
	{
	    APIRegistry.perms.registerPermissionDescription(CommandZone.PERM_NODE, "Permission nodes for area-management command");
        APIRegistry.perms.registerPermission(CommandZone.PERM_ALL, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(CommandZone.PERM_LIST, RegisteredPermValue.TRUE);
		APIRegistry.perms.registerPermission(CommandZone.PERM_INFO, RegisteredPermValue.TRUE);

        APIRegistry.perms.registerPermissionDescription(PermissionCommandParser.PERM, "Permission nodes for permission-management command");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_ALL, RegisteredPermValue.OP);
        
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER, RegisteredPermValue.OP, "Allow modifying user permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_PERMS, RegisteredPermValue.OP, "Allow modifying user permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_SPAWN, RegisteredPermValue.OP, "Allow setting user spawn");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_FIX, RegisteredPermValue.OP, "Allow setting user prefix / suffix");
        
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP, RegisteredPermValue.OP, "Allow showing group info");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_PERMS, RegisteredPermValue.OP, "Allow modifying group permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_SPAWN, RegisteredPermValue.OP, "Allow setting group spawn");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_FIX, RegisteredPermValue.OP, "Allow setting group prefix / suffix");
        
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_USERS, RegisteredPermValue.OP, "Allow listing users");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_GROUPS, RegisteredPermValue.OP, "Allow listing groups");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_ZONES, RegisteredPermValue.OP, "Allow listing zones");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_PERMS, RegisteredPermValue.TRUE, "Allow listing all permissions affecting current user");
        
		APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_TEST, RegisteredPermValue.TRUE, "Allow testing permission nodes");
		
		APIRegistry.perms.registerPermission("fe.perm.autoPromote", RegisteredPermValue.OP);
		APIRegistry.perms.registerPermission("fe.core.info", RegisteredPermValue.OP);
		
		APIRegistry.perms.registerPermission(TeleportCenter.BYPASS_COOLDOWN, RegisteredPermValue.OP, "Allow bypassing teleport cooldown");
		APIRegistry.perms.registerPermission(TeleportCenter.BYPASS_WARMUP, RegisteredPermValue.OP, "Allow bypassing teleport warmup");
		// CommandSetChecker.regMCOverrides();
	}

}
