package com.forgeessentials.permissions;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.autoPromote.AutoPromote;
import com.forgeessentials.permissions.autoPromote.AutoPromoteManager;
import com.forgeessentials.permissions.autoPromote.CommandAutoPromote;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandSetSpawn;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.permissions.core.PermissionEventHandler;
import com.forgeessentials.permissions.core.PermissionsListWriter;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.permissions.persistence.JsonProvider;
import com.forgeessentials.permissions.persistence.SQLProvider;
import com.forgeessentials.util.DBConnector;
import com.forgeessentials.util.EnumDBType;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, canDisable = false)
public class ModulePermissions extends ConfigLoaderBase {

    private static final String CONFIG_CAT = "Permissions";

    public static AutoPromoteManager autoPromoteManager;

    public static ZonedPermissionHelper permissionHelper;

    public static PermissionEventHandler permissionEventHandler;

    private String persistenceBackend = "flatfile";

    private DBConnector dbConnector = new DBConnector("Permissions", null, EnumDBType.H2_FILE, "ForgeEssentials", ForgeEssentials.getFEDirectory().getPath() + "/permissions",
            false);

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
        DataStorageManager.registerSaveableType(AutoPromote.class);

        // Register permission event-handler
        permissionEventHandler = new PermissionEventHandler();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverStarting(FEModuleServerInitEvent e)
    {
        // Backup FEData directory
        try
        {
            File path = new File(FunctionHelper.getWorldPath(), "FEData");
            File backupPath = new File(FunctionHelper.getWorldPath(), "FEData_backup");
            if (backupPath.exists())
                FileUtils.deleteDirectory(backupPath);
            FileUtils.copyDirectory(path, backupPath);
        }
        catch (IOException ex)
        {
            OutputHandler.felog.warning("Unable to create FEData backup");
        }
        
        // Load permissions
        switch (persistenceBackend.toLowerCase())
        {
        case "sql":
            permissionHelper.setPersistenceProvider(new SQLProvider(dbConnector.getChosenConnection(), dbConnector.getActiveType()));
            break;
        case "json":
        	permissionHelper.setPersistenceProvider(new JsonProvider(new File(FunctionHelper.getWorldPath(), "FEData/json")));
        	break;
        case "flatfile":
        default:
        {
            permissionHelper.setPersistenceProvider(new FlatfileProvider(new File(FunctionHelper.getWorldPath(), "FEData/permissions")));
            break;
        }
        }
        permissionHelper.load();

        // Register commands
        new CommandZone().register();
        new CommandPermissions().register();
        new CommandAutoPromote().register();
        new CommandSetSpawn().register();

        // Register permissions
        registerPermissions();

        // Load auto-promote manager
        autoPromoteManager = new AutoPromoteManager();
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        PermissionsListWriter.write(permissionHelper.getRegisteredPermissions(), new File(ForgeEssentials.getFEDirectory(), "PermissionsList.txt"));
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
        // Permission settings command
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
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_DEBUG, RegisteredPermValue.OP, "Allow using permission-debug command");

        // Other
        APIRegistry.perms.registerPermission("fe.perm.autoPromote", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission("fe.core.info", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(CommandSetSpawn.PERM_SETSPAWN, RegisteredPermValue.TRUE);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        persistenceBackend = config.get(CONFIG_CAT, "persistenceBackend", "flatfile", "Choose a permission persistence backend (flatfile, sql, json)").getString();

        dbConnector.loadOrGenerate(config, CONFIG_CAT + ".SQL");
    }

    public DBConnector getDbConnector()
    {
        return dbConnector;
    }

}
