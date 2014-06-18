package com.forgeessentials.permission.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.AreaSelector.WorldPoint;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;

public class CommandFEPermGroup {
    public static void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0) // display syntax & possible options for this
        // level
        {
            // Make list
            OutputHandler.chatConfirmation(sender, "Possible usage:");
            OutputHandler.chatConfirmation(sender, "/p group create|delete");
            OutputHandler.chatConfirmation(sender, "/p group <groupName>");
            OutputHandler.chatConfirmation(sender, "/p group list ");
            // OutputHandler.chatError(sender,
            // "Improper syntax. Please try this instead: " + "");
            return;
        }

		/*
         * Create / remove part\ /p group create <groupName>
		 */
        if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("make"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "Usage: /p group create <groupname>");
                return;
            }
            Zone zone = APIRegistry.zones.getGLOBAL();
            if (args.length > 2)
            {

                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else if (args[2].equalsIgnoreCase("here"))
                {
                    zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
            APIRegistry.perms.createGroupInZone(args[1], zone.getZoneName(), "", "", null, 0);
            ChatUtils.sendMessage(sender, "Group " + args[1] + " made in zone " + zone.getZoneName());
            return;
        }
        if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "Usage: /p group delete <groupname>");
                return;
            }
            if (APIRegistry.perms.getGroupForName(args[1]) == null)
            {
                OutputHandler.chatError(sender, args[1] + " does not exist as a group!");
                return;
            }
            Zone zone = APIRegistry.zones.getGLOBAL();
            if (args.length == 3)
            {
                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else if (args[2].equalsIgnoreCase("here"))
                {
                    zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
            // Remove zone
            ArrayList<?> groups = APIRegistry.perms.getGroupsInZone(zone.getZoneName());
            for (Object groupObj : groups)
            {
                Group group = (Group) groupObj;
                if (group.name.equalsIgnoreCase(args[1]))
                {
                    APIRegistry.perms.deleteGroupInZone(args[1], zone.getZoneName());
                    OutputHandler.chatConfirmation(sender, "Group " + args[1] + " removed in zone " + zone.getZoneName());
                    return;
                }
            }
            OutputHandler.chatError(sender, args[1] + " does not exist in " + zone.getZoneName() + " zone!");
            return;
        }
        if (args[0].equalsIgnoreCase("list"))
        {
            // list the current groups: by zone? in priority order?
            Zone zone = APIRegistry.zones.getGLOBAL();
            if (args.length == 2)
            {
                if (APIRegistry.zones.doesZoneExist(args[1]))
                {
                    zone = APIRegistry.zones.getZone(args[1]);
                }
                else if (args[1].equalsIgnoreCase("here"))
                {
                    zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
            ArrayList<?> list = APIRegistry.perms.getGroupsInZone(zone.getZoneName());
            String groups = "";
            int i = 0;
            for (Object groupObj : list)
            {
                groups += ((Group) groupObj).name;
                i++;
                if (i != list.size())
                {
                    groups += ", ";
                }
            }
            OutputHandler.chatConfirmation(sender, "Groups available in zone " + zone.getZoneName() + ":");
            OutputHandler.chatConfirmation(sender, groups);
            return;
        }

        Group group = APIRegistry.perms.getGroupForName(args[0]);
        if (group == null)
        {
            OutputHandler.chatError(sender, args[0] + " does not exist as a group!");
            return;
        }
        if (args.length == 1) // display group-specific settings and their
        // values for this group
        {
            OutputHandler.chatConfirmation(sender,
                    "Current settings for " + group.name + ": prefix=" + group.prefix + ", suffix=" + group.suffix + ", parent=" + group.parent + ", priority="
                            + group.priority);
            OutputHandler.chatConfirmation(sender, "To change any of these, type /p group <groupname> prefix|suffix|parent|priority set <value>");
            return;
        }
		/*
		 * Settings
		 */
        Zone zone = APIRegistry.zones.getGLOBAL();
        if (args.length == 3 && (args[1].equalsIgnoreCase("prefix") || args[1].equalsIgnoreCase("suffix") || args[1].equalsIgnoreCase("parent") || args[1]
                .equalsIgnoreCase("priority")))
        {
            if (args.length == 3)
            {
                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else if (args[2].equalsIgnoreCase("here"))
                {
                    zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
        }
        else if (args.length == 5)
        {
            if (APIRegistry.zones.doesZoneExist(args[4]))
            {
                zone = APIRegistry.zones.getZone(args[4]);
            }
            else if (args[4].equalsIgnoreCase("here"))
            {
                zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
            }
        }
        if (args[1].equalsIgnoreCase("prefix"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                OutputHandler.chatConfirmation(sender, group.name + "'s prefix is &f" + group.prefix);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.prefix = " ";
                }
                else
                {
                    group.prefix = args[3];
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    OutputHandler.chatConfirmation(sender, group.name + "'s prefix set to &f" + group.prefix);
                }
                else
                {
                    OutputHandler.chatError(sender, "Error processing group prefix update.");
                }
                return;
            }
        }
        if (args[1].equalsIgnoreCase("suffix"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                OutputHandler.chatConfirmation(sender, group.name + "'s suffix is &f" + group.suffix);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.suffix = " ";
                }
                else
                {
                    group.suffix = args[3];
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    OutputHandler.chatConfirmation(sender, group.name + "'s suffix set to &f" + group.suffix);
                }
                else
                {
                    OutputHandler.chatError(sender, "Error processing group suffix update.");
                }
                return;
            }
        }
		/*
		 * Parent part
		 */
        if (args[1].equalsIgnoreCase("parent"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                OutputHandler.chatConfirmation(sender, group.name + "'s parent is " + group.parent);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.parent = null;
                }
                else
                {
                    group.parent = args[3];
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    OutputHandler.chatConfirmation(sender, group.name + "'s parent set to " + group.parent);
                }
                else
                {
                    OutputHandler.chatError(sender, "Error processing group parent update.");
                }
                return;
            }
        }
		/*
		 * Priority part
		 */
        if (args[1].equalsIgnoreCase("priority"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                OutputHandler.chatConfirmation(sender, group.name + "'s priority is " + group.priority);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.priority = 0;
                }
                else
                {
                    try
                    {
                        group.priority = Integer.parseInt(args[3]);
                    }
                    catch (NumberFormatException e)
                    {
                        OutputHandler.chatError(sender, args[3] + "");
                    }
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    OutputHandler.chatConfirmation(sender, group.name + "'s priority set to " + group.priority);
                }
                else
                {
                    OutputHandler.chatError(sender, "Error processing group priority update.");
                }
                return;
            }
        }
		/*
		 * Permissions part
		 */
        zone = APIRegistry.zones.getGLOBAL();
        if (args.length == 4)
        {
            if (APIRegistry.zones.doesZoneExist(args[3]))
            {
                zone = APIRegistry.zones.getZone(args[3]);
            }
            else if (args[3].equalsIgnoreCase("here"))
            {
                zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[4]));
            }
        }
        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow"))
        {
            String result = APIRegistry.perms.setGroupPermission(group.name, args[2], true, zone.getZoneName());
            if (result == null)
            {
                OutputHandler.chatConfirmation(sender, group.name + " in zone " + zone.getZoneName() + " allowed access to " + args[2]);
            }
            else
            {
                OutputHandler.chatError(sender, result);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny"))
        {
            String result = APIRegistry.perms.setGroupPermission(group.name, args[2], false, zone.getZoneName());
            if (result == null)
            {
                OutputHandler.chatConfirmation(sender, group.name + " in zone " + zone.getZoneName() + " denied access to " + args[2]);
            }
            else
            {
                OutputHandler.chatError(sender, result);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))
        {
            String result = APIRegistry.perms.clearGroupPermission(group.name, args[2], zone.getZoneName());
            if (result == null)
            {
                OutputHandler.chatConfirmation(sender, args[2] + " has been removed from " + group.name + " in zone " + zone.getZoneName());
            }
            else
            {
                OutputHandler.chatError(sender, result);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("get"))
        {
            String result = APIRegistry.perms.getPermissionForGroup(group.name, zone.getZoneName(), args[2]);
            if (result == null)
            {
                OutputHandler.chatError(sender, "Error processing statement");
            }
            else if (result.equals("Zone or target invalid"))
            {
                OutputHandler.chatError(sender, "Zone or group does not exist!");
            }
            else
            {
                OutputHandler.chatConfirmation(sender, args[2] + " is " + result + " for " + group.name);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("perms"))
        {
            if (args.length == 3)
            {
                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else if (args[2].equalsIgnoreCase("here"))
                {
                    zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(sender));
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[4]));
                }
            }

            ArrayList<String> list = APIRegistry.perms.getGroupPermissions(group.name, zone.getZoneName());
            Collections.sort(list);
            ArrayList<String> messageAllowed = new ArrayList<String>();
            ArrayList<String> messageDenied = new ArrayList<String>();
            for (String perm : list)
            {
                if (perm.contains("has no individual permissions."))
                {
                    OutputHandler.chatConfirmation(sender, perm);
                    return;
                }
                if (perm.toLowerCase().contains("allow") || perm.toLowerCase().contains("true"))
                {
                    messageAllowed.add(" " + EnumChatFormatting.DARK_GREEN + perm.substring(0, perm.indexOf(":")));
                }
                else
                {
                    messageDenied.add(" " + EnumChatFormatting.DARK_RED + perm.substring(0, perm.indexOf(":")));
                }
            }
            OutputHandler.chatConfirmation(sender,
                    group.name + (group.parent != null ? " inherits from " + group.parent : "") + ".\nCurrent permissions in zone " + zone.getZoneName() + ":");
            OutputHandler.chatConfirmation(sender,
                    " (" + EnumChatFormatting.DARK_GREEN + "ALLOWED" + EnumChatFormatting.DARK_RED + " DENIED" + EnumChatFormatting.GREEN + ")");
            for (String perm : messageAllowed)
            {
                OutputHandler.chatConfirmation(sender, perm);
            }
            for (String perm : messageDenied)
            {
                OutputHandler.chatConfirmation(sender, perm);
            }
            return;
        }

        OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + "");
    }

    public static void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0) // display syntax & possible options for this
        // level
        {
            // Make list
            ChatUtils.sendMessage(sender, "Possible usage:");
            ChatUtils.sendMessage(sender, "/p group create|delete");
            ChatUtils.sendMessage(sender, "/p group <groupName>");
            ChatUtils.sendMessage(sender, "/p group list ");
            // sender.sendChatToPlayer("Improper syntax. Please try this instead: "
            // + "");
            return;
        }

		/*
		 * Create / remove part\ /p group create <groupName>
		 */
        if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("make"))
        {
            if (args.length == 1)
            {
                ChatUtils.sendMessage(sender, "Usage: /p group create <groupname>");
                return;
            }
            Zone zone = APIRegistry.zones.getGLOBAL();
            if (args.length > 2)
            {

                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else
                {
                    ChatUtils.sendMessage(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
            APIRegistry.perms.createGroupInZone(args[1], zone.getZoneName(), "", "", null, 0);
            ChatUtils.sendMessage(sender, "Group " + args[1] + " made in zone " + zone.getZoneName());
            return;
        }
        if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove"))
        {
            if (args.length == 1)
            {
                ChatUtils.sendMessage(sender, "Usage: /p group delete <groupname>");
                return;
            }
            if (APIRegistry.perms.getGroupForName(args[1]) == null)
            {
                ChatUtils.sendMessage(sender, args[0] + " does not exist as a group!");
                return;
            }
            Zone zone = APIRegistry.zones.getGLOBAL();
            if (args.length == 3)
            {
                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else
                {
                    ChatUtils.sendMessage(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
            // Remove zone
            APIRegistry.perms.deleteGroupInZone(args[1], zone.getZoneName());
            ChatUtils.sendMessage(sender, "Group " + args[1] + " removed in zone " + zone.getZoneName());
            return;
        }
        if (args[0].equalsIgnoreCase("list"))
        {
            // list the current groups: by zone? in priority order?
            Zone zone = APIRegistry.zones.getGLOBAL();
            if (args.length == 2)
            {
                if (APIRegistry.zones.doesZoneExist(args[1]))
                {
                    zone = APIRegistry.zones.getZone(args[1]);
                }
                else
                {
                    ChatUtils.sendMessage(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
            ArrayList<?> list = APIRegistry.perms.getGroupsInZone(zone.getZoneName());
            String groups = "";
            int i = 0;
            for (Object groupObj : list)
            {
                groups += ((Group) groupObj).name;
                i++;
                if (i != list.size())
                {
                    groups += ", ";
                }
            }
            ChatUtils.sendMessage(sender, "Groups available in zone " + zone.getZoneName() + ":");
            ChatUtils.sendMessage(sender, groups);
            return;
        }

        Group group = APIRegistry.perms.getGroupForName(args[0]);
        if (group == null)
        {
            ChatUtils.sendMessage(sender, args[0] + " does not exist as a group!");
            return;
        }
        if (args.length == 1) // display group-specific settings and their
        // values for this group
        {
            ChatUtils.sendMessage(sender,
                    "Current settings for " + group.name + ": prefix=" + group.prefix + ", suffix=" + group.suffix + ", parent=" + group.parent + ", priority="
                            + group.priority);
            ChatUtils.sendMessage(sender, "To change any of these, type /p group <groupname> prefix|suffix|parent|priority set <value>");
            return;
        }
		/*
		 * Settings
		 */
        Zone zone = APIRegistry.zones.getGLOBAL();
        if (args.length == 3 && (args[1].equalsIgnoreCase("prefix") || args[1].equalsIgnoreCase("suffix") || args[1].equalsIgnoreCase("parent") || args[1]
                .equalsIgnoreCase("priority")))
        {
            if (args.length == 3)
            {
                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else
                {
                    ChatUtils.sendMessage(sender, String.format("No zone by the name %s exists!", args[2]));
                }
            }
        }
        else if (args.length == 5)
        {
            if (APIRegistry.zones.doesZoneExist(args[4]))
            {
                zone = APIRegistry.zones.getZone(args[4]);
            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("No zone by the name %s exists!", args[2]));
            }
        }
        if (args[1].equalsIgnoreCase("prefix"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                ChatUtils.sendMessage(sender, group.name + "'s prefix is &f" + group.prefix);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.prefix = " ";
                }
                else
                {
                    group.prefix = args[3];
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    ChatUtils.sendMessage(sender, group.name + "'s prefix set to &f" + group.prefix);
                }
                else
                {
                    ChatUtils.sendMessage(sender, "Error processing group prefix update.");
                }
                return;
            }
        }
        if (args[1].equalsIgnoreCase("suffix"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                ChatUtils.sendMessage(sender, group.name + "'s suffix is &f" + group.suffix);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.suffix = " ";
                }
                else
                {
                    group.suffix = args[3];
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    ChatUtils.sendMessage(sender, group.name + "'s suffix set to &f" + group.suffix);
                }
                else
                {
                    ChatUtils.sendMessage(sender, "Error processing group suffix update.");
                }
                return;
            }
        }
		/*
		 * Parent part
		 */
        if (args[1].equalsIgnoreCase("parent"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                ChatUtils.sendMessage(sender, group.name + "'s parent is " + group.parent);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.parent = null;
                }
                else
                {
                    group.parent = args[3];
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    ChatUtils.sendMessage(sender, group.name + "'s parent set to " + group.parent);
                }
                else
                {
                    ChatUtils.sendMessage(sender, "Error processing group parent update.");
                }
                return;
            }
        }
		/*
		 * Priority part
		 */
        if (args[1].equalsIgnoreCase("priority"))
        {
            if (args.length == 2 || !args[2].equalsIgnoreCase("set"))
            {
                ChatUtils.sendMessage(sender, group.name + "'s priority is " + group.priority);
                return;
            }
            else
            // args[2] must contian "set"
            {
                if (args.length == 3)
                {
                    group.priority = 0;
                }
                else
                {
                    try
                    {
                        group.priority = Integer.parseInt(args[3]);
                    }
                    catch (NumberFormatException e)
                    {
                        ChatUtils.sendMessage(sender, args[3] + "");
                    }
                }
                boolean result = APIRegistry.perms.updateGroup(group);
                if (result)
                {
                    ChatUtils.sendMessage(sender, group.name + "'s priority set to " + group.priority);
                }
                else
                {
                    ChatUtils.sendMessage(sender, "Error processing group priority update.");
                }
                return;
            }
        }
		/*
		 * Permissions part
		 */
        zone = APIRegistry.zones.getGLOBAL();
        if (args.length == 4)
        {
            if (APIRegistry.zones.doesZoneExist(args[3]))
            {
                zone = APIRegistry.zones.getZone(args[3]);
            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("No zone by the name %s exists!", args[4]));
            }
        }
        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow"))
        {
            String result = APIRegistry.perms.setGroupPermission(group.name, args[2], true, zone.getZoneName());
            if (result == null)
            {
                ChatUtils.sendMessage(sender, group.name + " in zone " + zone.getZoneName() + " allowed access to " + args[2]);
            }
            else
            {
                ChatUtils.sendMessage(sender, result);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny"))
        {
            String result = APIRegistry.perms.setGroupPermission(group.name, args[2], false, zone.getZoneName());
            if (result == null)
            {
                ChatUtils.sendMessage(sender, group.name + " in zone " + zone.getZoneName() + " denied access to " + args[2]);
            }
            else
            {
                ChatUtils.sendMessage(sender, result);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))
        {
            String result = APIRegistry.perms.clearGroupPermission(group.name, args[2], zone.getZoneName());
            if (result == null)
            {
                ChatUtils.sendMessage(sender, args[2] + " has been removed from " + group.name + " in zone " + zone.getZoneName());
            }
            else
            {
                ChatUtils.sendMessage(sender, result);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("get"))
        {
            String result = APIRegistry.perms.getPermissionForGroup(group.name, zone.getZoneName(), args[2]);
            if (result == null)
            {
                ChatUtils.sendMessage(sender, "Error processing statement");
            }
            else if (result.equals("Zone or target invalid"))
            {
                ChatUtils.sendMessage(sender, "Zone or group does not exist!");
            }
            else
            {
                ChatUtils.sendMessage(sender, args[2] + " is " + result + " for " + group.name);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("perms"))
        {
            if (args.length == 3)
            {
                if (APIRegistry.zones.doesZoneExist(args[2]))
                {
                    zone = APIRegistry.zones.getZone(args[2]);
                }
                else if (args[2].equalsIgnoreCase("here"))
                {
                    ChatUtils.sendMessage(sender, "ERROR: You are not allowed to use the here keyword in console.");
                }
                else
                {
                    ChatUtils.sendMessage(sender, "ERROR: " + String.format("No zone by the name %s exists!", args[4]));
                }
            }
            ArrayList<String> list = APIRegistry.perms.getGroupPermissions(group.name, zone.getZoneName());
            Collections.sort(list);
            ArrayList<String> messageAllowed = new ArrayList<String>();
            ArrayList<String> messageDenied = new ArrayList<String>();
            for (String perm : list)
            {
                if (perm.contains("has no individual permissions."))
                {
                    ChatUtils.sendMessage(sender, perm);
                    return;
                }
                if (perm.contains("ALLOW"))
                {
                    messageAllowed.add(" " + perm);
                }
                else
                {
                    messageDenied.add(" " + perm);
                }
            }
            ChatUtils.sendMessage(sender,
                    group.name + (group.parent != null ? " inherits from " + group.parent : "") + ".\nCurrent permissions in zone " + zone.getZoneName() + ":");
            for (String perm : messageAllowed)
            {
                ChatUtils.sendMessage(sender, perm);
            }
            for (String perm : messageDenied)
            {
                ChatUtils.sendMessage(sender, perm);
            }
            return;
        }

        ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: " + "");
    }

}
