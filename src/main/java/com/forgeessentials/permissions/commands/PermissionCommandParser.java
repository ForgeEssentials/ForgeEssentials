package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;

public class PermissionCommandParser
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
    private static final String[] parseMainArgs = { "user", "group", "global", "list", "test", "reload", "save", "debug" }; // "export",
                                                                                                                            // "promote",
    private static final String[] parseListArgs = { "zones", "perms", "users", "groups" };
    private static final String[] parseUserArgs = { "zone", "group", "allow", "deny", "clear", "value", "true", "false", "spawn", "prefix", "suffix", "perms" };
    private static final String[] parseGroupArgs = { "zone", "users", "allow", "deny", "clear", "value", "true", "false", "spawn", "prefix", "suffix", "perms",
            "priority", "parent", "include" };
    private static final String[] parseUserGroupArgs = { "add", "remove", "set" };
    private static final String[] parseGroupIncludeArgs = { "add", "remove", "clear" };
    private static final String[] parseSpawnArgs = { "here", "clear", "bed" };

    public static void parseMain(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion && arguments.args.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseMainArgs);
            return;
        }
        if (arguments.args.isEmpty())
        {
            arguments.confirm("/feperm " + StringUtils.join(parseMainArgs, "|") + ": Displays help for the subcommands");
        }
        else
        {
            switch (arguments.args.remove().toLowerCase())
            {
            case "save":
                arguments.checkPermission(PERM_SAVE);
                arguments.tabComplete("disable", "enable");
                if (arguments.isTabCompletion)
                    return;
                if (arguments.isEmpty())
                {
                    ModulePermissions.permissionHelper.setDirty(false);
                    ModulePermissions.permissionHelper.save();
                    arguments.confirm("Permissions saved!");
                }
                else
                {
                    String action = arguments.remove().toLowerCase();
                    switch (action)
                    {
                    case "enable":
                        ModulePermissions.permissionHelper.disableAutoSave = false;
                        arguments.confirm("Permission saving enabled");
                        break;
                    case "disable":
                        ModulePermissions.permissionHelper.disableAutoSave = true;
                        arguments.confirm("Permission saving disabled");
                        break;
                    default:
                        throw new TranslatedCommandException.InvalidSyntaxException();
                    }
                }
                break;
            case "reload":
                arguments.checkPermission(PERM_RELOAD);
                if (arguments.isTabCompletion)
                    return;
                if (ModulePermissions.permissionHelper.load())
                    arguments.confirm("Successfully reloaded permissions");
                else
                    arguments.error("Error while reloading permissions");
                break;
            case "test":
                parseTest(arguments);
                break;
            case "list":
                parseList(arguments);
                break;
            case "user":
                parseUser(arguments);
                break;
            case "group":
                parseGroup(arguments);
                break;
            case "global":
                parseGlobal(arguments);
                break;
            case "debug":
                if (arguments.isTabCompletion)
                    return;
                if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_DEBUG))
                {
                    arguments.error(FEPermissions.MSG_NO_COMMAND_PERM);
                    return;
                }
                if (ModulePermissions.permissionHelper.permissionDebugUsers.contains(arguments.sender))
                {
                    ModulePermissions.permissionHelper.permissionDebugUsers.remove(arguments.sender);
                    arguments.confirm("Permission debug mode off");
                }
                else
                {
                    ModulePermissions.permissionHelper.permissionDebugUsers.add(arguments.sender);
                    arguments.confirm("Permission debug mode on");
                }
                break;
            default:
                arguments.error("Unknown command argument");
                break;
            }
        }
    }

    // ------------------------------------------------------------
    // -- Listings
    // ------------------------------------------------------------

    public static void parseList(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
        {
            if (arguments.args.size() == 1)
                arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseListArgs);
            return;
        }
        if (arguments.args.isEmpty())
        {
            arguments.confirm("/feperm list " + StringUtils.join(parseListArgs, "|") + " : List the specified objects");
        }
        else
        {
            String arg = arguments.args.remove().toLowerCase();
            switch (arg)
            {
            case "zones":
                if (arguments.senderPlayer == null)
                    throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
                listZones(arguments.senderPlayer, new WorldPoint(arguments.senderPlayer));
                break;
            case "perms":
                if (arguments.senderPlayer == null)
                    throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
                listUserPermissions(arguments.sender, new UserIdent(arguments.senderPlayer), true);
                break;
            case "users":
                listUsers(arguments.sender);
                break;
            case "groups":
                listGroups(arguments.sender);
                break;
            default:
                arguments.error("Unknown command argument");
                break;
            }
        }
    }

    public static void parseTest(CommandParserArgs arguments)
    {
        if (arguments.args.isEmpty())
            throw new TranslatedCommandException("Missing permission argument!");
        if (arguments.senderPlayer == null)
            throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_TEST))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        if (arguments.isTabCompletion)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseUserArgs);
            for (Zone zone : APIRegistry.perms.getZones())
            {
                if (CommandBase.doesStringStartWith(arguments.args.peek(), zone.getName()))
                    arguments.tabCompletion.add(zone.getName());
            }
            for (String perm : APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions())
            {
                if (CommandBase.doesStringStartWith(arguments.args.peek(), perm))
                    arguments.tabCompletion.add(perm);
            }
            return;
        }

        String permissionNode = arguments.args.remove();
        String result = APIRegistry.perms.getPermissionProperty(arguments.senderPlayer, permissionNode);
        if (result == null)
        {
            arguments.confirm(permissionNode + " = \u00a7etrue (not set)");
        }
        else if (Zone.PERMISSION_FALSE.equalsIgnoreCase(result))
        {
            arguments.confirm(permissionNode + " = \u00a7c" + result);
        }
        else
        {
            arguments.confirm(permissionNode + " = " + result);
        }
    }

    // ------------------------------------------------------------
    // -- User
    // ------------------------------------------------------------

    public static void parseUser(CommandParserArgs arguments)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_USER))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        if (arguments.args.isEmpty())
        {
            arguments.confirm("Possible usage:");
            arguments.confirm("/p user <player> : Display user info");
            arguments.confirm("/p user <player> zone <zone> ... : Work with zones");
            arguments.confirm("/p user <player> perms : List player's permissions");
            arguments.confirm("/p user <player> group add|remove <group>: Player's group settings");
            arguments.confirm("/p user <player> allow|deny|clear <perms> : Set permissions");
            arguments.confirm("/p user <player> value <perm> <value> : Set permission property");
            arguments.confirm("/p user <player> spawn : Set player spawn");
            return;
        }

        // Parse player
        UserIdent ident = arguments.parsePlayer(false);
        if (ident == null)
            return;
        if (!ident.hasUUID())
            arguments.error(String.format("Player %s not found. playername will be used, but may be inaccurate.", ident.getUsername()));

        parseUserInner(arguments, ident, null);
    }

    public static void parseUserInner(CommandParserArgs arguments, UserIdent ident, Zone zone)
    {
        // Display help or player info
        if (arguments.args.isEmpty())
        {
            if (zone == null)
            {
                arguments.confirm(String.format("Groups for player %s:", ident.getUsernameOrUUID()));
                for (GroupEntry group : APIRegistry.perms.getPlayerGroups(ident))
                {
                    arguments.confirm("  " + group);
                }
                return;
            }
            // args.info("Possible usage:");
            // args.info("/p ... group add|remove <group>: Player's group settings");
            // args.info("/p ... allow|deny|clear <perms> : Set permissions");
            // args.info("/p ... value <perm> <value> : Set permission property");
            // args.info("/p ... spawn : Set player spawn");
            arguments.confirm(ident.getUsernameOrUUID() + "'s permissions in zone " + zone.getName() + ":");
            for (Entry<String, String> perm : zone.getPlayerPermissions(ident).entrySet())
            {
                arguments.confirm("  " + perm.getKey() + " = " + (perm.getValue() == null ? "null" : perm.getValue()));
            }
            return;
        }

        // TAB-complete command
        if (arguments.isTabCompletion && arguments.args.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseUserArgs);
            if (zone != null)
                arguments.tabCompletion.remove("zone");
            return;
        }

        String cmd = arguments.args.remove().toLowerCase();

        if (cmd.equals("zone"))
        {
            if (zone != null)
            {
                arguments.error(String.format("Zone already set!"));
                return;
            }
            if (arguments.args.isEmpty())
            {
                arguments.error(String.format("Expected zone identifier."));
                return;
            }
            zone = getZoneByName(arguments.sender, arguments.args.remove());
            if (zone == null)
                return;
            parseUserInner(arguments, ident, zone);
            return;
        }

        // Set default zone
        if (zone == null)
            zone = APIRegistry.perms.getServerZone();

        // Parse command
        switch (cmd)
        {
        case "group":
            parseUserGroup(arguments, ident, zone);
            break;
        case "perms":
            listUserPermissions(arguments.sender, ident, true);
            break;
        case "prefix":
            parseUserPrefixSuffix(arguments, ident, zone, false);
            break;
        case "suffix":
            parseUserPrefixSuffix(arguments, ident, zone, true);
            break;
        case "spawn":
            parseUserSpawn(arguments, ident, zone);
            break;
        case "true":
        case "allow":
            parseUserPermissions(arguments, ident, zone, PermissionAction.ALLOW);
            break;
        case "false":
        case "deny":
            parseUserPermissions(arguments, ident, zone, PermissionAction.DENY);
            break;
        case "clear":
            parseUserPermissions(arguments, ident, zone, PermissionAction.CLEAR);
            break;
        case "value":
            parseUserPermissions(arguments, ident, zone, PermissionAction.VALUE);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    public static void parseUserPrefixSuffix(CommandParserArgs arguments, UserIdent ident, Zone zone, boolean isSuffix)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_USER_FIX))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        if (arguments.isTabCompletion)
            return;

        String fixName = isSuffix ? "suffix" : "prefix";
        if (arguments.args.isEmpty())
        {
            String fix = zone.getPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (fix == null || fix.isEmpty())
                fix = "empty";
            arguments.confirm(String.format("%s's %s is %s", ident.getUsernameOrUUID(), fixName, fix));
        }
        else
        {
            String fix = StringUtils.join(arguments.args, " ");
            if (fix.equalsIgnoreCase("clear"))
            {
                arguments.confirm(String.format("%s's %s cleared", ident.getUsernameOrUUID(), fixName));
                zone.clearPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            }
            else
            {
                arguments.confirm(String.format("%s's %s set to %s", ident.getUsernameOrUUID(), fixName, fix));
                zone.setPlayerPermissionProperty(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
            }
        }
    }

    public static void parseUserPermissions(CommandParserArgs arguments, UserIdent ident, Zone zone, PermissionAction type)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_USER_PERMS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        if (arguments.args.isEmpty())
            throw new TranslatedCommandException("Missing permission argument!");

        // Apply permissions
        while (!arguments.args.isEmpty())
        {
            if (arguments.isTabCompletion && arguments.args.size() == 1)
            {
                if (type != PermissionAction.CLEAR)
                    arguments.tabCompletion = completePermission(arguments.args.peek());
                else
                    arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(),
                            zone.getPlayerPermissions(ident).keySet());
                return;
            }

            String permissionNode = arguments.args.remove();
            String msg = null, value = null;
            if (type == PermissionAction.VALUE)
            {
                if (arguments.args.isEmpty())
                {
                    arguments.confirm(Translator.format("Value of %s = %s", permissionNode, zone.getPlayerPermission(ident, permissionNode)));
                    return;
                }
                value = StringUtils.join(arguments.args, ' ');
                arguments.args.clear();
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
                arguments.confirm(String.format("Set %s for %s to %s in zone %s", permissionNode, ident.getUsernameOrUUID(), value, zone.getName()));
                break;
            }
            if (msg != null)
                arguments.confirm(String.format(msg, ident.getUsernameOrUUID(), permissionNode, zone.getName()));
        }
    }

    public static void parseUserSpawn(CommandParserArgs arguments, UserIdent ident, Zone zone)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_USER_SPAWN))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        if (arguments.args.isEmpty())
        {
            arguments.confirm("/feperm user " + ident.getUsernameOrUUID() + " spawn here|clear|<x> <y> <z> <dim>: Set spawn location");
            arguments.confirm("/feperm user " + ident.getUsernameOrUUID() + " spawn bed (enable|disable): Enable/disable spawning at bed");
            return;
        }
        if (arguments.isTabCompletion)
        {
            if (arguments.args.size() == 1)
                arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseSpawnArgs);
            return;
        }

        String loc = arguments.args.remove().toLowerCase();
        WorldPoint point = null;
        switch (loc)
        {
        case "here":
            if (arguments.senderPlayer == null)
                throw new TranslatedCommandException("[here] cannot be used from console.");
            point = new WorldPoint(arguments.senderPlayer);
            break;
        case "bed":
        {
            if (arguments.args.isEmpty())
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            String val = arguments.args.peek().toLowerCase();
            if (val.equals("true") | val.equals("enable"))
            {
                zone.setPlayerPermission(ident, FEPermissions.SPAWN_BED, true);
                arguments.confirm(String.format("Enabled bed-spawning for user %s in zone %s", ident.getUsernameOrUUID(), zone.getName()));
            }
            else if (val.equals("false") | val.equals("disable"))
            {
                zone.setPlayerPermission(ident, FEPermissions.SPAWN_BED, false);
                arguments.confirm(String.format("Disabled bed-spawning for user %s in zone %s", ident.getUsernameOrUUID(), zone.getName()));
            }
            arguments.confirm("Invalid argument. Use enable or disable.");
            return;
        }
        case "clear":
            break;
        default:
            if (arguments.args.size() < 3)
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            try
            {
                int x = CommandBase.parseInt(arguments.sender, loc);
                int y = CommandBase.parseInt(arguments.sender, arguments.args.remove());
                int z = CommandBase.parseInt(arguments.sender, arguments.args.remove());
                int dimension = CommandBase.parseInt(arguments.sender, arguments.args.remove());
                point = new WorldPoint(dimension, x, y, z);
            }
            catch (NumberFormatException e)
            {
                arguments.error("Invalid location argument");
                return;
            }
            break;
        }

        if (point == null)
        {
            zone.clearPlayerPermission(ident, FEPermissions.SPAWN_LOC);
            arguments.confirm(String.format("Cleared spawn-rule for user %s in zone %s", ident.getUsernameOrUUID(), zone.getName()));
        }
        else
        {
            zone.setPlayerPermissionProperty(ident, FEPermissions.SPAWN_LOC, point.toString());
            arguments.confirm(String.format("Set spawn for user %s to %s in zone %s", ident.getUsernameOrUUID(), point.toString(), zone.getName()));
        }
    }

    public static void parseUserGroup(CommandParserArgs arguments, UserIdent ident, Zone zone)
    {
        if (arguments.isTabCompletion && arguments.args.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseUserGroupArgs);
            return;
        }
        if (arguments.args.isEmpty())
        {
            if (zone instanceof ServerZone)
                arguments.confirm(String.format("Groups for player %s (without includes):", ident.getUsernameOrUUID()));
            else
                arguments.confirm(String.format("Groups for player %s (without includes) in %s:", ident.getUsernameOrUUID(), zone.getName()));
            for (GroupEntry g : zone.getStoredPlayerGroupEntries(ident))
            {
                arguments.confirm("  " + g);
            }
        }
        else
        {
            String mode = arguments.args.remove().toLowerCase();
            if (!mode.equals("add") && !mode.equals("remove") && !mode.equals("set"))
            {
                arguments.error("Syntax error. Please try this instead:");
                arguments.error("/p user <player> group add|set|remove <GROUP>");
                return;
            }

            if (arguments.isTabCompletion)
            {
                if (arguments.args.size() == 1)
                {
                    arguments.tabCompletion = new ArrayList<String>();
                    for (String group : APIRegistry.perms.getServerZone().getGroups())
                    {
                        if (CommandBase.doesStringStartWith(arguments.args.peek(), group))
                            arguments.tabCompletion.add(group);
                    }
                }
                return;
            }
            if (arguments.args.isEmpty())
            {
                arguments.error("Usage: /p user <player> group " + mode + " <group-name>");
            }
            else
            {
                String groups[] = arguments.args.remove().split(",");
                for (String group : groups)
                    if (!APIRegistry.perms.groupExists(group))
                    {
                        arguments.error(String.format("Group %s not found.", group));
                        return;
                    }

                switch (mode)
                {
                case "add":
                    for (String group : groups)
                        zone.addPlayerToGroup(ident, group);
                    arguments.confirm(String.format("Player %s added to group(s) %s", ident.getUsernameOrUUID(), StringUtils.join(groups, ", ")));
                    break;
                case "remove":
                    for (String group : groups)
                        zone.removePlayerFromGroup(ident, group);
                    arguments.confirm(String.format("Player %s removed from group(s) %s", ident.getUsernameOrUUID(), StringUtils.join(groups, ", ")));
                    break;
                case "set":
                    for (GroupEntry g : APIRegistry.perms.getStoredPlayerGroups(ident))
                        zone.removePlayerFromGroup(ident, g.getGroup());
                    for (String group : groups)
                        zone.addPlayerToGroup(ident, group);
                    arguments.confirm(String.format("Set %s's group(s) to %s", ident.getUsernameOrUUID(), StringUtils.join(groups, ", ")));
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------
    // -- Group
    // ------------------------------------------------------------

    public static void parseGroup(CommandParserArgs arguments)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        if (arguments.args.isEmpty())
        {
            arguments.confirm("Possible usage:");
            arguments.confirm("/p group <group> : Display group info");
            arguments.confirm("/p group <group> users : Show users in this group");
            arguments.confirm("/p group <group> zone <zone> ... : Work with zones");
            arguments.confirm("/p group <group> create : Create a new group");
            arguments.confirm("/p group <group> perms : List group's permissions");
            arguments.confirm("/p group <group> allow|deny|clear <perms> : Set permissions");
            arguments.confirm("/p group <group> value <perm> <value> : Set permission property");
            arguments.confirm("/p group <group> spawn : Set group spawn");
            return;
        }

        // Auto-complete group name
        if (arguments.isTabCompletion && arguments.args.size() == 1)
        {
            arguments.tabCompletion = new ArrayList<String>();
            for (String group : APIRegistry.perms.getServerZone().getGroups())
            {
                if (CommandBase.doesStringStartWith(arguments.args.peek(), group))
                    arguments.tabCompletion.add(group);
            }
            return;
        }

        String group = arguments.args.remove();
        if (!APIRegistry.perms.groupExists(group))
        {
            if (arguments.isTabCompletion && arguments.args.size() == 1)
            {
                arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), "create");
                return;
            }
            if (arguments.args.isEmpty())
            {
                arguments.confirm(String.format("Group %s does not exist", group));
            }
            else
            {
                String groupArg = arguments.args.remove();
                if (groupArg.equalsIgnoreCase("create"))
                {
                    if (APIRegistry.perms.createGroup(group))
                        arguments.confirm(String.format("Created group %s", group));
                    else
                        arguments.confirm(String.format("Could not create group %s. Cancelled.", group));
                }
                else
                {
                    arguments.error(String.format("Group %s does not exist", group));
                }
            }
            return;
        }

        parseGroupInner(arguments, group, null);
    }

    public static void parseGlobal(CommandParserArgs arguments)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        parseGroupInner(arguments, Zone.GROUP_DEFAULT, null);
    }

    public static void parseGroupInner(CommandParserArgs arguments, String group, Zone zone)
    {
        // Display help or player info
        if (arguments.args.isEmpty())
        {
            if (zone == null)
            {
                arguments.confirm("Group " + group + " permissions:");
                listGroupPermissions(arguments.sender, group);
                return;
            }
            if (zone.getGroupPermissions(group) == null)
            {
                arguments.confirm("Group " + group + " has no permissions in zone " + zone.getName() + ".");
            }
            else
            {
                arguments.confirm("Group " + group + " permissions in zone " + zone.getName() + ":");
                for (Entry<String, String> perm : zone.getGroupPermissions(group).entrySet())
                {
                    arguments.confirm("  " + perm.getKey() + " = " + perm.getValue());
                }
            }
            return;
        }

        // TAB-complete command
        if (arguments.isTabCompletion && arguments.args.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseGroupArgs);
            if (zone != null)
                arguments.tabCompletion.remove("zone");
            return;
        }

        String cmd = arguments.args.remove().toLowerCase();

        if (cmd.equals("zone"))
        {
            if (zone != null)
            {
                arguments.error(String.format("Zone already set!"));
                return;
            }
            if (arguments.args.isEmpty())
            {
                arguments.error(String.format("Expected zone identifier."));
                return;
            }
            if (arguments.isTabCompletion && arguments.args.size() == 1)
            {
                arguments.tabCompletion = new ArrayList<>();
                for (Zone z : APIRegistry.perms.getZones())
                {
                    if (CommandBase.doesStringStartWith(arguments.args.peek(), z.getName()))
                        arguments.tabCompletion.add(z.getName());
                }
                return;
            }
            zone = getZoneByName(arguments.sender, arguments.args.remove());
            if (zone == null)
                return;
            parseGroupInner(arguments, group, zone);
            return;
        }

        // Set default zone
        if (zone == null)
            zone = APIRegistry.perms.getServerZone();

        switch (cmd)
        {
        // case "users":
        // listGroupUsers(group);
        // break;
        case "perms":
            arguments.confirm("Group " + group + " permissions:");
            listGroupPermissions(arguments.sender, group);
            break;
        case "users":
            listGroupUsers(arguments.sender, group);
            break;
        case "prefix":
            parseGroupPrefixSuffix(arguments, group, zone, false);
            break;
        case "suffix":
            parseGroupPrefixSuffix(arguments, group, zone, true);
            break;
        case "spawn":
            parseGroupSpawn(arguments, group, zone);
            break;
        case "priority":
            parseGroupPriority(arguments, group);
            break;
        case "parent":
            parseGroupInclude(arguments, group, true);
            break;
        case "include":
            parseGroupInclude(arguments, group, false);
            break;
        case "true":
        case "allow":
            parseGroupPermissions(arguments, group, zone, PermissionAction.ALLOW);
            break;
        case "false":
        case "deny":
            parseGroupPermissions(arguments, group, zone, PermissionAction.DENY);
            break;
        case "clear":
            parseGroupPermissions(arguments, group, zone, PermissionAction.CLEAR);
            break;
        case "value":
            parseGroupPermissions(arguments, group, zone, PermissionAction.VALUE);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    public static void parseGroupPrefixSuffix(CommandParserArgs arguments, String group, Zone zone, boolean isSuffix)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP_FIX))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        if (arguments.isTabCompletion)
            return;

        String fixName = isSuffix ? "suffix" : "prefix";
        if (arguments.args.isEmpty())
        {
            String fix = APIRegistry.perms.getServerZone().getGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (fix == null || fix.isEmpty())
                fix = "empty";
            arguments.confirm(String.format("%s's %s is %s", group, fixName, fix));
        }
        else
        {
            String fix = StringUtils.join(arguments.args, " ");
            if (fix.equalsIgnoreCase("clear"))
            {
                arguments.confirm(String.format("%s's %s cleared", group, fixName));
                APIRegistry.perms.getServerZone().clearGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            }
            else
            {
                arguments.confirm(String.format("%s's %s set to %s", group, fixName, fix));
                APIRegistry.perms.getServerZone().setGroupPermissionProperty(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
            }
        }
    }

    public static void parseGroupPermissions(CommandParserArgs arguments, String group, Zone zone, PermissionAction type)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP_PERMS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        if (arguments.args.isEmpty())
            throw new TranslatedCommandException("Missing permission argument!");

        // Apply permissions
        while (!arguments.args.isEmpty())
        {
            if (arguments.isTabCompletion && arguments.args.size() == 1)
            {
                if (type != PermissionAction.CLEAR)
                    arguments.tabCompletion = completePermission(arguments.args.peek());
                else
                    arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), zone
                            .getGroupPermissions(group).keySet());
                return;
            }

            String permissionNode = arguments.args.remove();
            String msg = null, value = null;
            if (type == PermissionAction.VALUE)
            {
                if (arguments.args.isEmpty())
                {
                    arguments.confirm(Translator.format("Value of %s = %s", permissionNode, zone.getGroupPermission(group, permissionNode)));
                    return;
                }
                value = StringUtils.join(arguments.args, ' ');
                arguments.args.clear();
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
                arguments.confirm(String.format("Set %s for group %s to %s in zone %s", permissionNode, group, value, zone.getName()));
                break;
            }
            if (msg != null)
                arguments.confirm(String.format(msg, group, permissionNode, zone.getName()));
            if (type == PermissionAction.VALUE)
                return;
        }
    }

    public static void parseGroupSpawn(CommandParserArgs arguments, String group, Zone zone)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP_SPAWN))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        if (arguments.args.isEmpty())
        {
            if (arguments.command.getCommandName().equalsIgnoreCase("setspawn"))
            {
                arguments.confirm("/setspawn here|clear|<x> <y> <z> <dim>: Set spawn location");
                arguments.confirm("/setspawn bed (enable|disable): Enable/disable spawning at bed");
            }
            else
            {
                arguments.confirm("/feperm group " + group + " spawn here|clear|<x> <y> <z> <dim>: Set spawn location");
                arguments.confirm("/feperm group " + group + " spawn bed (enable|disable): Enable/disable spawning at bed");
            }
            return;
        }
        if (arguments.isTabCompletion)
        {
            if (arguments.args.size() == 1)
                arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseSpawnArgs);
            return;
        }

        String loc = arguments.args.remove().toLowerCase();
        WorldPoint point = null;
        switch (loc)
        {
        case "here":
            if (arguments.senderPlayer == null)
                throw new TranslatedCommandException("[here] cannot be used from console.");
            point = new WorldPoint(arguments.senderPlayer);
            break;
        case "bed":
        {
            if (arguments.args.isEmpty())
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            String val = arguments.args.peek().toLowerCase();
            if (val.equals("true") | val.equals("enable"))
            {
                zone.setGroupPermission(group, FEPermissions.SPAWN_BED, true);
                arguments.confirm(String.format("Enabled bed-spawning for group %s in zone %s", group, zone.getName()));
            }
            else if (val.equals("false") | val.equals("disable"))
            {
                zone.setGroupPermission(group, FEPermissions.SPAWN_BED, false);
                arguments.confirm(String.format("Disabled bed-spawning for group %s in zone %s", group, zone.getName()));
            }
            arguments.confirm("Invalid argument. Use enable or disable.");
            return;
        }
        case "clear":
            break;
        default:
            if (arguments.args.size() < 3)
                throw new TranslatedCommandException("Too few arguments!");
            try
            {
                int x = CommandBase.parseInt(arguments.sender, loc);
                int y = CommandBase.parseInt(arguments.sender, arguments.args.remove());
                int z = CommandBase.parseInt(arguments.sender, arguments.args.remove());
                int dimension = CommandBase.parseInt(arguments.sender, arguments.args.remove());
                point = new WorldPoint(dimension, x, y, z);
            }
            catch (NumberFormatException e)
            {
                arguments.error("Invalid location argument");
                return;
            }
            break;
        }

        if (point == null)
        {
            zone.clearGroupPermission(group, FEPermissions.SPAWN_LOC);
            arguments.confirm(String.format("Cleared spawn-rule for group %s in zone %s", group, zone.getName()));
        }
        else
        {
            zone.setGroupPermissionProperty(group, FEPermissions.SPAWN_LOC, point.toString());
            arguments.confirm(String.format("Set spawn for group %s to %s in zone %s", group, point.toString(), zone.getName()));
        }
    }

    public static void parseGroupPriority(CommandParserArgs arguments, String group)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP_PERMS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        if (arguments.args.isEmpty())
        {
            arguments.confirm("Priority for group " + group + ": " + APIRegistry.perms.getGroupPermissionProperty(group, FEPermissions.GROUP_PRIORITY));
            return;
        }
        String priorityValue = arguments.args.remove();
        try
        {
            APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_PRIORITY, Integer.toString(Integer.parseInt(priorityValue)));
            arguments.confirm(String.format("Set priority for group %s to %s", group, priorityValue));
        }
        catch (NumberFormatException e)
        {
            arguments.error(String.format("The string %s is not a valid integer", priorityValue));
        }
    }

    public static void parseGroupInclude(CommandParserArgs arguments, String group, boolean isParent)
    {
        if (!arguments.isTabCompletion && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(arguments.sender), PERM_GROUP_PERMS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        if (arguments.isTabCompletion && arguments.args.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.args.peek(), parseGroupIncludeArgs);
            return;
        }

        final String displayName1 = (isParent ? " parent" : " include");

        // Get included groups
        Set<String> groups = isParent ? APIRegistry.perms.getServerZone().getParentedGroups(group) : APIRegistry.perms.getServerZone().getIncludedGroups(group);

        if (arguments.args.isEmpty())
        {
            // arguments.info("/feperm group " + group + " " + displayName1 + " add|remove <group>");
            // arguments.info("/feperm group " + group + " " + displayName1 + " clear");
            arguments.confirm(String.format((isParent ? "Parented" : "Included") + " groups for %s:", group));
            for (String includedGroup : groups)
                arguments.confirm("  " + includedGroup);
            return;
        }

        String cmd = arguments.args.remove().toLowerCase();
        if (cmd.equals("clear"))
        {
            APIRegistry.perms.getServerZone().clearGroupPermission(group, isParent ? FEPermissions.GROUP_PARENTS : FEPermissions.GROUP_INCLUDES);
            arguments.confirm(String.format("Cleared group-" + displayName1 + "s for group %s", group));
            return;
        }

        if (arguments.args.isEmpty())
        {
            arguments.error(FEPermissions.MSG_INVALID_SYNTAX);
            return;
        }

        // Auto-complete group name
        if (arguments.isTabCompletion)
        {
            if (arguments.args.size() == 1)
            {
                arguments.tabCompletion = new ArrayList<String>();
                for (String g : APIRegistry.perms.getServerZone().getGroups())
                {
                    if (CommandBase.doesStringStartWith(arguments.args.peek(), g))
                        arguments.tabCompletion.add(g);
                }
            }
            return;
        }

        String groupsName = arguments.args.remove();
        switch (cmd)
        {
        case "add":
            groups.add(groupsName);
            arguments.confirm(String.format("Added group-" + displayName1 + " of %s to group %s", groupsName, group));
            break;
        case "remove":
            groups.remove(groupsName);
            arguments.confirm(String.format("Removed group-" + displayName1 + " of %s to group %s", groupsName, group));
            break;
        default:
            arguments.error(FEPermissions.MSG_INVALID_SYNTAX);
            return;
        }

        APIRegistry.perms.setGroupPermissionProperty(group, isParent ? FEPermissions.GROUP_PARENTS : FEPermissions.GROUP_INCLUDES,
                StringUtils.join(groups, ","));
    }

    // ------------------------------------------------------------
    // -- Utils
    // ------------------------------------------------------------

    public static List<String> completePermission(String permission)
    {
        Set<String> result = new TreeSet<String>();
        for (String perm : APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions())
        {
            int nodeIndex = perm.indexOf('.', permission.length());
            if (nodeIndex >= 0)
                perm = perm.substring(0, nodeIndex);
            if (CommandBase.doesStringStartWith(permission, perm))
                result.add(perm);
        }

        return new ArrayList<String>(result);
    }

    public static Zone getZoneByName(ICommandSender sender, String zoneId)
    {
        try
        {
            int intId = Integer.parseInt(zoneId);
            if (intId < 1)
            {
                OutputHandler.chatError(sender, String.format("Zone ID must be greater than 0!"));
                return null;
            }

            Zone zone = APIRegistry.perms.getZoneById(intId);
            if (zone != null)
                return zone;

            OutputHandler.chatError(sender, String.format("No zone by the ID %s exists!", zoneId));
            return null;
        }
        catch (NumberFormatException e)
        {
            for (WorldZone wz : APIRegistry.perms.getServerZone().getWorldZones().values())
                if (wz.getName().equals(zoneId))
                    return wz;

            if (!(sender instanceof EntityPlayerMP))
            {
                OutputHandler.chatError(sender, Translator.translate("Cannot identify zones by name from console!"));
                return null;
            }

            Zone zone = APIRegistry.perms.getServerZone().getWorldZone(((EntityPlayerMP) sender).dimension).getAreaZone(zoneId);
            if (zone != null)
                return zone;

            OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", zoneId));
            return null;
        }
    }

    public static void listUserPermissions(ICommandSender sender, UserIdent ident, boolean showGroupPerms)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST_PERMS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        OutputHandler.chatNotification(sender, ident.getUsernameOrUUID() + " permissions:");

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
                    OutputHandler.chatWarning(sender, "Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                    printedZone = true;
                }
                OutputHandler.chatNotification(sender, "  " + perm.getKey() + " = " + perm.getValue());
            }
        }
        if (showGroupPerms)
        {
            for (GroupEntry group : APIRegistry.perms.getPlayerGroups(ident))
            {
                Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper.enumGroupPermissions(group.getGroup(), false);
                if (!groupPerms.isEmpty())
                {
                    boolean printedGroup = false;
                    for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet())
                    {
                        boolean printedZone = false;
                        for (Entry<String, String> perm : zone.getValue().entrySet())
                        {
                            if (perm.getKey().equals(FEPermissions.GROUP) || perm.getKey().equals(FEPermissions.GROUP_ID)
                                    || perm.getKey().equals(FEPermissions.GROUP_PRIORITY) || perm.getKey().equals(FEPermissions.PREFIX)
                                    || perm.getKey().equals(FEPermissions.SUFFIX))
                                continue;
                            if (!printedGroup)
                            {
                                OutputHandler.chatWarning(sender, "Group " + group);
                                printedGroup = true;
                            }
                            if (!printedZone)
                            {
                                OutputHandler.chatWarning(sender, "  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                                printedZone = true;
                            }
                            OutputHandler.chatNotification(sender, "    " + perm.getKey() + " = " + perm.getValue());
                        }
                    }
                }
            }
        }
    }

    public static void listGroupPermissions(ICommandSender sender, String group)
    {
        Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper.enumGroupPermissions(group, false);
        if (!groupPerms.isEmpty())
        {
            for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet())
            {
                boolean printedZone = false;
                for (Entry<String, String> perm : zone.getValue().entrySet())
                {
                    if (perm.getKey().equals(FEPermissions.GROUP) || perm.getKey().equals(FEPermissions.GROUP_ID)
                            || perm.getKey().equals(FEPermissions.GROUP_PRIORITY) || perm.getKey().equals(FEPermissions.PREFIX)
                            || perm.getKey().equals(FEPermissions.SUFFIX))
                        continue;
                    if (!printedZone)
                    {
                        OutputHandler.chatWarning(sender, "  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                        printedZone = true;
                    }
                    OutputHandler.chatNotification(sender, "    " + perm.getKey() + " = " + perm.getValue());
                }
            }
        }
    }

    public static void listZones(ICommandSender sender, WorldPoint location)
    {
        OutputHandler.chatNotification(sender, "Zones at position " + location.toString());
        for (Zone zone : APIRegistry.perms.getServerZone().getZonesAt(location))
        {
            if (zone.isHidden())
                continue;
            OutputHandler.chatNotification(sender, "  #" + zone.getId() + " " + zone.toString());
        }
    }

    public static void listGroups(ICommandSender sender)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST_GROUPS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        OutputHandler.chatNotification(sender, "Groups:");
        for (String group : APIRegistry.perms.getServerZone().getGroups())
            OutputHandler.chatNotification(sender, " - " + group);
    }

    public static void listUsers(ICommandSender sender)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST_USERS))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        OutputHandler.chatNotification(sender, "Known players:");
        for (UserIdent ident : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            OutputHandler.chatNotification(sender, " - " + ident.getUsernameOrUUID());
        }

        OutputHandler.chatNotification(sender, "Online players:");
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            OutputHandler.chatNotification(sender, " - " + ((EntityPlayerMP) player).getCommandSenderName());
        }
    }

    public static void listGroupUsers(ICommandSender sender, String group)
    {
        Set<UserIdent> players = ModulePermissions.permissionHelper.getServerZone().getGroupPlayers().get(group);
        OutputHandler.chatNotification(sender, "Players in group " + group + ":");
        if (players != null)
            for (UserIdent player : players)
                OutputHandler.chatNotification(sender, "  " + player.getUsernameOrUUID());
    }

}