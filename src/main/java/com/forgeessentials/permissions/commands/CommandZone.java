package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.commons.AreaBase;

public class CommandZone extends ForgeEssentialsCommandBase {

    public static final String PERM_NODE = "fe.perm.zone";
    public static final String PERM_ALL = PERM_NODE + ".*";
    public static final String PERM_LIST = PERM_NODE + ".list";
    public static final String PERM_INFO = PERM_NODE + ".info";
    public static final String PERM_DEFINE = PERM_NODE + ".define";
    public static final String PERM_REDEFINE = PERM_NODE + ".redefine";
    public static final String PERM_DELETE = PERM_NODE + ".delete";
    public static final String PERM_SETTINGS = PERM_NODE + ".settings";

    // Variables for auto-complete
    private static final String[] parseMainArgs = { "help", "list", "info", "define", "redefine", "delete", "exit", "entry" };
    private boolean tabCompleteMode = false;
    private List<String> tabComplete;

    @Override
    public String getCommandName()
    {
        return "zone";
    }

    @Override
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("area");
        return list;
    }

    @SuppressWarnings("unchecked")
    public void parse(ICommandSender sender, Queue<String> args)
    {
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = CommandBase.getListOfStringsMatchingLastWord(args.toArray(new String[args.size()]), parseMainArgs);
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
                worldZone = APIRegistry.perms.getWorldZone(((EntityPlayerMP) sender).dimension);
            }

            String arg = args.remove().toLowerCase();
            switch (arg) {
            case "help":
                help(sender);
                break;
            case "info":
                throw new CommandException("Not yet implemented!");
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
                OutputHandler.chatError(sender, "Unknown command argument");
                break;
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
            throw new CommandException("No world found");
        }
        if (args.isEmpty())
        {
            throw new CommandException("Missing arguments!");
        }
        if (tabCompleteMode)
        {
            if (!redefine)
                return;
            for (Zone z: APIRegistry.perms.getZones()) {
                if (z instanceof AreaZone) {
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
            throw new CommandException(String.format("Area \"%s\" already exists!", zoneName));
        }
        else if (redefine && zone == null)
        {
            throw new CommandException(String.format("Area \"%s\" does not exist!", zoneName));
        }

        if (args.isEmpty())
        {
            if (!(sender instanceof EntityPlayerMP))
            {
                throw new CommandException("Command not usable from console. Try /zone set <name> <coords> instead");
            }
            
            PlayerInfo info = PlayerInfo.getPlayerInfo((EntityPlayerMP) sender);
            AreaBase area = info.getSelection();
            if (area == null)
                throw new CommandException("No selection available. Please select a region first.");

            PermissionContext context = new PermissionContext();
            context.setCommandSender(sender);
            context.setTargetLocationStart(area.getLowPoint().toVec3());
            context.setTargetLocationEnd(area.getHighPoint().toVec3());
            if (!PermissionsManager.checkPermission(context, PERM_DEFINE))
            {
                throw new CommandException("You don't have the permission to define an area.");
            }

            if (redefine)
            {
                zone.setArea(area);
                OutputHandler.chatConfirmation(sender, String.format("Area \"%s\" has been redefined.", zoneName));
            }
            else
            {
                zone = new AreaZone(worldZone, zoneName, area);
                OutputHandler.chatConfirmation(sender, String.format("Area \"%s\" has been defined.", zoneName));
            }
        }
        else if (args.size() >= 3)
        {
            throw new CommandException("Not yet implemented!");
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
            throw new CommandException("No world found");
        }
        if (args.isEmpty())
        {
            throw new CommandException("Missing arguments!");
        }
        if (tabCompleteMode)
        {
            for (Zone z: APIRegistry.perms.getZones()) {
                if (z instanceof AreaZone) {
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

    private void parseEntryExitMessage(ICommandSender sender, WorldZone worldZone, Queue<String> args, boolean isEntry)
    {
        if (!PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender), PERM_SETTINGS))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        if (worldZone == null)
        {
            throw new CommandException("No world found");
        }
        if (args.isEmpty())
        {
            throw new CommandException("Missing arguments!");
        }
        String zoneName = args.remove();
        AreaZone zone = getAreaZone(worldZone, zoneName);
        if (zone == null)
        {
            throw new CommandException(String.format("Area \"%s\" does not exist!", zoneName));
        }

        if (args.isEmpty())
        {
            zone.getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE);
        }
        else
        {
            String msg = StringUtils.join(args);
            if (msg.equalsIgnoreCase("clear"))
                msg = null;
            zone.setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
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
