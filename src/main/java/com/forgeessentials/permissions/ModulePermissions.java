package com.forgeessentials.permissions;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.autoPromote.AutoPromote;
import com.forgeessentials.permissions.autoPromote.AutoPromoteManager;
import com.forgeessentials.permissions.autoPromote.CommandAutoPromote;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.permissions.core.PermissionEventHandler;
import com.forgeessentials.permissions.core.PermissionsListWriter;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.teleport.TeleportCenter;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class)
public class ModulePermissions {

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

        // Register permission manager
        APIRegistry.perms = permissionHelper;
        PermissionsManager.setPermProvider(permissionHelper);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        // Open database
        // SqlHelper.getInstance();

        DataStorageManager.registerSaveableType(AutoPromote.class);

        // Register permission event-handler
        permissionEventHandler = new PermissionEventHandler();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        // Load permissions
        File permPath = new File(FunctionHelper.getWorldPath(), "FEPermissions");
        {
            File oldPermPath = new File(moduleFolder, "flat");
            if (oldPermPath.exists() && !permPath.exists())
            {
                try
                {
                    FileUtils.moveDirectory(oldPermPath, permPath);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        permissionHelper.setPersistenceProvider(new FlatfileProvider(permPath));
        permissionHelper.load();

        // Register commands
        FunctionHelper.registerServerCommand(new CommandZone());
        FunctionHelper.registerServerCommand(new CommandPermissions());
        FunctionHelper.registerServerCommand(new CommandAutoPromote());

        // Register permissions
        registerPermissions();

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

    private static void registerPermissions()
    {
        APIRegistry.perms.registerPermissionDescription(FEPermissions.FE_INTERNAL,
                "Internal permissions - DO NOT TOUCH THESE UNLESS YOU KNOW WHAT YOU DO (WHICH YOU DON'T!)");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP,
                "(optional) Permission to keep groups saved, even if they have no permissions set.");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_ID, "Group ID");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_PRIORITY, "Group priority");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER, "Player information");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_GROUPS, "Comma separated list of player groups");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_NAME, "Player name");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_UUID, "Player UUID");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PREFIX, "Prefix property node");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.SUFFIX, "Suffix property node");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.ZONE_ENTRY_MESSAGE, "Zone entry message");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.ZONE_EXIT_MESSAGE, "Zone exit message");
        APIRegistry.perms.registerPermissionProperty(FEPermissions.SPAWN, "bed", "Player spawn property");

        APIRegistry.perms.registerPermissionDescription(CommandZone.PERM_NODE, "Permission nodes for area-management command");
        APIRegistry.perms.registerPermission(CommandZone.PERM_ALL, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(CommandZone.PERM_LIST, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(CommandZone.PERM_INFO, RegisteredPermValue.TRUE);

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM, RegisteredPermValue.TRUE, "Basic usage of permission-management command");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_ALL, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER, RegisteredPermValue.OP, "Allow basic access to users (displays infos)");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_PERMS, RegisteredPermValue.OP, "Allow modifying user permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_SPAWN, RegisteredPermValue.OP, "Allow setting user spawn");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_FIX, RegisteredPermValue.OP, "Allow setting user prefix / suffix");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP, RegisteredPermValue.OP, "Allow basic access to groups (displays infos)");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_PERMS, RegisteredPermValue.OP, "Allow modifying group permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_SPAWN, RegisteredPermValue.OP, "Allow setting group spawn");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_FIX, RegisteredPermValue.OP, "Allow setting group prefix / suffix");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_USERS, RegisteredPermValue.OP, "Allow listing users");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_GROUPS, RegisteredPermValue.OP, "Allow listing groups");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_ZONES, RegisteredPermValue.OP, "Allow listing zones");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_PERMS, RegisteredPermValue.TRUE,
                "Allow listing all permissions affecting current user");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_TEST, RegisteredPermValue.TRUE, "Allow testing permission nodes");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_RELOAD, RegisteredPermValue.TRUE, "Allow reloading changed permission files");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_SAVE, RegisteredPermValue.TRUE, "Allow force-saving permission files");

        APIRegistry.perms.registerPermission("fe.perm.autoPromote", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission("fe.core.info", RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(TeleportCenter.BYPASS_COOLDOWN, RegisteredPermValue.OP, "Allow bypassing teleport cooldown");
        APIRegistry.perms.registerPermission(TeleportCenter.BYPASS_WARMUP, RegisteredPermValue.OP, "Allow bypassing teleport warmup");
    }

}
