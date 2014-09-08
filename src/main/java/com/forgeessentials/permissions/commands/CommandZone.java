package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldPoint;

public class CommandZone extends ForgeEssentialsCommandBase {
    private static String[] commands = { "list", "info", "define", "redefine", "remove", "setParent", "entry", "exit" };

    @Override
    public String getCommandName()
    {
        return "zone";
    }

    @Override
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("zn");
        return list;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
    	throw new RuntimeException("Not yet implemented!");
//        PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
//        ArrayList<Zone> zones = APIRegistry.perms.getZoneList();
//        int zonePages = zones.size() / 15 + 1;
//        if (args.length == 1)
//        {
//            if (args[0].equalsIgnoreCase("list"))
//            {
//                if (!PermissionsManager.checkPerm(sender, getPermissionNode() + ".list"))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else
//                {
//                    OutputHandler.chatConfirmation(sender, String.format("command.permissions.zone.list.header", 1, zonePages));
//                    int itterrator = 0;
//                    String output;
//                    for (Zone zone : zones)
//                    {
//                        if (itterrator == 15)
//                        {
//                            break;
//                        }
//                        output = " - " + zone.getName();
//                        if (zone.isWorldZone())
//                        {
//                            output = output + " --> WorldZone";
//                        }
//                        OutputHandler.chatConfirmation(sender, output);
//                    }
//                }
//                return;
//            }
//            else
//            {
//                error(sender);
//            }
//
//        }
//        else if (args.length == 2)
//        {
//            if (args[0].equalsIgnoreCase("list"))
//            {
//                if (!PermissionsManager.checkPerm(sender, getPermissionNode() + ".list"))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else
//                {
//                    try
//                    {
//                        int page = Integer.parseInt(args[1]);
//                        if (page <= 0 || page > zonePages)
//                        {
//                            OutputHandler.chatConfirmation(sender, "No page by that number exists!");
//                        }
//                        else
//                        {
//                            OutputHandler.chatConfirmation(sender, String.format(">--- Showing the zonelist page %1$d of %2$d ---", page, zonePages));
//                            String output;
//                            Zone zone;
//                            for (int i = (page - 1) * 15; i < page * 15; i++)
//                            {
//                                zone = zones.get(i);
//                                output = " - " + zone.getName();
//                                if (zone.isWorldZone())
//                                {
//                                    output = output + " --> WorldZone";
//                                }
//                                OutputHandler.chatConfirmation(sender, output);
//                            }
//                        }
//                    }
//                    catch (NumberFormatException e)
//                    {
//                        OutputHandler.chatError(sender, String.format("%s param was not recognized as number. Please try again.", 1));
//                    }
//                }
//                return;
//            }
//            else if (args[0].equalsIgnoreCase("info"))
//            {
//                if (args[1].equalsIgnoreCase("here"))
//                {
//                    WorldPoint point = new WorldPoint(sender);
//                    args[1] = APIRegistry.perms.getZoneAt(point).getName();
//                }
//                if (!APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[1]));
//                }
//                else
//                {
//                    if (!PermissionsManager.checkPerm(sender, getPermissionNode( + ".info." + args[1])))
//                    {
//                        OutputHandler.chatError(sender,
//                                "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                    }
//                    else
//                    {
//                        Zone zone = APIRegistry.perms.getZone(args[1]);
//                        PropQueryBlanketZone query1 = new PropQueryBlanketZone("fe.perm.Zone.entry", zone, false);
//                        PropQueryBlanketZone query2 = new PropQueryBlanketZone("fe.perm.Zone.exit", zone, false);
//                        APIRegistry.perms.getPermissionProp(query1);
//                        APIRegistry.perms.getPermissionProp(query2);
//
//                        OutputHandler.chatConfirmation(sender, "Name: " + zone.getName());
//                        OutputHandler.chatConfirmation(sender, "Parent: " + zone.parent);
//                        OutputHandler.chatConfirmation(sender, "Priority: " + zone.priority);
//                        OutputHandler.chatConfirmation(sender,
//                                "Dimension: " + zone.getDimension() + "     World: " + FunctionHelper.getDimension(zone.getDimension()).provider.getDimensionName());
//                        ChatUtils.sendMessage(sender,
//                                FunctionHelper.formatColors(EnumChatFormatting.GREEN + "Entry Message: " + EnumChatFormatting.RESET + query1.getStringValue()));
//                        ChatUtils.sendMessage(sender,
//                                FunctionHelper.formatColors(EnumChatFormatting.GREEN + "Exit Message: " + EnumChatFormatting.RESET + query2.getStringValue()));
//                        Point high = zone.getHighPoint();
//                        Point low = zone.getLowPoint();
//                        OutputHandler.chatConfirmation(sender, high.getX() + ", " + high.getY() + ", " + high.getZ() + " -> " + low.getX() + ", " + low.getY() + ", " + low.getZ());
//                    }
//                }
//                return;
//            }
//            else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))
//            {
//                if (!APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[1]));
//                }
//                else
//                {
//                    if (!PermissionsManager.checkPerm(sender, getPermissionNode( + ".remove." + args[1])))
//                    {
//                        OutputHandler.chatError(sender,
//                                "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                    }
//                    else
//                    {
//                        APIRegistry.perms.deleteZone(args[1]);
//                        OutputHandler.chatConfirmation(sender, String.format("%s was removed successfully!", args[1]));
//                    }
//                }
//                return;
//            }
//            else if (args[0].equalsIgnoreCase("define"))
//            {
//                if (APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("A zone by the name %s already exists!", args[1]));
//                }
//                else if (info.getSelection() == null)
//                {
//                    OutputHandler.chatError(sender, "Invalid selection detected. Please check your selection.");
//                    return;
//                }
//                else if (!PermissionsManager.checkPerm(new PermQueryPlayerArea(sender, getPermissionNode() + ".define", info.getSelection(), true)))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else
//                {
//                    APIRegistry.perms.createZone(args[1], info.getSelection(), sender.worldObj);
//                    OutputHandler.chatConfirmation(sender, String.format("%s was defined successfully", args[1]));
//                }
//                return;
//            }
//            else if (args[0].equalsIgnoreCase("redefine"))
//            {
//                if (!APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("A zone by the name %s already exists!", args[1]));
//                }
//                else if (info.getSelection() == null)
//                {
//                    OutputHandler.chatError(sender, "Invalid selection detected. Please check your selection.");
//                    return;
//                }
//                else if (!APIRegistry.perms
//                        .checkPermAllowed(new PermQueryPlayerArea(sender, getPermissionNode() + ".redefine." + args[1], info.getSelection(), true)))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else
//                {
//                    Zone z = APIRegistry.perms.getZone(args[1]);
//                    z.redefine(info.getPoint1(), info.getPoint2());
//                    saveZone(z);
//                    OutputHandler.chatConfirmation(sender, String.format("%s redefined successfully!", args[1]));
//                }
//                return;
//            }
//
//        }
//        else if (args.length >= 3)
//        {
//            if (args[0].equalsIgnoreCase("setParent"))
//            {
//                if (!APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[1]));
//                }
//                else if (!APIRegistry.perms.doesZoneExist(args[2]))
//                {
//                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
//                }
//                else if (!PermissionsManager.checkPerm(sender, getPermissionNode( + ".setparent." + args[1])))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else
//                {
//                    Zone z = APIRegistry.perms.getZone(args[1]);
//                    z.parent = args[2];
//                    saveZone(z);
//                    OutputHandler.chatConfirmation(sender, String.format("The parent of %s was successfully set to %s.", args[1], args[2]));
//                }
//                return;
//            }
//
//            if (args[0].equalsIgnoreCase("entry"))
//            {
//                if (!APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[1]));
//                    return;
//                }
//                else if (!PermissionsManager.checkPerm(sender, getPermissionNode( + ".entry." + args[1])))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else if (args[2].equalsIgnoreCase("get"))
//                {
//                    PropQueryBlanketZone query = new PropQueryBlanketZone("fe.perm.Zone.entry", APIRegistry.perms.getZone(args[1]), false);
//                    APIRegistry.perms.getPermissionProp(query);
//                    OutputHandler.chatConfirmation(sender, query.getStringValue());
//
//                    return;
//                }
//                else if (args[2].equalsIgnoreCase("remove"))
//                {
//                    APIRegistry.perms.clearGroupPermissionProp(APIRegistry.perms.getDefaultGroup().name, "fe.perm.Zone.entry", args[1]);
//                    OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Entry Message removed.");
//                }
//                else
//                {
//                    String tempEntry = "";
//                    for (int i = 2; i < args.length; i++)
//                    {
//                        tempEntry += args[i] + " ";
//                    }
//                    APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDefaultGroup().name, "fe.perm.Zone.entry", tempEntry, args[1]);
//
//                    OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Entry Message set to: " + tempEntry);
//                    return;
//                }
//            }
//            else if (args[0].equalsIgnoreCase("exit"))
//            {
//                if (!APIRegistry.perms.doesZoneExist(args[1]))
//                {
//                    OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[1]));
//                    return;
//                }
//                else if (!PermissionsManager.checkPerm(sender, getPermissionNode( + ".exit." + args[1])))
//                {
//                    OutputHandler.chatError(sender,
//                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
//                }
//                else if (args[2].equalsIgnoreCase("get"))
//                {
//                    PropQueryBlanketZone query = new PropQueryBlanketZone("fe.perm.Zone.exit", APIRegistry.perms.getZone(args[1]), false);
//                    APIRegistry.perms.getPermissionProp(query);
//                    OutputHandler.chatConfirmation(sender, query.getStringValue());
//
//                    return;
//                }
//                else if (args[2].equalsIgnoreCase("remove"))
//                {
//                    APIRegistry.perms.clearGroupPermissionProp(APIRegistry.perms.getDefaultGroup().name, "fe.perm.Zone.exit", args[1]);
//                    OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Exit Message removed.");
//                }
//                else
//                {
//                    String tempEntry = "";
//                    for (int i = 2; i < args.length; i++)
//                    {
//                        tempEntry += args[i] + " ";
//                    }
//                    APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDefaultGroup().name, "fe.perm.Zone.exit", tempEntry, args[1]);
//
//                    OutputHandler.chatConfirmation(sender, "Zone: " + args[1] + " Exit Message set to: " + tempEntry);
//                    return;
//                }
//            }
//
//        }
//        else
//        {
//            help(sender);
//        }
    }

    private void help(ICommandSender sender)
    {
        ChatUtils.sendMessage(sender, "/zone list [#page] Lists all zones");
        ChatUtils.sendMessage(sender, "/zone info <zone|here> Displays information about the zone such as parent, priority, and location");
        ChatUtils.sendMessage(sender, "/zone <define|redefine|delete> define, redefine or delete a zone.");
        ChatUtils.sendMessage(sender, "/zone setparent <parentzone> <childzone> Set a zone as a parent of another zone.");
        ChatUtils.sendMessage(sender,
                "/zone entry|exit <name> [... message ...] Set the zone entry or exit message to a particular message. Set the message to 'remove' to delete it.");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.perm.zone";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	// TODO: addTabCompletionOptions
    	return null;
//        ArrayList<String> list = new ArrayList<String>();
//        switch (args.length)
//        {
//        case 0:
//        case 1:
//            for (String c : commands)
//            {
//                list.add(c);
//            }
//            break;
//        case 2:
//            for (Zone z : APIRegistry.perms.getZoneList())
//            {
//                list.add(z.getName());
//            }
//            break;
//        case 3:
//            if (args[0].equalsIgnoreCase("setparent"))
//            {
//                for (Zone z : APIRegistry.perms.getZoneList())
//                {
//                    list.add(z.getName());
//                }
//            }
//        }
//        return list;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/zone help";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
