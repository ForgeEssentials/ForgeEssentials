package com.forgeessentials.permissions;

import java.io.File;
import java.io.IOException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.permissions.commands.CommandItemPermission;
import com.forgeessentials.permissions.commands.CommandPermissions;
import com.forgeessentials.permissions.commands.CommandPromote;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.permissions.core.ItemPermissionManager;
import com.forgeessentials.permissions.core.PermissionScheduler;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.permissions.persistence.JsonProvider;
import com.forgeessentials.permissions.persistence.SQLProvider;
import com.forgeessentials.permissions.persistence.SingleFileProvider;
import com.forgeessentials.util.DBConnector;
import com.forgeessentials.util.EnumDBType;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, canDisable = false)
public class ModulePermissions extends ConfigLoaderBase
{
    private static ForgeConfigSpec PERMISSIONS_CONFIG;
	public static final ConfigData data = new ConfigData("Permissions", PERMISSIONS_CONFIG, new ForgeConfigSpec.Builder());
	
    public static final String CONFIG_CAT = "Permissions";

    private static final String PERSISTENCE_HELP = "Choose a permission persistence backend (flatfile, sql, json, singlejson). DO NOT use SQL, unless you really need to use it.";

    public static ZonedPermissionHelper permissionHelper;

    private static String persistenceBackend = "flatfile";

    private DBConnector dbConnector = new DBConnector("Permissions", null, EnumDBType.H2_FILE, "ForgeEssentials", ForgeEssentials.getFEDirectory().getPath()
            + "/permissions", false);

    private static PermissionScheduler permissionScheduler;

    private static ItemPermissionManager itemPermissionManager;

    public static boolean fakePlayerIsSpecialBunny = true;

