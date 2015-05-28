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
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandPromote;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.permissions.core.PermissionScheduler;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.permissions.persistence.JsonProvider;
import com.forgeessentials.permissions.persistence.SQLProvider;
import com.forgeessentials.util.DBConnector;
import com.forgeessentials.util.EnumDBType;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, canDisable = false)
public class ModulePermissions extends ConfigLoaderBase
{

    private static final String CONFIG_CAT = "Permissions";

    public static ZonedPermissionHelper permissionHelper;

    private String persistenceBackend = "flatfile";

    private DBConnector dbConnector = new DBConnector("Permissions", null, EnumDBType.H2_FILE, "ForgeEssentials", ForgeEssentials.getFEDirectory().getPath()
            + "/permissions", false);

    private PermissionScheduler permissionScheduler;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void preLoad(FEModulePreInitEvent e)
    {
        // Create permission manager
        permissionHelper = new ZonedPermissionHelper();
        permissionScheduler = new PermissionScheduler();
        APIRegistry.perms = permissionHelper;
        PermissionsManager.setPermProvider(permissionHelper);

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);

        FECommandManager.registerCommand(new CommandZone());
        FECommandManager.registerCommand(new CommandPermissions());
        FECommandManager.registerCommand(new CommandPromote());
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
        permissionScheduler.loadAll();
        registerPermissions();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        permissionHelper.save();
        // permissionHelper.verbosePermissionDebug = true;
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        // permissionHelper.verbosePermissionDebug = false;
        permissionHelper.disableAutoSave = false;
        permissionHelper.save();
        permissionScheduler.saveAll();
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
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_INCLUDES, "Groups that are included using the included group's priority");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_PARENTS, "Groups that are included using the including group's priority");
        APIRegistry.perms.registerPermission(FEPermissions.GROUP_PROMOTION, RegisteredPermValue.FALSE, "Unlock this group for promotion with /promote");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER, "Player information");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_GROUPS, "Comma separated list of player groups");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_NAME, "Player name");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_UUID, "Player UUID");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PREFIX, "Prefix property node");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.SUFFIX, "Suffix property node");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.ZONE_ENTRY_MESSAGE, "Zone entry message");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.ZONE_EXIT_MESSAGE, "Zone exit message");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.SPAWN_LOC, "Player spawn location property");
        APIRegistry.perms.registerPermission(FEPermissions.SPAWN_BED, RegisteredPermValue.TRUE, "Player spawn at bed if available");

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
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_RELOAD, RegisteredPermValue.OP, "Allow reloading changed permission files");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_SAVE, RegisteredPermValue.OP, "Allow force-saving permission files");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_DEBUG, RegisteredPermValue.TRUE, "Allow using permission-debug command");

        // Other
        APIRegistry.perms.registerPermission("fe.perm.autoPromote", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission("fe.core.info", RegisteredPermValue.OP);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        persistenceBackend = config.get(CONFIG_CAT, "persistenceBackend", "flatfile", "Choose a permission persistence backend (flatfile, sql, json)")
                .getString();

        dbConnector.loadOrGenerate(config, CONFIG_CAT + ".SQL");
    }

    public DBConnector getDbConnector()
    {
        return dbConnector;
    }

}
