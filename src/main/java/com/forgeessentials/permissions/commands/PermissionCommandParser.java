package com.forgeessentials.permissions.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.permissions.persistence.FlatfileProvider;
import com.forgeessentials.permissions.persistence.JsonProvider;
import com.forgeessentials.permissions.persistence.SingleFileProvider;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraft.server.level.ServerLevel;

public class PermissionCommandParser extends CommandUtils
{

    public static final String PERM = "fe.perm";
    public static final String PERM_ALL = PERM + Zone.ALL_PERMS;
    public static final String PERM_TEST = PERM + ".test";
    public static final String PERM_RELOAD = PERM + ".reload";
    public static final String PERM_SAVE = PERM + ".save";
    public static final String PERM_DEBUG = PERM + ".debug";

    public static final String PERM_USER = PERM + ".user";
    public static final String PERM_USER_PERMS = PERM_USER + ".perms";
    public static final String PERM_USER_SPAWN = PERM_USER + ".spawn";
    public static final String PERM_USER_FIX = PERM_USER + ".fix";

    public static final String PERM_GROUP = PERM + ".group";
    public static final String PERM_GROUP_PERMS = PERM_GROUP + ".perms";
    public static final String PERM_GROUP_SPAWN = PERM_GROUP + ".spawn";
    public static final String PERM_GROUP_FIX = PERM_GROUP + ".fix";

    private static final String PERM_LIST = PERM + ".list";
    public static final String PERM_LIST_PERMS = PERM_LIST + ".perms";
    public static final String PERM_LIST_ZONES = PERM_LIST + ".zones";
    public static final String PERM_LIST_USERS = PERM_LIST + ".users";
    public static final String PERM_LIST_GROUPS = PERM_LIST + ".groups";

    enum PermissionAction
    {
        ALLOW, DENY, CLEAR, VALUE
    }

    // Variables for auto-complete
    public static final String[] parseMainArgs = { "user", "group", "global", "list", "test", "reload", "save",
            "debug" }; // "export",
                       // "promote",
    public static final String[] parseListArgs = { "zones", "perms", "users", "groups", "worlds" };
    public static final String[] parseUserArgs = { "zone", "group", "allow", "deny", "clear", "value", "spawn",
            "prefix", "suffix", "perms", "denydefault" };
    public static final String[] parseGroupArgs = { "zone", "users", "allow", "deny", "clear", "value", "spawn",
            "prefix", "suffix", "perms", "denydefault", "priority", "parent", "include", "create" };
    public static final String[] parseUserGroupArgs = { "add", "remove", "set" };
    public static final String[] parseGroupIncludeArgs = { "add", "remove", "clear" };
    public static final String[] parseSpawnArgs = { "here", "clear", "bed" };

