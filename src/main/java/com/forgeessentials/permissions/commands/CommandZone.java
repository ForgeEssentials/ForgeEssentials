package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandZone extends ForgeEssentialsCommandBase
{

    public static final String PERM_NODE = "fe.perm.zone";
    public static final String PERM_ALL = PERM_NODE + Zone.ALL_PERMS;
    public static final String PERM_LIST = PERM_NODE + ".list";
    public static final String PERM_INFO = PERM_NODE + ".info";
    public static final String PERM_DEFINE = PERM_NODE + ".define";
    public static final String PERM_REDEFINE = PERM_NODE + ".redefine";
    public static final String PERM_DELETE = PERM_NODE + ".delete";
    public static final String PERM_SETTINGS = PERM_NODE + ".settings";

    // Variables for auto-complete
    private static final String[] parseMainArgs = { "help", "list", "select", "define", "redefine", "delete", "exit", "entry" };
    private boolean tabCompleteMode = false;
    private List<String> tabComplete;

    @Override
    public String getCommandName()
    {
        return "area";
    }

    @Override
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("zone");
        return list;
    }

    public void parse(ICommandSender sender, Queue<String> args)
    {
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = getListOfStringsMatchingLastWord(args.peek(), parseMainArgs);
            return;
        }
        if (args.isEmpty())
        {
            help(sender);
        }
        else
        {
            // Get world
            WorldZone worldZone = null;
            if (sender instanceof EntityPlayerMP)
            {
                worldZone = APIRegistry.perms.getServerZone().getWorldZone(((EntityPlayerMP) sender).dimension);
            }

            String arg = args.remove().toLowerCase();
            switch (arg)
            {
            case "help":
                help(sender);
                break;
            case "select":
                parseSelect(sender, worldZone, args);
                break;
            case "info":
                parseInfo(sender, worldZone, args);
                break;
            case "list":
                parseList(sender, worldZone, args);
                break;
            case "define":
            case "redefine":
                parseDefine(sender, worldZone, args, arg.equals("redefine"));
                break;
            case "delete":
                parseDelete(sender, worldZone, args);
                break;
            case "exit":
            case "entry":
                parseEntryExitMessage(sender, worldZone, args, arg.equals("entry"));
                break;
            default:
                throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND);
            }
        }
    }

    private static AreaZone getAreaZone(WorldZone worldZone, String arg)
    {
        try
        {
            Zone z = APIRegistry.perms.getZoneById(arg);
            if (z != null && z instanceof AreaZone)
                return (AreaZone) z;
        }
        catch (NumberFormatException e)
        {
            /* none */
        }
        return worldZone.getAreaZone(arg);
    }

    private static void parseList(ICommandSender sender, WorldZone worldZone, Queue<String> args)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_LIST))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        final int PAGE_SIZE = 12;
        int limit = 1;
        if (!args.isEmpty())
        {
            try
            {
                limit = Integer.parseInt(args.remove());
            }
            catch (NumberFormatException e)
            {
                limit = 1;
            }
        }
        OutputHandler.chatConfirmation(sender, "List of areas (page #" + limit + "):");
        limit *= PAGE_SIZE;
        if (worldZone == null)
        {
            for (WorldZone wz : APIRegistry.perms.getServerZone().getWorldZones().values())
            {
                for (AreaZone areaZone : wz.getAreaZones())
                {
                    if (areaZone.isHidden())
                        continue;
                    if (limit >= 0)
                    {
                        if (limit <= PAGE_SIZE)
                            OutputHandler.chatConfirmation(sender, "#" + areaZone.getId() + ": " + areaZone.toString());
                        limit--;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
        else
        {
            for (AreaZone areaZone : worldZone.getAreaZones())
            {
                if (areaZone.isHidden())
                    continue;
                if (limit >= 0)
                {
                    if (limit <= PAGE_SIZE)
                        OutputHandler.chatConfirmation(sender, "#" + areaZone.getId() + ": " + areaZone.toString());
                    limit--;
                }
                else
                {
                    break;
                }
            }
        }
    }

    private void parseDefine(ICommandSender sender, WorldZone worldZone, Queue<String> args, boolean redefine)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_DEFINE))
        {
            if (!redefine || !PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_REDEFINE))
            {
                OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
                return;
            }
        }

        if (worldZone == null)
        {
            throw new TranslatedCommandException("No world found");
        }
        if (args.isEmpty())
        {
            throw new TranslatedCommandException("Missing arguments!");
        }
        if (tabCompleteMode && args.size() == 1)
        {
            if (!redefine)
                return;
            for (Zone z : APIRegistry.perms.getZones())
            {
                if (z instanceof AreaZone)
                {
                    if (z.getName().startsWith(args.peek()))
                        tabComplete.add(z.getName());
                    if (Integer.toString(z.getId()).startsWith(args.peek()))
                        tabComplete.add(Integer.toString(z.getId()));
                }
            }
            return;
        }
        String zoneName = args.remove();
        AreaZone zone = getAreaZone(worldZone, zoneName);
        if (!redefine && zone != null)
        {
            throw new TranslatedCommandException(String.format("Area \"%s\" already exists!", zoneName));
        }
        else if (redefine && zone == null)
        {
            throw new TranslatedCommandException(String.format("Area \"%s\" does not exist!", zoneName));
        }

        if (tabCompleteMode)
        {
            if (args.size() == 1)
            {
                tabComplete = getListOfStringsMatchingLastWord(args.peek(), AreaShape.valueNames());
            }
            return;
        }

        AreaBase area = null;
        AreaShape shape = null;

        if (!args.isEmpty())
        {
            shape = AreaShape.getByName(args.remove());
            if (shape == null)
                shape = AreaShape.BOX;
        }

        if (!(sender instanceof EntityPlayerMP))
        {
            throw new TranslatedCommandException("Command not usable from console. Try /zone set <name> <coords> instead");
        }

        area = SelectionHandler.selectionProvider.getSelection((EntityPlayerMP) sender);
        if (area == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");

        PermissionContext context = new PermissionContext();
        context.setCommandSender(sender);
        context.setTargetLocationStart(area.getLowPoint().toVec3());
        context.setTargetLocationEnd(area.getHighPoint().toVec3());
        if (!PermissionsManager.checkPermission(context, PERM_DEFINE))
        {
            throw new TranslatedCommandException("You don't have the permission to define an area.");
        }

        if (redefine && zone != null)
        {
            zone.setArea(area);
            if (shape != null)
                zone.setShape(shape);
            OutputHandler.chatConfirmation(sender, String.format("Area \"%s\" has been redefined.", zoneName));
        }
        else
        {
            try
            {
                zone = new AreaZone(worldZone, zoneName, area);
                if (shape != null)
                    zone.setShape(shape);
                OutputHandler.chatConfirmation(sender, String.format("Area \"%s\" has been defined.", zoneName));
            }
            catch (EventCancelledException e)
            {
                OutputHandler.chatError(sender, String.format("Defining area \"%s\" has been cancelled.", zoneName));
            }
        }
    }

    private void parseDelete(ICommandSender sender, WorldZone worldZone, Queue<String> args)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_DELETE))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }
        if (worldZone == null)
        {
            throw new TranslatedCommandException("No world found");
        }
        if (args.isEmpty())
        {
            throw new TranslatedCommandException("Missing arguments!");
        }
        if (tabCompleteMode)
        {
            for (Zone z : APIRegistry.perms.getZones())
            {
                if (z instanceof AreaZone)
                {
                    if (z.getName().startsWith(args.peek()))
                        tabComplete.add(z.getName());
                    if (Integer.toString(z.getId()).startsWith(args.peek()))
                        tabComplete.add(Integer.toString(z.getId()));
                }
            }
            return;
        }
        String zoneName = args.remove();
        AreaZone zone = getAreaZone(worldZone, zoneName);
        if (zone == null)
        {
            OutputHandler.chatError(sender, String.format("Area \"%s\" has does not exist!", zoneName));
            return;
        }
        zone.getWorldZone().removeAreaZone(zone);
        OutputHandler.chatConfirmation(sender, String.format("Area \"%s\" has been deleted.", zoneName));
    }

    private void parseSelect(ICommandSender sender, WorldZone worldZone, Queue<String> args)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_INFO))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        if (!(sender instanceof EntityPlayerMP))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (worldZone == null)
            throw new TranslatedCommandException("No world found");

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing arguments!");

        if (tabCompleteMode)
        {
            for (Zone z : APIRegistry.perms.getZones())
            {
                if (z instanceof AreaZone)
                {
                    if (z.getName().startsWith(args.peek()))
                        tabComplete.add(z.getName());
                    if (Integer.toString(z.getId()).startsWith(args.peek()))
                        tabComplete.add(Integer.toString(z.getId()));
                }
            }
            return;
        }
        String zoneName = args.remove();
        AreaZone zone = getAreaZone(worldZone, zoneName);
        if (zone == null)
        {
            OutputHandler.chatError(sender, String.format("Area \"%s\" has does not exist!", zoneName));
            return;
        }
        AreaBase area = zone.getArea();
        SelectionHandler.selectionProvider.setDimension(player, worldZone.getDimensionID());
        SelectionHandler.selectionProvider.setStart(player, area.getLowPoint());
        SelectionHandler.selectionProvider.setEnd(player, area.getHighPoint());
        SelectionHandler.sendUpdate(player);
        OutputHandler.chatConfirmation(sender, String.format("Area \"%s\" has been selected.", zoneName));
    }

    private void parseInfo(ICommandSender sender, WorldZone worldZone, Queue<String> args)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_INFO))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        if (worldZone == null)
            throw new TranslatedCommandException("No world found");

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing arguments!");

        if (tabCompleteMode)
        {
            for (Zone z : APIRegistry.perms.getZones())
            {
                if (z instanceof AreaZone)
                {
                    if (z.getName().startsWith(args.peek()))
                        tabComplete.add(z.getName());
                    if (Integer.toString(z.getId()).startsWith(args.peek()))
                        tabComplete.add(Integer.toString(z.getId()));
                }
            }
            return;
        }
        String zoneName = args.remove();
        AreaZone zone = getAreaZone(worldZone, zoneName);
        if (zone == null)
        {
            OutputHandler.chatError(sender, String.format("Area \"%s\" has does not exist!", zoneName));
            return;
        }
        AreaBase area = zone.getArea();
        OutputHandler.chatConfirmation(sender, String.format("Area \"%s\"", zoneName));
        OutputHandler.chatNotification(sender, "  start = " + area.getLowPoint().toString());
        OutputHandler.chatNotification(sender, "  end   = " + area.getHighPoint().toString());
    }

    private void parseEntryExitMessage(ICommandSender sender, WorldZone worldZone, Queue<String> args, boolean isEntry)
    {
        if (tabCompleteMode)
        {
            if (args.size() == 1 && "clear".startsWith(args.peek().toLowerCase()))
                tabComplete.add("clear");
            return;
        }
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_SETTINGS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        if (worldZone == null)
        {
            throw new TranslatedCommandException("No world found");
        }
        if (args.isEmpty())
        {
            throw new TranslatedCommandException("Missing arguments!");
        }
        String zoneName = args.remove();
        AreaZone zone = getAreaZone(worldZone, zoneName);
        if (zone == null)
        {
            throw new TranslatedCommandException(String.format("Area \"%s\" does not exist!", zoneName));
        }

        if (args.isEmpty())
        {
            zone.getGroupPermission(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE);
        }
        else
        {
            String msg = StringUtils.join(args, " ");
            if (msg.equalsIgnoreCase("clear"))
                msg = null;
            zone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        LinkedList<String> argsList = new LinkedList<String>(Arrays.asList(args));
        tabCompleteMode = false;
        parse(sender, argsList);
    }

    private static void help(ICommandSender sender)
    {
        OutputHandler.chatConfirmation(sender, "/zone list [page]: Lists all zones");
        OutputHandler.chatConfirmation(sender, "/zone info <zone>|here: Zone information");
        OutputHandler.chatConfirmation(sender, "/zone define|redefine <zone-name>: define or redefine a zone.");
        OutputHandler.chatConfirmation(sender, "/zone delete <zone-id>: Delete a zone.");
        OutputHandler.chatConfirmation(sender, "/zone entry|exit <zone-id> <message|clear>: Set the zone entry/exit message.");
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_NODE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        LinkedList<String> argsList = new LinkedList<String>(Arrays.asList(args));
        tabCompleteMode = true;
        tabComplete = new ArrayList<String>();
        parse(sender, argsList);
        return tabComplete;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/zone: Displays command help";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
