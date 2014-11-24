package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

@SuppressWarnings("unchecked")
public class PermissionCommandParser {

    public static final String PERM = "fe.perm";
    public static final String PERM_ALL = PERM + ".*";
    public static final String PERM_TEST = PERM + ".test";
    public static final String PERM_RELOAD = PERM + ".reload";
    public static final String PERM_SAVE = PERM + ".save";

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

    enum PermissionAction {
        ALLOW, DENY, CLEAR, VALUE
    }

    private ICommandSender sender;
    private EntityPlayerMP senderPlayer;
    private Queue<String> args;
    private boolean tabCompleteMode = false;
    private List<String> tabComplete;

    public PermissionCommandParser(ICommandSender sender, String[] args, boolean tabCompleteMode)
    {
        this.sender = sender;
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.tabCompleteMode = tabCompleteMode;
        if (tabCompleteMode)
        {
            try
            {
                parseMain();
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            parseMain();
        }
    }

    public List<String> getTabCompleteList()
    {
        return tabComplete;
    }

    private void info(String message)
    {
        if (!tabCompleteMode)
            OutputHandler.chatConfirmation(sender, message);
    }

    private void warn(String message)
    {
        if (!tabCompleteMode)
            OutputHandler.chatWarning(sender, message);
    }

    private void error(String message)
    {
        if (!tabCompleteMode)
            OutputHandler.chatError(sender, message);
    }

    // Variables for auto-complete
    private static final String[] parseMainArgs = { "user", "group", "global", "list", "test", "testp", "reload", "save" }; // "export", "promote", "test" };
    private static final String[] parseListArgs = { "zones", "perms", "users", "groups" };
    private static final String[] parseUserArgs = { "zone", "group", "allow", "deny", "clear", "value", "true", "false", "spawn", "prefix", "suffix", "perms" };
    private static final String[] parseGroupArgs = { "zone", "allow", "deny", "clear", "value", "true", "false", "spawn", "prefix", "suffix", "perms",
            "priority", "include" };
    private static final String[] parseUserGroupArgs = { "add", "remove" };
    private static final String[] parseGroupIncludeArgs = { "add", "remove", "clear" };
    private static final String[] parseSpawnArgs = { "here", "clear", "bed" };

    private void parseMain()
    {
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseMainArgs);
            return;
        }
        if (args.isEmpty())
        {
            info("/feperm " + StringUtils.join(parseMainArgs, "|") + ": Displays help for the subcommands");
        }
        else
        {
            switch (args.remove().toLowerCase())
            {
            case "save":
                ModulePermissions.permissionHelper.save();
                info("Permissions saved!");
                break;
            case "reload":
                if (ModulePermissions.permissionHelper.load())
                    info("Successfully reloaded permissions");
                else
                    error("Error while reloading permissions");
                break;
            case "test":
                parseTest();
                break;
            case "testp":
                parseTestProperty();
                break;
            case "list":
                parseList();
                break;
            case "user":
                parseUser();
                break;
            case "group":
                parseGroup();
                break;
            case "global":
                parseGlobal();
                break;
            default:
                error("Unknown command argument");
                break;
            }
        }
    }

    // ------------------------------------------------------------
    // -- Listings
    // ------------------------------------------------------------

    private void parseList()
    {
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseListArgs);
            return;
        }
        if (args.isEmpty())
        {
            info("/feperm list " + StringUtils.join(parseListArgs, "|") + " : List the specified objects");
        }
        else
        {
            String arg = args.remove().toLowerCase();
            switch (arg)
            {
            case "zones":
                listZones();
                break;
            case "perms":
                listPermissions();
                break;
            case "users":
                listUsers();
                break;
            case "groups":
                listGroups();
                break;
            default:
                error("Unknown command argument");
                break;
            }
        }
    }

    private void listZones()
    {
        if (senderPlayer == null)
        {
            error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            return;
        }
        WorldPoint wp = new WorldPoint(senderPlayer);
        info("Zones at position " + wp.toString());
        for (Zone zone : APIRegistry.perms.getZonesAt(wp))
        {
            if (zone.isHidden())
                continue;
            info("  #" + zone.getId() + " " + zone.toString());
        }
    }

    private void listPermissions()
    {
        if (senderPlayer == null)
        {
            error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            return;
        }
        listUserPermissions(new UserIdent(senderPlayer), true);
    }

    private void listUserPermissions(UserIdent ident, boolean showGroupPerms)
    {
        if (tabCompleteMode)
            return;

        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST_PERMS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        info(ident.getUsernameOrUUID() + " permissions:");

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
                    warn("Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                    printedZone = true;
                }
                info("  " + perm.getKey() + " = " + perm.getValue());
            }
        }
        if (showGroupPerms)
        {
            for (String group : APIRegistry.perms.getPlayerGroups(ident))
            {
                Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper.enumGroupPermissions(group, false);
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
                                warn("Group " + group);
                                printedGroup = true;
                            }
                            if (!printedZone)
                            {
                                warn("  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                                printedZone = true;
                            }
                            info("    " + perm.getKey() + " = " + perm.getValue());
                        }
                    }
                }
            }
        }
    }

    private void listGroups()
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST_GROUPS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        info("Groups:");
        for (String group : APIRegistry.perms.getServerZone().getGroups())
        {
            info(" - " + group);
        }
    }

    private void listUsers()
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST_USERS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        info("Known players:");
        for (UserIdent ident : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            info(" - " + ident.getUsernameOrUUID());
        }
        info("Online players:");
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            if (player instanceof EntityPlayerMP)
                info(" - " + ((EntityPlayerMP) player).getCommandSenderName());
        }
    }

    private void parseTest()
    {
        if (args.isEmpty())
        {
            error("Missing permission argument!");
            return;
        }
        if (senderPlayer == null)
        {
            error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            return;
        }
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_TEST))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (tabCompleteMode)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseUserArgs);
            for (Zone zone : APIRegistry.perms.getZones())
            {
                if (CommandBase.doesStringStartWith(args.peek(), zone.getName()))
                    tabComplete.add(zone.getName());
            }
            for (String perm : ModulePermissions.permissionHelper.enumRegisteredPermissions())
            {
                if (CommandBase.doesStringStartWith(args.peek(), perm))
                    tabComplete.add(perm);
            }
            return;
        }

        String permissionNode = args.remove();
        if (APIRegistry.perms.checkPermission(senderPlayer, permissionNode))
        {
            info(permissionNode + " = true");
        }
        else
        {
            info(permissionNode + " = false");
        }
    }

    private void parseTestProperty()
    {
        if (args.isEmpty())
        {
            error("Missing permission argument!");
            return;
        }
        if (senderPlayer == null)
        {
            error(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            return;
        }
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_TEST))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (tabCompleteMode)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseUserArgs);
            for (Zone zone : APIRegistry.perms.getZones())
            {
                if (CommandBase.doesStringStartWith(args.peek(), zone.getName()))
                    tabComplete.add(zone.getName());
            }
            for (String perm : ModulePermissions.permissionHelper.enumRegisteredPermissions())
            {
                if (CommandBase.doesStringStartWith(args.peek(), perm))
                    tabComplete.add(perm);
            }
            return;
        }

        String permissionNode = args.remove();
        String result = APIRegistry.perms.getPermissionProperty(senderPlayer, permissionNode);
        if (result == null)
        {
            error("Permission-property does not exist");
        }
        else
        {
            info(permissionNode + " = " + result);
        }
    }

    private void listGroupPermissions(String group)
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
                        warn("  Zone #" + zone.getKey().getId() + " " + zone.getKey().toString());
                        printedZone = true;
                    }
                    info("    " + perm.getKey() + " = " + perm.getValue());
                }
            }
        }
    }

    // ------------------------------------------------------------
    // -- User
    // ------------------------------------------------------------

    private void parseUser()
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_USER))
        {
            error(FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (args.isEmpty())
        {
            info("Possible usage:");
            info("/p user <player> : Display user info");
            info("/p user <player> zone <zone> ... : Work with zones");
            info("/p user <player> perms : List player's permissions");
            info("/p user <player> group add|remove <group>: Player's group settings");
            info("/p user <player> allow|deny|clear <perms> : Set permissions");
            info("/p user <player> value <perm> <value> : Set permission property");
            info("/p user <player> spawn : Set player spawn");
            return;
        }

        // Auto-complete player name
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = new ArrayList<String>();
            for (UserIdent knownPlayerIdent : APIRegistry.perms.getServerZone().getKnownPlayers())
            {
                if (CommandBase.doesStringStartWith(args.peek(), knownPlayerIdent.getUsernameOrUUID()))
                    tabComplete.add(knownPlayerIdent.getUsernameOrUUID());
            }
            for (EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
            {
                if (CommandBase.doesStringStartWith(args.peek(), player.getGameProfile().getName()))
                    tabComplete.add(player.getGameProfile().getName());
            }
            return;
        }

        String playerName = args.remove();
        UserIdent ident;
        if (playerName.equalsIgnoreCase("_ME_"))
        {
            if (senderPlayer == null)
            {
                error("_ME_ cannot be used in console.");
                return;
            }
            ident = new UserIdent(senderPlayer);
        }
        else
        {
            ident = new UserIdent(playerName);
            if (!ident.hasUUID())
            {
                error(String.format("Player %s not found. playername will be used, but may be inaccurate.", ident.getUsername()));
            }
        }

        parseUserInner(ident, null);
    }

    private void parseUserInner(UserIdent ident, Zone zone)
    {
        // Display help or player info
        if (args.isEmpty())
        {
            if (zone == null)
            {
                info(String.format("Groups for player %s:", ident.getUsernameOrUUID()));
                for (String group : APIRegistry.perms.getPlayerGroups(ident))
                {
                    info("  " + group);
                }
                return;
            }
            // info("Possible usage:");
            // info("/p ... group add|remove <group>: Player's group settings");
            // info("/p ... allow|deny|clear <perms> : Set permissions");
            // info("/p ... value <perm> <value> : Set permission property");
            // info("/p ... spawn : Set player spawn");
            info(ident.getUsernameOrUUID() + "'s permissions in zone " + zone.getName() + ":");
            for (Entry<String, String> perm : zone.getPlayerPermissions(ident).entrySet())
            {
                info("  " + perm.getKey() + " = " + perm.getValue());
            }
            return;
        }

        // TAB-complete command
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseUserArgs);
            if (zone != null)
                tabComplete.remove("zone");
            return;
        }

        String cmd = args.remove().toLowerCase();

        if (cmd.equals("zone"))
        {
            if (zone != null)
            {
                error(String.format("Zone already set!"));
                return;
            }
            if (args.isEmpty())
            {
                error(String.format("Expected zone identifier."));
                return;
            }
            String zoneId = args.remove();
            try
            {
                int intId = Integer.parseInt(zoneId);
                if (intId < 1)
                {
                    error(String.format("Zone ID must be greater than 0!"));
                    return;
                }
                zone = APIRegistry.perms.getZoneById(intId);
                if (zone == null)
                {
                    error(String.format("No zone by the ID %s exists!", zoneId));
                    return;
                }
            }
            catch (NumberFormatException e)
            {
                if (senderPlayer == null)
                {
                    error("Cannot identify zones by name from console!");
                    return;
                }
                zone = APIRegistry.perms.getWorldZone(senderPlayer.dimension).getAreaZone(zoneId);
                if (zone == null)
                {
                    error(String.format("No zone by the name %s exists!", zoneId));
                    return;
                }
            }
            parseUserInner(ident, zone);
            return;
        }

        // Set default zone
        if (zone == null)
            zone = APIRegistry.perms.getServerZone();

        // Parse command
        switch (cmd)
        {
        case "group":
            parseUserGroup(ident);
            break;
        case "perms":
            listUserPermissions(ident, true);
            break;
        case "prefix":
            parseUserPrefixSuffix(ident, zone, false);
            break;
        case "suffix":
            parseUserPrefixSuffix(ident, zone, true);
            break;
        case "spawn":
            parseUserSpawn(ident, zone);
            break;
        case "true":
        case "allow":
            parseUserPermissions(ident, zone, PermissionAction.ALLOW);
            break;
        case "false":
        case "deny":
            parseUserPermissions(ident, zone, PermissionAction.DENY);
            break;
        case "clear":
            parseUserPermissions(ident, zone, PermissionAction.CLEAR);
            break;
        case "value":
            parseUserPermissions(ident, zone, PermissionAction.VALUE);
            break;
        default:
            throw new CommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    private void parseUserPrefixSuffix(UserIdent ident, Zone zone, boolean isSuffix)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_USER_FIX))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (tabCompleteMode)
            return;
        String fixName = isSuffix ? "suffix" : "prefix";
        if (args.isEmpty())
        {
            String fix = zone.getPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (fix == null || fix.isEmpty())
                fix = "empty";
            info(String.format("%s's %s is %s", ident.getUsernameOrUUID(), fixName, fix));
        }
        else
        {
            String fix = StringUtils.join(args, " ");
            if (fix.equalsIgnoreCase("clear"))
            {
                info(String.format("%s's %s cleared", ident.getUsernameOrUUID(), fixName));
                zone.clearPlayerPermission(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            }
            else
            {
                info(String.format("%s's %s set to %s", ident.getUsernameOrUUID(), fixName, fix));
                zone.setPlayerPermissionProperty(ident, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
            }
        }
    }

    private void parseUserPermissions(UserIdent ident, Zone zone, PermissionAction type)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_USER_PERMS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (args.isEmpty())
        {
            error("Missing permission argument!");
            return;
        }

        // Apply permissions
        while (!args.isEmpty())
        {
            if (tabCompleteMode && args.size() == 1)
            {
                if (type != PermissionAction.CLEAR)
                    tabComplete = completePermission(args.peek());
                else
                    tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), zone.getPlayerPermissions(ident).keySet());
                return;
            }

            String permissionNode = args.remove();
            String msg = null, value = null;
            if (type == PermissionAction.VALUE)
            {
                if (args.isEmpty())
                {
                    error("Need to specify value");
                    return;
                }
                value = StringUtils.join(args, ' ');
                args.clear();
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
                msg = "Cleared %s's acces to %s in zone %s";
                break;
            case VALUE:
                zone.setPlayerPermissionProperty(ident, permissionNode, value);
                info(String.format("Set %s for %s to %s in zone %s", permissionNode, ident.getUsernameOrUUID(), value, zone.getName()));
                break;
            }
            if (msg != null)
                info(String.format(msg, ident.getUsernameOrUUID(), permissionNode, zone.getName()));
        }
    }

    private void parseUserSpawn(UserIdent ident, Zone zone)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_USER_SPAWN))
        {
            throw new CommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        }
        if (args.isEmpty())
        {
            info("/feperm user " + ident.getUsernameOrUUID() + " spawn (here|bed|clear|<x> <y> <z> <dim>) [zone] : Set spawn");
            return;
        }
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseSpawnArgs);
            return;
        }

        String loc = args.remove().toLowerCase();
        WorldPoint point = null;
        boolean isBed = false;
        switch (loc)
        {
        case "here":
            point = new WorldPoint(senderPlayer);
            break;
        case "bed":
            isBed = true;
            break;
        case "clear":
            break;
        default:
            if (args.size() < 3)
                throw new CommandException("Too few arguments!");
            try
            {
                int x = CommandBase.parseInt(sender, loc);
                int y = CommandBase.parseInt(sender, args.remove());
                int z = CommandBase.parseInt(sender, args.remove());
                int dimension = CommandBase.parseInt(sender, args.remove());
                point = new WorldPoint(dimension, x, y, z);
            }
            catch (NumberFormatException e)
            {
                error("Invalid location argument");
                return;
            }
            break;
        }

        if (isBed)
        {
            zone.setPlayerPermissionProperty(ident, FEPermissions.SPAWN, "bed");
            info(String.format("Set spawn for user %s to be bed-location in zone %s", ident.getUsernameOrUUID(), zone.getName()));
        }
        else if (point == null)
        {
            zone.clearPlayerPermission(ident, FEPermissions.SPAWN);
            info(String.format("Cleared spawn-rule for user %s in zone %s", ident.getUsernameOrUUID(), zone.getName()));
        }
        else
        {
            zone.setPlayerPermissionProperty(ident, FEPermissions.SPAWN, point.toString());
            info(String.format("Set spawn for user %s to %s in zone %s", ident.getUsernameOrUUID(), point.toString(), zone.getName()));
        }
    }

    private void parseUserGroup(UserIdent ident)
    {
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseUserGroupArgs);
            return;
        }
        if (args.isEmpty())
        {
            info(String.format("Groups for player %s (without includes):", ident.getUsernameOrUUID()));
            for (String g : APIRegistry.perms.getStoredPlayerGroups(ident))
            {
                info("  " + g);
            }
        }
        else
        {
            String mode = args.remove().toLowerCase();
            if (!mode.equals("add") && !mode.equals("remove") && !mode.equals("set"))
            {
                error("Syntax error. Please try this instead:");
                error("/p user <player> group add|set|remove <GROUP>");
                return;
            }

            if (tabCompleteMode && args.size() == 1)
            {
                tabComplete = new ArrayList<String>();
                for (String group : APIRegistry.perms.getServerZone().getGroups())
                {
                    if (CommandBase.doesStringStartWith(args.peek(), group))
                        tabComplete.add(group);
                }
                return;
            }
            if (args.isEmpty())
            {
                error("Usage: /p user <player> group " + mode + " <group-name>");
            }
            else
            {
                String group = args.remove();
                if (!APIRegistry.perms.groupExists(group))
                {
                    error(String.format("Group %s not found.", group));
                    return;
                }
                switch (mode)
                {
                case "add":
                    APIRegistry.perms.addPlayerToGroup(ident, group);
                    info(String.format("Player %s added to group %s", ident.getUsernameOrUUID(), group));
                    break;
                case "remove":
                    APIRegistry.perms.removePlayerFromGroup(ident, group);
                    info(String.format("Player %s removed from group %s", ident.getUsernameOrUUID(), group));
                    break;
                case "set":
                    for (String g : APIRegistry.perms.getStoredPlayerGroups(ident))
                    {
                        APIRegistry.perms.removePlayerFromGroup(ident, g);
                    }
                    APIRegistry.perms.addPlayerToGroup(ident, group);
                    info(String.format("Set %s's group to %s", ident.getUsernameOrUUID(), group));
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------
    // -- Group
    // ------------------------------------------------------------

    private void parseGroup()
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (args.isEmpty())
        {
            info("Possible usage:");
            info("/p group <group> : Display group info");
            info("/p group <group> zone <zone> ... : Work with zones");
            info("/p group <group> create : Create a new group");
            info("/p group <group> perms : List group's permissions");
            info("/p group <group> allow|deny|clear <perms> : Set permissions");
            info("/p group <group> value <perm> <value> : Set permission property");
            info("/p group <group> spawn : Set group spawn");
            return;
        }

        // Auto-complete group name
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = new ArrayList<String>();
            for (String group : APIRegistry.perms.getServerZone().getGroups())
            {
                if (CommandBase.doesStringStartWith(args.peek(), group))
                    tabComplete.add(group);
            }
            return;
        }

        String group = args.remove();
        if (!APIRegistry.perms.groupExists(group))
        {
            if (tabCompleteMode && args.size() == 1)
            {
                tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), "create");
                return;
            }
            if (args.isEmpty())
            {
                info(String.format("Group %s does not exist", group));
            }
            else
            {
                String groupArg = args.remove();
                if (groupArg.equalsIgnoreCase("create"))
                {
                    APIRegistry.perms.createGroup(group);
                    info(String.format("Created group %s", group));
                }
                else
                {
                    error(String.format("Group %s does not exist", group));
                }
            }
            return;
        }

        parseGroupInner(group, null);
    }

    private void parseGlobal()
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        // if (args.isEmpty())
        // {
        // info("Possible usage:");
        // info("/p global : Display global-group info");
        // info("/p global zone <zone> ... : Work with zones");
        // info("/p global perms : List global permissions");
        // info("/p global allow|deny|clear <perms> : Set permissions");
        // info("/p global value <perm> <value> : Set permission property");
        // info("/p global spawn : Set global spawn");
        // return;
        // }

        parseGroupInner(IPermissionsHelper.GROUP_DEFAULT, null);
    }

    private void parseGroupInner(String group, Zone zone)
    {
        // Display help or player info
        if (args.isEmpty())
        {
            if (zone == null)
            {
                info("Group " + group + " permissions:");
                listGroupPermissions(group);
                return;
            }
            if (zone.getGroupPermissions(group) == null)
            {
                info("Group " + group + " has no permissions in zone " + zone.getName() + ".");
            }
            else
            {
                info("Group " + group + " permissions in zone " + zone.getName() + ":");
                for (Entry<String, String> perm : zone.getGroupPermissions(group).entrySet())
                {
                    info("  " + perm.getKey() + " = " + perm.getValue());
                }
            }
            return;
        }

        // TAB-complete command
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseGroupArgs);
            if (zone != null)
                tabComplete.remove("zone");
            return;
        }

        String cmd = args.remove().toLowerCase();

        if (cmd.equals("zone"))
        {
            if (zone != null)
            {
                error(String.format("Zone already set!"));
                return;
            }
            if (args.isEmpty())
            {
                error(String.format("Expected zone identifier."));
                return;
            }
            if (tabCompleteMode && args.size() == 1)
            {
                tabComplete = new ArrayList<>();
                for (Zone z : APIRegistry.perms.getZones())
                {
                    if (CommandBase.doesStringStartWith(args.peek(), z.getName()))
                        tabComplete.add(z.getName());
                }
                return;
            }
            String zoneId = args.remove();
            try
            {
                int intId = Integer.parseInt(zoneId);
                if (intId < 1)
                {
                    error(String.format("Zone ID must be greater than 0!"));
                    return;
                }
                zone = APIRegistry.perms.getZoneById(intId);
                if (zone == null)
                {
                    error(String.format("No zone by the ID %s exists!", zoneId));
                    return;
                }
            }
            catch (NumberFormatException e)
            {
                if (senderPlayer == null)
                {
                    error("Cannot identify zones by name from console!");
                    return;
                }
                zone = APIRegistry.perms.getWorldZone(senderPlayer.dimension).getAreaZone(zoneId);
                if (zone == null)
                {
                    error(String.format("No zone by the name %s exists!", zoneId));
                    return;
                }
            }
            parseGroupInner(group, zone);
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
            info("Group " + group + " permissions:");
            listGroupPermissions(group);
            break;
        case "prefix":
            parseGroupPrefixSuffix(group, zone, false);
            break;
        case "suffix":
            parseGroupPrefixSuffix(group, zone, true);
            break;
        case "spawn":
            parseGroupSpawn(group, zone);
            break;
        case "priority":
            parseGroupPriority(group);
            break;
        case "include":
            parseGroupInclude(group);
            break;
        case "true":
        case "allow":
            parseGroupPermissions(group, zone, PermissionAction.ALLOW);
            break;
        case "false":
        case "deny":
            parseGroupPermissions(group, zone, PermissionAction.DENY);
            break;
        case "clear":
            parseGroupPermissions(group, zone, PermissionAction.CLEAR);
            break;
        case "value":
            parseGroupPermissions(group, zone, PermissionAction.VALUE);
            break;
        default:
            throw new CommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    private void parseGroupPrefixSuffix(String group, Zone zone, boolean isSuffix)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP_FIX))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (tabCompleteMode)
            return;
        String fixName = isSuffix ? "suffix" : "prefix";
        if (args.isEmpty())
        {
            String fix = APIRegistry.perms.getServerZone().getGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (fix == null || fix.isEmpty())
                fix = "empty";
            info(String.format("%s's %s is %s", group, fixName, fix));
        }
        else
        {
            String fix = StringUtils.join(args, " ");
            if (fix.equalsIgnoreCase("clear"))
            {
                info(String.format("%s's %s cleared", group, fixName));
                APIRegistry.perms.getServerZone().clearGroupPermission(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            }
            else
            {
                info(String.format("%s's %s set to %s", group, fixName, fix));
                APIRegistry.perms.getServerZone().setGroupPermissionProperty(group, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX, fix);
            }
        }
    }

    private void parseGroupPermissions(String group, Zone zone, PermissionAction type)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP_PERMS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (args.isEmpty())
        {
            error("Missing permission argument!");
            return;
        }

        // Apply permissions
        while (!args.isEmpty())
        {
            if (tabCompleteMode && args.size() == 1)
            {
                if (type != PermissionAction.CLEAR)
                    tabComplete = completePermission(args.peek());
                else
                    tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), zone.getGroupPermissions(group).keySet());
                return;
            }

            String permissionNode = args.remove();
            String msg = null, value = null;
            if (type == PermissionAction.VALUE)
            {
                if (args.isEmpty())
                {
                    error("Need to specify value");
                    return;
                }
                value = StringUtils.join(args, ' ');
                args.clear();
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
                msg = "Cleared %s's acces to %s in zone %s";
                break;
            case VALUE:
                zone.setGroupPermissionProperty(group, permissionNode, value);
                info(String.format("Set %s for group %s to %s in zone %s", permissionNode, group, value, zone.getName()));
                break;
            }
            if (msg != null)
                info(String.format(msg, group, permissionNode, zone.getName()));
        }
    }

    private void parseGroupSpawn(String group, Zone zone)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP_SPAWN))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (args.isEmpty())
        {
            info("/feperm group " + group + " spawn (here|bed|clear|<x> <y> <z> <dim>) [zone] : Set spawn");
            return;
        }
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseSpawnArgs);
            return;
        }

        String loc = args.remove().toLowerCase();
        WorldPoint point = null;
        boolean isBed = false;
        switch (loc)
        {
        case "here":
            point = new WorldPoint(senderPlayer);
            break;
        case "bed":
            isBed = true;
            break;
        case "clear":
            break;
        default:
            if (args.size() < 3)
                throw new CommandException("Too few arguments!");
            try
            {
                int x = CommandBase.parseInt(sender, loc);
                int y = CommandBase.parseInt(sender, args.remove());
                int z = CommandBase.parseInt(sender, args.remove());
                int dimension = CommandBase.parseInt(sender, args.remove());
                point = new WorldPoint(dimension, x, y, z);
            }
            catch (NumberFormatException e)
            {
                error("Invalid location argument");
                return;
            }
            break;
        }

        if (isBed)
        {
            zone.setGroupPermissionProperty(group, FEPermissions.SPAWN, "bed");
            info(String.format("Set spawn for group %s to be bed-location in zone %s", group, zone.getName()));
        }
        else if (point == null)
        {
            zone.clearGroupPermission(group, FEPermissions.SPAWN);
            info(String.format("Cleared spawn-rule for group %s in zone %s", group, zone.getName()));
        }
        else
        {
            zone.setGroupPermissionProperty(group, FEPermissions.SPAWN, point.toString());
            info(String.format("Set spawn for group %s to %s in zone %s", group, point.toString(), zone.getName()));
        }
    }

    private void parseGroupPriority(String group)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP_PERMS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (args.isEmpty())
        {
            info("/feperm group " + group + " priority <prio> : Set group priority");
        }
        String priorityValue = args.remove();
        try
        {
            APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_PRIORITY, Integer.toString(Integer.parseInt(priorityValue)));
            info(String.format("Set priority for group %s to %s", group, priorityValue));
        }
        catch (NumberFormatException e)
        {
            error(String.format("The string %s is not a valid integer", priorityValue));
        }
    }

    private void parseGroupInclude(String group)
    {
        if (!tabCompleteMode && !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_GROUP_PERMS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseGroupIncludeArgs);
            return;
        }

        // Get included groups
        String includedGroupsStr = APIRegistry.perms.getGroupPermissionProperty(group, FEPermissions.GROUP_INCLUDES);
        Set<String> includedGroups = new HashSet<String>();
        if (includedGroupsStr != null)
            for (String includedGroup : includedGroupsStr.split(","))
                if (!includedGroup.isEmpty())
                    includedGroups.add(includedGroup);

        if (args.isEmpty())
        {
            info("/feperm group " + group + " include add|remove <group>");
            info("/feperm group " + group + " include clear");
            info(String.format("Included groups for %s:", group));
            for (String includedGroup : includedGroups)
                info("  " + includedGroup);
            return;
        }

        String cmd = args.remove().toLowerCase();
        if (cmd.equals("clear"))
        {
            APIRegistry.perms.getServerZone().clearGroupPermission(group, FEPermissions.GROUP_INCLUDES);
            info(String.format("Cleared group-includes for group %s", group));
            return;
        }

        if (args.isEmpty())
        {
            error(FEPermissions.MSG_INVALID_SYNTAX);
            return;
        }

        // Auto-complete group name
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = new ArrayList<String>();
            for (String g : APIRegistry.perms.getServerZone().getGroups())
            {
                if (CommandBase.doesStringStartWith(args.peek(), g))
                    tabComplete.add(g);
            }
            return;
        }

        String includeGroup = args.remove();

        switch (cmd)
        {
        case "add":
            includedGroups.add(includeGroup);
            info(String.format("Added group-include of %s to group %s", includeGroup, group));
            break;
        case "remove":
            includedGroups.remove(includeGroup);
            info(String.format("Removed group-include of %s to group %s", includeGroup, group));
            break;
        default:
            error(FEPermissions.MSG_INVALID_SYNTAX);
            return;
        }

        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_INCLUDES, StringUtils.join(includedGroups, ","));
    }

    // ------------------------------------------------------------
    // -- Utils
    // ------------------------------------------------------------

    private static List<String> completePermission(String permission)
    {
        Set<String> perms = new TreeSet<String>();
        for (String perm : ModulePermissions.permissionHelper.enumRegisteredPermissions())
        {
            int nodeIndex = perm.indexOf('.', permission.length());
            if (nodeIndex >= 0)
                perm = perm.substring(0, nodeIndex);
            if (CommandBase.doesStringStartWith(permission, perm))
                perms.add(perm);
        }

        return new ArrayList<String>(perms);
    }

}