    public static void parseMain(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/feperm " + StringUtils.join(parseMainArgs, "|") + ": Displays help for the subcommands");
            return;
        }
        switch (params.remove(0).toLowerCase())
        {
        case "save":// p
            parseSave(ctx, params);
            break;
        case "reload":
            if (ModulePermissions.permissionHelper.load())
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Successfully reloaded permissions");
            else
                ChatOutputHandler.chatError(ctx.getSource(), "Error while reloading permissions");
            break;
        case "test":
            parseTest(ctx, params);
            break;
        case "list":
            parseList(ctx, params);
            break;
        case "user":
            parseUser(ctx, params);
            break;
        case "group":
            parseGroup(ctx, params);
            break;
        case "global":
            parseGlobal(ctx, params);
            break;
        case "debug":
            if (getServerPlayer(ctx.getSource()) == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
                return;
            }
            if (ModulePermissions.permissionHelper.permissionDebugUsers.contains(getServerPlayer(ctx.getSource())))
            {
                ModulePermissions.permissionHelper.permissionDebugUsers.remove(getServerPlayer(ctx.getSource()));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permission debug mode off");
            }
            else
            {
                ModulePermissions.permissionHelper.permissionDebugUsers.add(getServerPlayer(ctx.getSource()));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permission debug mode on");
            }
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), "Unknown command argument");
            break;
        }
    }

    public static void parseSave(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ModulePermissions.permissionHelper.setDirty(false);
            ModulePermissions.permissionHelper.save();
            for (ServerPlayer serverplayerentity : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
            {
                ServerLifecycleHooks.getCurrentServer().getCommands().sendCommands(serverplayerentity);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permissions saved!");
        }
        else
        {
            String action = params.remove(0).toLowerCase();
            switch (action)
            {
            case "enable":
                ModulePermissions.permissionHelper.disableAutoSave = false;
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permission saving enabled");
                break;
            case "disable":
                ModulePermissions.permissionHelper.disableAutoSave = true;
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permission saving disabled");
                break;
            case "flatfile":
                new FlatfileProvider().save(APIRegistry.perms.getServerZone());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permissions saved to flatfile format");
                break;
            case "singlejson":
                new SingleFileProvider().save(APIRegistry.perms.getServerZone());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permissions saved to single-json format");
                break;
            case "json":
                new JsonProvider().save(APIRegistry.perms.getServerZone());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Permissions saved to json format");
                break;
            default:
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, action);
                return;
            }
        }
    }

    // ------------------------------------------------------------
    // -- Listings
    // ------------------------------------------------------------

    public static void parseList(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/feperm list " + StringUtils.join(parseListArgs, "|") + " : List the specified objects");
        }
        else
        {
            String arg = params.remove(0).toLowerCase();
            switch (arg)
            {
            case "zones":
                if (getServerPlayer(ctx.getSource()) == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
                    return;
                }
                listZones(ctx.getSource(), new WorldPoint(getServerPlayer(ctx.getSource())));
                break;
            case "worlds":
                listWorlds(ctx.getSource());
                break;
            case "perms":
                if (getServerPlayer(ctx.getSource()) == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
                    return;
                }
                listUserPermissions(ctx.getSource(), UserIdent.get(getServerPlayer(ctx.getSource())), true);
                break;
            case "users":
                listUsers(ctx.getSource());
                break;
            case "groups":
                listGroups(ctx.getSource());
                break;
            default:
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, arg);
                return;
            }
        }
    }

    public static void parseTest(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Missing permission argument!");
            return;
        }

        UserIdent ident = getIdent(ctx.getSource());
        if (CommandUtils.GetSource(ctx.getSource()) instanceof DoAsCommandSender)
            ident = ((DoAsCommandSender) CommandUtils.GetSource(ctx.getSource())).getUserIdent();

        String permissionNode = Zone.fixPerms(params.remove(0));
        String result = APIRegistry.perms.getUserPermissionProperty(ident, permissionNode);
        if (result == null)
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), permissionNode + " = \u00a7etrue (not set)");
        }
        else if (Zone.PERMISSION_FALSE.equalsIgnoreCase(result))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), permissionNode + " = \u00a7c" + result);
        }
        else
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), permissionNode + " = " + result);
        }
    }

    // ------------------------------------------------------------
    // -- User
    // ------------------------------------------------------------

    public static void parseUser(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Possible usage:");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> : Display user info");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> perms : List player's permissions");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> suffix|prefix clear|<value> : Set player's titles");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> zone <zone> ... : Work with zones");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> zone <zone> group add|remove <group>: Player's group settings");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> zone <zone> allow|deny|clear <perms> : Set permissions");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> zone <zone> value <perm> : View permission property");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p user <player> zone <zone> spawn  (bed enable|disable)|here|clear|(pos <pos>): Set player spawn");
            return;
        }

        // Parse player
        UserIdent ident;
        try
        {
            ident = parsePlayer(params.remove(0), false, false);
        }
        catch (FECommandParsingException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), e.error);
            return;
        }
        if (!ident.hasUuid())
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Player %s not found. playername will be used, but may be inaccurate.", ident.getUsername());
        else if (!ident.hasUsername())
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Player uuid %s not found. uuid will be used, but may be inaccurate.", ident.getUuid());

        parseUserInner(ctx, params, ident, null);
    }

    public static void parseUserInner(CommandContext<CommandSourceStack> ctx, List<String> params, UserIdent ident,
            Zone zone) throws CommandRuntimeException
    {
        // Display help or player info
        if (params.isEmpty())
        {
            if (zone == null)
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Groups for player %s:", ident.getUsernameOrUuid());
                for (GroupEntry group : APIRegistry.perms.getPlayerGroups(ident))
                {
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + group);
                }
                return;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    ident.getUsernameOrUuid() + "'s permissions in zone " + zone.getName() + ":");
            for (Entry<String, String> perm : zone.getPlayerPermissions(ident).entrySet())
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "  " + perm.getKey() + " = " + (perm.getValue() == null ? "null" : perm.getValue()));
            }
            return;
        }

        String cmd = params.remove(0).toLowerCase();

        if (cmd.equals("zone"))
        {
            if (zone != null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Zone already set!");
                return;
            }
            if (params.isEmpty())
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Expected zone identifier.");
                return;
            }
            zone = parseZone(ctx, params);
            if (zone == null)
                return;
            params.remove(0);
            parseUserInner(ctx, params, ident, zone);
            return;
        }

        // Set default zone
        if (zone == null)
        {
            zone = APIRegistry.perms.getServerZone();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Using Main Zone");
        }
        // Catch case where zone is defined
        if (cmd.equals("zone"))
        {
            cmd = params.remove(0).toLowerCase();
        }

        // Parse command
        switch (cmd)
        {
        case "group":
            parseUserGroup(ctx, params, ident, zone);
            break;
        case "perms":
            listUserPermissions(ctx.getSource(), ident, true);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    ident.getUsernameOrUuid() + "'s permissions in zone " + zone.getName() + ":");
            for (Entry<String, String> perm : zone.getPlayerPermissions(ident).entrySet())
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "  " + perm.getKey() + " = " + (perm.getValue() == null ? "null" : perm.getValue()));
            }
            break;
        case "prefix":
            parseUserPrefixSuffix(ctx, params, ident, zone, false);
            break;
        case "suffix":
            parseUserPrefixSuffix(ctx, params, ident, zone, true);
            break;
        case "spawn":
            parseUserSpawn(ctx, params, ident, zone);
            break;
        case "allow":
            parseUserPermissions(ctx, params, ident, zone, PermissionAction.ALLOW);
            break;
        case "deny":
            parseUserPermissions(ctx, params, ident, zone, PermissionAction.DENY);
            break;
        case "clear":
            parseUserPermissions(ctx, params, ident, zone, PermissionAction.CLEAR);
            break;
        case "value":
            parseUserPermissions(ctx, params, ident, zone, PermissionAction.VALUE);
            break;
        case "denydefault":
            denyDefault(zone.getPlayerPermissions(ident));
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_INVALID_SYNTAX+""+ cmd+""+ params.toString());
            return;
        }
    }

    public static void parseUserFormat(CommandContext<CommandSourceStack> ctx, List<String> params, UserIdent ident,
            Zone zone, boolean isPlayer) throws CommandRuntimeException
    {
        // String fixPerm = "fe.chat." + (isPlayer ? "playerformat" : "textformat");
        // TODO: Implement Player / Text Format as an option in the command
        // EX: /p user [] format [player|text] [format values]
    }

    public static void parseUserPrefixSuffix(CommandContext<CommandSourceStack> ctx, List<String> params, UserIdent ident,
            Zone zone, boolean isSuffix) throws CommandRuntimeException
    {
        String fixName = isSuffix ? "suffix" : "prefix";
        if (params.isEmpty())
        {
            String fix = zone.getPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (fix == null || fix.isEmpty())
                fix = "empty";
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s's %s is %s", ident.getUsernameOrUuid(), fixName,
                    fix);
        }
        else
        {
            String fix = StringUtils.join(params, " ");
            if (fix.equalsIgnoreCase("clear"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s's %s cleared", ident.getUsernameOrUuid(),
                        fixName);
                zone.clearPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            }
            else
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s's %s set to %s", ident.getUsernameOrUuid(),
                        fixName, fix);
                zone.setPlayerPermissionProperty(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
            }
        }
    }

    public static void parseUserPermissions(CommandContext<CommandSourceStack> ctx, List<String> params, UserIdent ident,
            Zone zone, PermissionAction type) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Missing permission argument!");
            return;
        }

        // Apply permissions
        // while (!arguments.args.isEmpty())
        {
            String permissionNode = Zone.fixPerms(params.remove(0));
            String msg = null, value = null;
            if (type == PermissionAction.VALUE)
            {
                if (params.isEmpty())
                {
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Value of %s = %s", permissionNode,
                            zone.getPlayerPermission(ident, permissionNode));
                    return;
                }
                value = StringUtils.join(params, ' ');
                params.clear();
            }

            switch (type)
            {
            case ALLOW:
                zone.setPlayerPermission(ident, permissionNode, true);
                msg = "Allowed %s access to %s in zone %s";
                break;
            case DENY:
                zone.setPlayerPermission(ident, permissionNode, false);
                msg = "Denied %s access to %s in zone %s";
                break;
            case CLEAR:
                zone.clearPlayerPermission(ident, permissionNode);
                msg = "Cleared %s's access to %s in zone %s";
                break;
            case VALUE:
                zone.setPlayerPermissionProperty(ident, permissionNode, value);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set %s for %s to %s in zone %s", permissionNode,
                        ident.getUsernameOrUuid(), value, zone.getName());
                break;
            }
            if (msg != null)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), msg, ident.getUsernameOrUuid(), permissionNode,
                        zone.getName());
        }
    }

    public static void parseUserSpawn(CommandContext<CommandSourceStack> ctx, List<String> params, UserIdent ident,
            Zone zone) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feperm user " + ident.getUsernameOrUuid()
            + " zone "+zone.getName() + " spawn here|clear|<x> <y> <z> <dim>: Set spawn location");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feperm user " + ident.getUsernameOrUuid()
            + " zone "+zone.getName() + " spawn bed (enable|disable): Enable/disable spawning at bed");
            return;
        }

        String loc = params.remove(0).toLowerCase();
        WarpPoint point = null;
        switch (loc)
        {
        case "here":
            if (getServerPlayer(ctx.getSource()) == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "[here] cannot be used from console.");
                return;
            }
            point = new WarpPoint(getServerPlayer(ctx.getSource()));
            break;
        case "bed":
        {
            if (params.isEmpty())
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
                return;
            }
            String val = params.remove(0).toLowerCase();
            if (val.equals("true") | val.equals("enable"))
            {
                zone.setPlayerPermission(ident, FEPermissions.SPAWN_BED, true);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Enabled bed-spawning for user %s in zone %s",
                        ident.getUsernameOrUuid(), zone.getName());
            }
            else if (val.equals("false") | val.equals("disable"))
            {
                zone.setPlayerPermission(ident, FEPermissions.SPAWN_BED, false);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Disabled bed-spawning for user %s in zone %s",
                        ident.getUsernameOrUuid(), zone.getName());
            }
            else
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid argument. Use enable or disable.");
            return;
        }
        case "clear":
            point = null;
            break;
        default:
            if (params.size() < 3)
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
                return;
            }
            try
            {
                int x = CommandUtils.parseInt(loc);
                int y = CommandUtils.parseInt(params.remove(0));
                int z = CommandUtils.parseInt(params.remove(0));
                String dimension = params.remove(0);
                point = new WarpPoint(dimension, x, y, z, 0, 0);
            }
            catch (NumberFormatException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid location argument");
                return;
            }
            break;
        }

        if (point == null)
        {
            zone.clearPlayerPermission(ident, FEPermissions.SPAWN_LOC);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared spawn-rule for user %s in zone %s",
                    ident.getUsernameOrUuid(), zone.getName());
        }
        else
        {
            zone.setPlayerPermissionProperty(ident, FEPermissions.SPAWN_LOC, point.toString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set spawn for user %s to %s in zone %s",
                    ident.getUsernameOrUuid(), point.toString(), zone.getName());
        }
    }

    public static void parseUserGroup(CommandContext<CommandSourceStack> ctx, List<String> params, UserIdent ident,
            Zone zone)
    {
        if (params.isEmpty())
        {
            if (zone instanceof ServerZone)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Groups for player %s (without includes):",
                        ident.getUsernameOrUuid());
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Groups for player %s (without includes) in %s:",
                        ident.getUsernameOrUuid(), zone.getName());
            for (GroupEntry g : zone.getStoredPlayerGroupEntries(ident))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + g);
            }
        }
        else
        {
            String mode = params.remove(0).toLowerCase();
            if (!mode.equals("add") && !mode.equals("remove") && !mode.equals("set"))
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Syntax error. Please try this instead:");
                ChatOutputHandler.chatError(ctx.getSource(), "/p user <player> zone <zone> group add|set|remove <GROUP>");
                return;
            }

            if (params.isEmpty())
            {
                ChatOutputHandler.chatError(ctx.getSource(),
                        "Usage: /p user " + ident.getUsername() + " zone "+zone.getName()+" group " + mode + " <group-name>");
            }
            else
            {
                String[] groups = params.remove(0).split(",");
                for (String group : groups)
                    if (!APIRegistry.perms.groupExists(group))
                    {
                        ChatOutputHandler.chatError(ctx.getSource(), "Group %s not found.", group);
                        return;
                    }

                switch (mode)
                {
                case "add":
                    for (String group : groups)
                        zone.addPlayerToGroup(ident, group);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Player %s added to group(s) %s",
                            ident.getUsernameOrUuid(), StringUtils.join(groups, ", "));
                    break;
                case "remove":
                    for (String group : groups)
                        zone.removePlayerFromGroup(ident, group);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Player %s removed from group(s) %s",
                            ident.getUsernameOrUuid(), StringUtils.join(groups, ", "));
                    break;
                case "set":
                    for (GroupEntry g : APIRegistry.perms.getStoredPlayerGroups(ident))
                        zone.removePlayerFromGroup(ident, g.getGroup());
                    for (String group : groups)
                        zone.addPlayerToGroup(ident, group);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set %s's group(s) to %s",
                            ident.getUsernameOrUuid(), StringUtils.join(groups, ", "));
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------
    // -- Group
    // ------------------------------------------------------------

    public static void parseGlobal(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        parseGroupInner(ctx, params, Zone.GROUP_DEFAULT, null);
    }

    public static void parseGroup(CommandContext<CommandSourceStack> ctx, List<String> params) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Possible usage:");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> : Display group info");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> create : Create a new group");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> users : Show users in this group");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> perms : List group's permissions");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> priority : Set group priority");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> priority : Set group priority");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> parent|include (add|remove <group>)|clear : Set group hierarchy");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> suffix|prefix clear|<value> : Set group titles");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> zone <zone> ... : Work with zones");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/p group <group> zone <zone> allow|deny|clear <perms> : Set permissions");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/p group <group> zone <zone> value <perm> : View permission property");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/p group <group> zone <zone> spawn (bed enable|disable)|here|clear|(pos <pos>): Set group spawn");
            return;
        }

        String group = params.remove(0);
        if (!APIRegistry.perms.groupExists(group))
        {
            if (params.isEmpty())
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Group %s does not exist", group);
            }
            else
            {
                String groupArg = params.remove(0);
                if (!groupArg.equalsIgnoreCase("create"))
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Group %s does not exist", group);
                    return;
                }
                if (APIRegistry.perms.createGroup(group))
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Created group %s", group);
                else
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Could not create group %s. Cancelled.", group);
            }
            return;
        }
        else if (params.get(0).toLowerCase().equals("create"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Group %s alredy exists", group);
            return;
        }

        parseGroupInner(ctx, params, group, null);
    }

    public static void parseGroupInner(CommandContext<CommandSourceStack> ctx, List<String> params, String group, Zone zone)
            throws CommandRuntimeException
    {
        String cmd = params.remove(0).toLowerCase();

        if (cmd.equals("zone"))
        {
            if (zone != null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Zone already set!");
                return;
            }
            if (params.isEmpty())
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Expected zone identifier.");
                return;
            }
            zone = parseZone(ctx, params);
            if (zone == null)
                return;
            params.remove(0);
            parseGroupInner(ctx, params, group, zone);
            return;
        }

        // Set default zone
        if (zone == null)
        {
            zone = APIRegistry.perms.getServerZone();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Using Main Zone");
        }
        // Catch case where zone is defined
        if (cmd.equals("zone"))
        {
            cmd = params.remove(0).toLowerCase();
        }
        switch (cmd)
        {
        case "perms":
            if (zone.getGroupPermissions(group) == null)
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "Group " + group + " has no permissions in zone " + zone.getName() + ".");
            }
            else
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "Group " + group + " permissions in zone " + zone.getName() + ":");
                for (Entry<String, String> perm : zone.getGroupPermissions(group).entrySet())
                {
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + perm.getKey() + " = " + perm.getValue());
                }
            }
            break;
        case "users":
            listGroupUsers(ctx.getSource(), group);
            break;
        case "prefix":
            parseGroupPrefixSuffix(ctx, params, group, zone, false);
            break;
        case "suffix":
            parseGroupPrefixSuffix(ctx, params, group, zone, true);
            break;
        case "spawn":
            parseGroupSpawn(ctx, params, group, zone, false);
            break;
        case "priority":
            parseGroupPriority(ctx, params, group);
            break;
        case "parent":
            parseGroupInclude(ctx, params, group, true);
            break;
        case "include":
            parseGroupInclude(ctx, params, group, false);
            break;
        case "allow":
            parseGroupPermissions(ctx, params, group, zone, PermissionAction.ALLOW);
            break;
        case "deny":
            parseGroupPermissions(ctx, params, group, zone, PermissionAction.DENY);
            break;
        case "clear":
            parseGroupPermissions(ctx, params, group, zone, PermissionAction.CLEAR);
            break;
        case "value":
            parseGroupPermissions(ctx, params, group, zone, PermissionAction.VALUE);
            break;
        case "denydefault":
            denyDefault(zone.getGroupPermissions(group));
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_INVALID_SYNTAX+""+ cmd+""+ params.toString());
            return;
        }
    }

    public static void parseGroupPrefixSuffix(CommandContext<CommandSourceStack> ctx, List<String> params, String group,
            Zone zone, boolean isSuffix) throws CommandRuntimeException
    {
        String fixName = isSuffix ? "suffix" : "prefix";
        if (params.isEmpty())
        {
            String fix = zone.getGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (fix == null || fix.isEmpty())
                fix = "empty";
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s's %s is %s", group, fixName, fix);
        }
        else
        {
            String fix = StringUtils.join(params, " ");
            if (fix.equalsIgnoreCase("clear"))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s's %s cleared", group, fixName);
                zone.clearGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            }
            else
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s's %s set to %s", group, fixName, fix);
                zone.setGroupPermissionProperty(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
            }
        }
    }

    public static void parseGroupPermissions(CommandContext<CommandSourceStack> ctx, List<String> params, String group,
            Zone zone, PermissionAction type) throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Missing permission argument!");
            return;
        }

        // Apply permissions
        // while (!arguments.args.isEmpty())
        {
            String permissionNode = Zone.fixPerms(params.remove(0));
            String msg = null, value = null;
            if (type == PermissionAction.VALUE)
            {
                if (params.isEmpty())
                {
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Value of %s = %s", permissionNode,
                            zone.getGroupPermission(group, permissionNode));
                    return;
                }
                value = StringUtils.join(params, ' ');
                params.clear();
            }

            switch (type)
            {
            case ALLOW:
                zone.setGroupPermission(group, permissionNode, true);
                msg = "Allowed %s access to %s in zone %s";
                break;
            case DENY:
                zone.setGroupPermission(group, permissionNode, false);
                msg = "Denied %s access to %s in zone %s";
                break;
            case CLEAR:
                zone.clearGroupPermission(group, permissionNode);
                msg = "Cleared %s's access to %s in zone %s";
                break;
            case VALUE:
                zone.setGroupPermissionProperty(group, permissionNode, value);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set %s for group %s to %s in zone %s",
                        permissionNode, group, value, zone.getName());
                break;
            }
            if (msg != null)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), msg, group, permissionNode, zone.getName());
            if (type == PermissionAction.VALUE)
                return;
        }
    }

    public static void parseGroupSpawn(CommandContext<CommandSourceStack> ctx, List<String> params, String group, Zone zone,
            boolean commandSetspawn) throws CommandRuntimeException
    {
        if (params.get(0).equals("help"))
        {
            params.remove(0);
            if (commandSetspawn)
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/setspawn here|clear|<x> <y> <z> <dim>: Set spawn location");
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/setspawn bed (enable|disable): Enable/disable spawning at bed");
            }
            else
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/feperm group " + group + " zone "+zone.getName() + " spawn here|clear|<x> <y> <z> <dim>: Set spawn location");
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        "/feperm group " + group + " zone "+zone.getName() + " spawn bed (enable|disable): Enable/disable spawning at bed");
            }
            return;
        }

        String loc = params.remove(0).toLowerCase();
        WarpPoint point = null;
        switch (loc)
        {
        case "here":
            if (getServerPlayer(ctx.getSource()) == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "[here] cannot be used from console.");
                return;
            }
            point = new WarpPoint(getServerPlayer(ctx.getSource()));
            break;
        case "bed":
        {
            if (params.isEmpty())
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
                return;
            }
            String val = params.remove(0).toLowerCase();
            if (val.equals("true") | val.equals("enable"))
            {
                zone.setGroupPermission(group, FEPermissions.SPAWN_BED, true);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Enabled bed-spawning for group %s in zone %s",
                        group, zone.getName());
            }
            else if (val.equals("false") | val.equals("disable"))
            {
                zone.setGroupPermission(group, FEPermissions.SPAWN_BED, false);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Disabled bed-spawning for group %s in zone %s",
                        group, zone.getName());
            }
            else
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid argument. Use enable or disable.");
            return;
        }
        case "clear":
            point = null;
            break;
        default:
            if (params.size() < 3)
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
                return;
            }
            try
            {
                int x = CommandUtils.parseInt(loc);
                int y = CommandUtils.parseInt(params.remove(0));
                int z = CommandUtils.parseInt(params.remove(0));
                String dimension = params.remove(0);
                point = new WarpPoint(dimension, x, y, z, 0, 0);
            }
            catch (NumberFormatException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid location argument");
                return;
            }
            break;
        }

        if (point == null)
        {
            zone.clearGroupPermission(group, FEPermissions.SPAWN_LOC);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared spawn-rule for group %s in zone %s", group,
                    zone.getName());
        }
        else
        {
            zone.setGroupPermissionProperty(group, FEPermissions.SPAWN_LOC, point.toString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set spawn for group %s to %s in zone %s", group,
                    point.toString(), zone.getName());
        }
    }

    public static void parseGroupPriority(CommandContext<CommandSourceStack> ctx, List<String> params, String group)
            throws CommandRuntimeException
    {
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Priority for group " + group + ": "
                    + APIRegistry.perms.getGroupPermissionProperty(group, FEPermissions.GROUP_PRIORITY));
            return;
        }
        String priorityValue = params.remove(0);
        try
        {
            APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_PRIORITY,
                    Integer.toString(Integer.parseInt(priorityValue)));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set priority for group %s to %s", group,
                    priorityValue);
        }
        catch (NumberFormatException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "The string %s is not a valid integer", priorityValue);
        }
    }

    public static void parseGroupInclude(CommandContext<CommandSourceStack> ctx, List<String> params, String group,
            boolean isParent) throws CommandRuntimeException
    {
        final String displayName1 = (isParent ? " parent" : " include");

        // Get included groups
        Set<String> groups = isParent ? APIRegistry.perms.getServerZone().getParentedGroups(group)
                : APIRegistry.perms.getServerZone().getIncludedGroups(group);

        if (params.isEmpty())
        {
            // arguments.info("/feperm group " + group + " " + displayName1 + " add|remove
            // <group>");
            // arguments.info("/feperm group " + group + " " + displayName1 + " clear");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    (isParent ? "Parented" : "Included") + " groups for %s:", group);
            for (String includedGroup : groups)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + includedGroup);
            return;
        }

        String cmd = params.remove(0).toLowerCase();
        if (cmd.equals("clear"))
        {
            APIRegistry.perms.getServerZone().clearGroupPermission(group,
                    isParent ? FEPermissions.GROUP_PARENTS : FEPermissions.GROUP_INCLUDES);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared group-" + displayName1 + "s for group %s",
                    group);
            return;
        }

        if (params.isEmpty())
        {
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_INVALID_SYNTAX);
            return;
        }

        String groupsName = params.remove(0);
        switch (cmd)
        {
        case "add":
            groups.add(groupsName);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Added group-" + displayName1 + " of %s to group %s",
                    groupsName, group);
            break;
        case "remove":
            groups.remove(groupsName);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed group-" + displayName1 + " of %s to group %s",
                    groupsName, group);
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_INVALID_SYNTAX);
            return;
        }

        APIRegistry.perms.setGroupPermissionProperty(group,
                isParent ? FEPermissions.GROUP_PARENTS : FEPermissions.GROUP_INCLUDES, StringUtils.join(groups, ","));
    }

    // ------------------------------------------------------------
    // -- Utils
    // ------------------------------------------------------------

    public static Zone parseZone(CommandContext<CommandSourceStack> ctx, List<String> params)
    {
        String zoneId = params.get(0).replace("-", ":");
        if(zoneId.equals("MainServerZone")) {
            return APIRegistry.perms.getServerZone();
        }
        try
        {
            int intId = Integer.parseInt(zoneId);
            if (intId < 1)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Zone ID must be greater than 0!");
                return null;
            }

            Zone zone = APIRegistry.perms.getZoneById(intId);
            if (zone != null)
                return zone;

            ServerLevel world = APIRegistry.namedWorldHandler.getWorld(zoneId);
            if (world != null)
                return APIRegistry.perms.getServerZone().getWorldZone(world);

            ChatOutputHandler.chatError(ctx.getSource(), "No zone by the ID %s exists!", zoneId);
            return null;
        }
        catch (NumberFormatException e)
        {
            for (WorldZone wz : APIRegistry.perms.getServerZone().getWorldZones().values())
                if (wz.getName().equals(zoneId))
                    return wz;
            ServerLevel world = APIRegistry.namedWorldHandler.getWorld(zoneId);
            if (world != null)
                return APIRegistry.perms.getServerZone().getWorldZone(world);

            if (getServerPlayer(ctx.getSource()) == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(),
                        Translator.translate("Cannot identify areas by name from console!"));
                return null;
            }

            Zone zone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).getLevel())
                    .getAreaZone(zoneId);
            if (zone != null)
                return zone;

            ChatOutputHandler.chatError(ctx.getSource(), "No zone by the name %s exists!", zoneId);
            return null;
        }
    }

    public static Zone parseZoneSafe(CommandSourceStack ctx, String zoneId)
    {
        zoneId = zoneId.replace("-", ":");
        if(zoneId.equals("MainServerZone")) {
            return APIRegistry.perms.getServerZone();
        }
        try
        {
            int intId = Integer.parseInt(zoneId);
            if (intId < 1)
            {
                return null;
            }

            Zone zone = APIRegistry.perms.getZoneById(intId);
            if (zone != null)
                return zone;

            ServerLevel world = APIRegistry.namedWorldHandler.getWorld(zoneId);
            if (world != null)
                return APIRegistry.perms.getServerZone().getWorldZone(world);
            return null;
        }
        catch (NumberFormatException e)
        {
            for (WorldZone wz : APIRegistry.perms.getServerZone().getWorldZones().values())
                if (wz.getName().equals(zoneId))
                    return wz;
            ServerLevel world = APIRegistry.namedWorldHandler.getWorld(zoneId);
            if (world != null)
                return APIRegistry.perms.getServerZone().getWorldZone(world);

            if (getServerPlayer(ctx) == null)
            {
                return null;
            }

            return APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx).getLevel())
                    .getAreaZone(zoneId);
        }
    }

    public static void listUserPermissions(CommandSourceStack sender, UserIdent ident, boolean showGroupPerms)
            throws CommandRuntimeException
    {
        ChatOutputHandler.chatNotification(sender, ident.getUsernameOrUuid() + " permissions:");

        Map<Zone, Map<String, String>> userPerms = ModulePermissions.permissionHelper.enumUserPermissions(ident);
        for (Entry<Zone, Map<String, String>> zone : userPerms.entrySet())
        {
            boolean printedZone = false;
            for (Entry<String, String> perm : zone.getValue().entrySet())
            {
                if (perm.getKey().startsWith(FEPermissions.GROUP))
                    continue;
                if (!printedZone)
                {
                    ChatOutputHandler.chatWarning(sender,
                            "Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                    printedZone = true;
                }
                ChatOutputHandler.chatNotification(sender, "  " + perm.getKey() + " = " + perm.getValue());
            }
        }
        if (showGroupPerms)
        {
            for (GroupEntry group : APIRegistry.perms.getPlayerGroups(ident))
            {
                Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper
                        .enumGroupPermissions(group.getGroup(), false);
                if (!groupPerms.isEmpty())
                {
                    boolean printedGroup = false;
                    for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet())
                    {
                        boolean printedZone = false;
                        for (Entry<String, String> perm : zone.getValue().entrySet())
                        {
                            if (perm.getKey().equals(FEPermissions.GROUP)
                                    || perm.getKey().equals(FEPermissions.GROUP_PRIORITY)
                                    || perm.getKey().equals(FEPermissions.PREFIX)
                                    || perm.getKey().equals(FEPermissions.SUFFIX))
                                continue;
                            if (!printedGroup)
                            {
                                ChatOutputHandler.chatWarning(sender, "Group " + group);
                                printedGroup = true;
                            }
                            if (!printedZone)
                            {
                                ChatOutputHandler.chatWarning(sender,
                                        "  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                                printedZone = true;
                            }
                            ChatOutputHandler.chatNotification(sender,
                                    "    " + perm.getKey() + " = " + perm.getValue());
                        }
                    }
                }
            }
        }
    }

    public static void listGroupPermissions(CommandSourceStack sender, String group)
    {
        Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper.enumGroupPermissions(group,
                false);
        if (!groupPerms.isEmpty())
        {
            for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet())
            {
                boolean printedZone = false;
                for (Entry<String, String> perm : zone.getValue().entrySet())
                {
                    if (perm.getKey().equals(FEPermissions.GROUP) || perm.getKey().equals(FEPermissions.GROUP_PRIORITY)
                            || perm.getKey().equals(FEPermissions.PREFIX) || perm.getKey().equals(FEPermissions.SUFFIX))
                        continue;
                    if (!printedZone)
                    {
                        ChatOutputHandler.chatWarning(sender,
                                "  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                        printedZone = true;
                    }
                    ChatOutputHandler.chatNotification(sender, "    " + perm.getKey() + " = " + perm.getValue());
                }
            }
        }
    }

    public static void listZones(CommandSourceStack sender, WorldPoint location)
    {
        ChatOutputHandler.chatNotification(sender, "Zones at position " + location.toString());
        for (Zone zone : APIRegistry.perms.getServerZone().getZonesAt(location))
        {
            if (zone.isHidden())
                continue;
            ChatOutputHandler.chatNotification(sender, "  #" + zone.getId() + " " + zone.toString());
        }
    }

    public static void listWorlds(CommandSourceStack sender) throws CommandRuntimeException
    {
        ChatOutputHandler.chatNotification(sender, "World IDs:");
        for (WorldZone zone : APIRegistry.perms.getServerZone().getWorldZones().values())
        {
            if (zone.isHidden())
                continue;
            ChatOutputHandler.chatNotification(sender,
                    String.format("  %s (%s): #%d / %s",
                            APIRegistry.namedWorldHandler.getWorldName(zone.getDimensionID()), zone.getDimensionID(),
                            zone.getId(), zone.toString()));
        }
    }

    public static void listGroups(CommandSourceStack sender) throws CommandRuntimeException
    {
        ChatOutputHandler.chatNotification(sender, "Groups:");
        for (String group : APIRegistry.perms.getServerZone().getGroups())
            ChatOutputHandler.chatNotification(sender, " - " + group);
    }

    public static void listUsers(CommandSourceStack sender) throws CommandRuntimeException
    {
        ChatOutputHandler.chatNotification(sender, "Known players:");
        for (UserIdent ident : APIRegistry.perms.getServerZone().getKnownPlayers())
            ChatOutputHandler.chatNotification(sender, " - " + ident.getUsernameOrUuid());

        ChatOutputHandler.chatNotification(sender, "Online players:");
        for (ServerPlayer player : ServerUtil.getPlayerList())
            ChatOutputHandler.chatNotification(sender, " - " + player.getDisplayName().getString());
    }

    public static void listGroupUsers(CommandSourceStack sender, String group)
    {
        Set<UserIdent> players = ModulePermissions.permissionHelper.getServerZone().getGroupPlayers().get(group);
        ChatOutputHandler.chatNotification(sender, "Players in group " + group + ":");
        if (players != null)
            for (UserIdent player : players)
                ChatOutputHandler.chatNotification(sender, "  " + player.getUsernameOrUuid());
    }

    public static void denyDefault(PermissionList list)
    {
        List<String> filter = Arrays.asList(ModuleProtection.PERM_BREAK, ModuleProtection.PERM_EXPLODE,
                ModuleProtection.PERM_PLACE, ModuleProtection.PERM_INTERACT, ModuleProtection.PERM_USE,
                ModuleProtection.PERM_INVENTORY, ModuleProtection.PERM_EXIST, ModuleProtection.PERM_CRAFT,
                ModuleProtection.PERM_MOBSPAWN, ModuleProtection.PERM_DAMAGE_BY, ModuleProtection.PERM_DAMAGE_TO,
                FEPermissions.FE_INTERNAL);

        RootZone rootZone = APIRegistry.perms.getServerZone().getRootZone();
        mainLoop: for (Entry<String, String> perm : rootZone.getGroupPermissions(Zone.GROUP_DEFAULT).entrySet())
        {
            if (Zone.PERMISSION_FALSE.equals(perm.getValue()))
                continue;
            if (list.containsKey(perm.getKey()))
                continue;
            if (perm.getKey().endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                continue;
            for (String f : filter)
                if (perm.getKey().startsWith(f))
                    continue mainLoop;
            list.put(perm.getKey(), perm.getValue());
        }
        list.put("*", Zone.PERMISSION_FALSE);
        list.put(ModuleProtection.PERM_USE + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
        list.put(ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
        list.put(ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
        list.put(ModuleProtection.PERM_EXIST + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
        list.put(ModuleProtection.PERM_CRAFT + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
        list.put(ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
        list.put(ModuleProtection.PERM_INVENTORY + Zone.ALL_PERMS, Zone.PERMISSION_TRUE);
    }
}