    public ModulePermissions()
    {
        // Earliest initialization of permission system possible
        //permissionHelper = new ZonedPermissionHelper();
        //APIRegistry.perms = permissionHelper;
        //PermissionAPI.setPermissionHandler(permissionHelper);

        if (ModList.get().isLoaded("ftblib"))
        {
            try
            {
                // MinecraftForge.EVENT_BUS.register(FTBURankConfigHandler.class);
                LoggingHandler.felog.debug("Rank Handler for FTBLib has been registered");
            }
            catch (NoClassDefFoundError e)
            {
                LoggingHandler.felog.error("FTBU is installed but an error was encountered while loading the compat!");
            }
        } else {
            LoggingHandler.felog.debug("FTBLib is not loaded!");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void preLoad(FEModuleCommonSetupEvent e)
    {
        itemPermissionManager = new ItemPermissionManager();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandZone(true), dispatcher);
        FECommandManager.registerCommand(new CommandPermissions(true), dispatcher);
        FECommandManager.registerCommand(new CommandPromote(true), dispatcher);
        FECommandManager.registerCommand(new CommandItemPermission(true), dispatcher);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        permissionScheduler = new PermissionScheduler();
        // Backup FEData directory
        try
        {
            File path = new File(ServerUtil.getWorldPath(), "FEData");
            File backupPath = new File(ServerUtil.getWorldPath(), "FEData_backup");
            if (backupPath.exists())
                FileUtils.deleteDirectory(backupPath);
            FileUtils.copyDirectory(path, backupPath);
        }
        catch (IOException ex)
        {
            LoggingHandler.felog.warn("Unable to create FEData backup");
        }

        // Load permissions
        switch (persistenceBackend.toLowerCase())
        {
        case "sql":
            permissionHelper.setPersistenceProvider(new SQLProvider(dbConnector.getChosenConnection(), dbConnector.getActiveType()));
            break;
        case "json":
            permissionHelper.setPersistenceProvider(new JsonProvider());
            break;
        case "flatfile":
            permissionHelper.setPersistenceProvider(new FlatfileProvider());
            break;
        case "singlejson":
        default:
            permissionHelper.setPersistenceProvider(new SingleFileProvider());
            break;
        }
        permissionHelper.load();
        permissionScheduler.loadAll();
        registerPermissions();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverStarted(FEModuleServerStartedEvent e)
    {
        permissionHelper.save();
        // permissionHelper.verbosePermissionDebug = true;
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e)
    {
        // permissionHelper.verbosePermissionDebug = false;
        permissionHelper.disableAutoSave = false;
        permissionHelper.save();
        permissionHelper.clear();
        permissionScheduler.saveAll();
    }

    private static void registerPermissions()
    {
        // Permission settings command
        APIRegistry.perms.registerPermissionDescription(FEPermissions.FE_INTERNAL,
                "Internal permissions - DO NOT TOUCH THESE UNLESS YOU KNOW WHAT YOU DO (WHICH YOU DON'T!)");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP,
                "(optional) Permission to keep groups saved, even if they have no permissions set.");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_NAME, "Group name for display purposes");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_PRIORITY, "Group priority");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_INCLUDES, "Groups that are included using the included group's priority");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.GROUP_PARENTS, "Groups that are included using the including group's priority");
        APIRegistry.perms.registerPermission(FEPermissions.GROUP_PROMOTION, DefaultPermissionLevel.NONE, "Unlock this group for promotion with /promote");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER, "Player information");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_GROUPS, "Comma separated list of player groups");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_NAME, "Player name");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PLAYER_UUID, "Player UUID");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.PREFIX, "Prefix property node");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.SUFFIX, "Suffix property node");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.ZONE_ENTRY_MESSAGE, "Zone entry message");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.ZONE_EXIT_MESSAGE, "Zone exit message");
        APIRegistry.perms.registerPermissionDescription(FEPermissions.SPAWN_LOC, "Player spawn location property");
        APIRegistry.perms.registerPermission(FEPermissions.SPAWN_BED, DefaultPermissionLevel.ALL, "Player spawn at bed if available");

        APIRegistry.perms.registerPermissionDescription(CommandZone.PERM_NODE, "Permission nodes for area-management command");
        APIRegistry.perms.registerPermission(CommandZone.PERM_ALL, DefaultPermissionLevel.OP, "Grants access to ALL area management commands. Use with caution.");
        APIRegistry.perms.registerPermission(CommandZone.PERM_LIST, DefaultPermissionLevel.ALL, "List all zones");
        APIRegistry.perms.registerPermission(CommandZone.PERM_INFO, DefaultPermissionLevel.ALL, "Get info of a zone");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM, DefaultPermissionLevel.ALL, "Basic usage of permission-management command");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_ALL, DefaultPermissionLevel.OP, "Grants access to ALL permission management commands. Use with caution.");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER, DefaultPermissionLevel.OP, "Allow basic access to users (displays infos)");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_PERMS, DefaultPermissionLevel.OP, "Allow modifying user permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_SPAWN, DefaultPermissionLevel.OP, "Allow setting user spawn");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_USER_FIX, DefaultPermissionLevel.OP, "Allow setting user prefix / suffix");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP, DefaultPermissionLevel.OP, "Allow basic access to groups (displays infos)");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_PERMS, DefaultPermissionLevel.OP, "Allow modifying group permissions");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_SPAWN, DefaultPermissionLevel.OP, "Allow setting group spawn");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_GROUP_FIX, DefaultPermissionLevel.OP, "Allow setting group prefix / suffix");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_USERS, DefaultPermissionLevel.OP, "Allow listing users");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_GROUPS, DefaultPermissionLevel.OP, "Allow listing groups");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_ZONES, DefaultPermissionLevel.OP, "Allow listing zones");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_LIST_PERMS, DefaultPermissionLevel.ALL,
                "Allow listing all permissions affecting current user");

        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_TEST, DefaultPermissionLevel.ALL, "Allow testing permission nodes");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_RELOAD, DefaultPermissionLevel.OP, "Allow reloading changed permission files");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_SAVE, DefaultPermissionLevel.OP, "Allow force-saving permission files");
        APIRegistry.perms.registerPermission(PermissionCommandParser.PERM_DEBUG, DefaultPermissionLevel.ALL, "Allow using permission-debug command");

        // Other
        APIRegistry.perms.registerPermission("fe.perm.autoPromote", DefaultPermissionLevel.OP, "Auto-promote a user after some time has passed");
        APIRegistry.perms.registerPermission("fe.core.info", DefaultPermissionLevel.OP, "Access FE's /feinfo command");
    }
    
    static ForgeConfigSpec.ConfigValue<String> FEpersistenceBackend;
    static ForgeConfigSpec.BooleanValue FEfakePlayerIsSpecialBunny;
    
	@Override
	public void load(Builder BUILDER, boolean isReload)
    {
    	BUILDER.push(CONFIG_CAT);
    	FEpersistenceBackend = BUILDER.comment(PERSISTENCE_HELP).define("persistenceBackend", "singlejson");
    	FEfakePlayerIsSpecialBunny = BUILDER.comment("Should we force override UUID for fake players? This is by default true because mods are randomly generating UUID each boot!").define("fakePlayerIsSpecialBunny", true);
    	BUILDER.pop();
    	dbConnector.loadOrGenerate(BUILDER, CONFIG_CAT + ".SQL");
    }

	@Override
	public void bakeConfig(boolean reload)
	{
		persistenceBackend = FEpersistenceBackend.get();
		fakePlayerIsSpecialBunny = FEfakePlayerIsSpecialBunny.get();
        dbConnector.bakeConfig(reload);
	}

	@Override
	public ConfigData returnData() {
		return data;
	}

	
    public DBConnector getDbConnector()
    {
        return dbConnector;
    }

	public static ItemPermissionManager getItemPermissionManager() {
		return itemPermissionManager;
	}
	public static PermissionScheduler getPermissionScheduler(){
        return permissionScheduler;
    }
}
