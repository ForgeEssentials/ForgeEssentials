package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandZone extends ParserCommandBase
{

    public static final String PERM_NODE = "fe.perm.zone";
    public static final String PERM_ALL = PERM_NODE + Zone.ALL_PERMS;
    public static final String PERM_LIST = PERM_NODE + ".list";
    public static final String PERM_INFO = PERM_NODE + ".info";
    public static final String PERM_DEFINE = PERM_NODE + ".define";
    public static final String PERM_DELETE = PERM_NODE + ".delete";
    public static final String PERM_SETTINGS = PERM_NODE + ".settings";

    @Override
    public String getCommandName()
    {
        return "area";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/area: Manage permission areas";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "zone" };
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_NODE;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/zone list [page]: Lists all zones");
            arguments.confirm("/zone info <zone>|here: Zone information");
            arguments.confirm("/zone define|redefine <zone-name>: define or redefine a zone.");
            arguments.confirm("/zone delete <zone-id>: Delete a zone.");
            arguments.confirm("/zone entry|exit <zone-id> <message|clear>: Set the zone entry/exit message.");
            return;
        }

        arguments.tabComplete("define", "list", "delete", "select", "redefine", "exit", "entry");
        String arg = arguments.remove().toLowerCase();
        switch (arg)
        {
        case "select":
            parseSelect(arguments);
            break;
        case "info":
            parseInfo(arguments);
            break;
        case "list":
            parseList(arguments);
            break;
        case "define":
            parseDefine(arguments, false);
            break;
        case "redefine":
            parseDefine(arguments, true);
            break;
        case "delete":
            parseDelete(arguments);
            break;
        case "entry":
            parseEntryExitMessage(arguments, true);
            break;
        case "exit":
            parseEntryExitMessage(arguments, false);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, arg);
        }
    }

    public static AreaZone getAreaZone(WorldZone worldZone, String arg)
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

    public static void parseList(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isTabCompletion)
            return;
        
        arguments.checkPermission(PERM_LIST);
        
        final int PAGE_SIZE = 12;
        int limit = 1;
        if (!arguments.isEmpty())
        {
            try
            {
                limit = Integer.parseInt(arguments.remove());
            }
            catch (NumberFormatException e)
            {
                limit = 1;
            }
        }
        arguments.confirm("List of areas (page #" + limit + "):");
        limit *= PAGE_SIZE;

        WorldZone worldZone = arguments.getWorldZone();
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
                            arguments.confirm("#" + areaZone.getId() + ": " + areaZone.toString());
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
                        arguments.confirm("#" + areaZone.getId() + ": " + areaZone.toString());
                    limit--;
                }
                else
                {
                    break;
                }
            }
        }
    }

    public static void parseDefine(CommandParserArgs arguments, boolean redefine) throws CommandException
    {
        arguments.checkPermission(PERM_DEFINE);
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone area = getAreaZone(worldZone, areaName);
        if (!redefine && area != null)
            throw new TranslatedCommandException(String.format("Area \"%s\" already exists!", areaName));
        if (redefine && area == null)
            throw new TranslatedCommandException(String.format("Area \"%s\" does not exist!", areaName));

        AreaShape shape = null;
        if (!arguments.isEmpty())
        {
            arguments.tabComplete(AreaShape.valueNames());
            shape = AreaShape.getByName(arguments.remove());
            if (shape == null)
                shape = AreaShape.BOX;
        }

        if (arguments.isTabCompletion)
            return;
        
        AreaBase selection = SelectionHandler.getSelection(arguments.senderPlayer);
        if (selection == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");

        arguments.permissionContext.setTargetStart(selection.getLowPoint().toVec3()).setTargetEnd(selection.getHighPoint().toVec3());
        arguments.checkPermission(PERM_DEFINE);

        if (redefine && area != null)
        {
            area.setArea(selection);
            if (shape != null)
                area.setShape(shape);
            arguments.confirm("Area \"%s\" has been redefined.", areaName);
        }
        else
        {
            try
            {
                area = new AreaZone(worldZone, areaName, selection);
                if (shape != null)
                    area.setShape(shape);
                arguments.confirm("Area \"%s\" has been defined.", areaName);
            }
            catch (EventCancelledException e)
            {
                throw new TranslatedCommandException("Defining area \"%s\" has been cancelled.", areaName);
            }
        }
    }

    public static void parseDelete(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(PERM_DELETE);
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        if (arguments.isTabCompletion)
            return;
        
        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
        areaZone.getWorldZone().removeAreaZone(areaZone);
        arguments.confirm("Area \"%s\" has been deleted.", areaZone.getName());
    }

    public static void parseSelect(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(PERM_INFO);
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        if (arguments.isTabCompletion)
            return;
        
        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);

        AreaBase area = areaZone.getArea();
        SelectionHandler.select(arguments.senderPlayer, worldZone.getDimensionID(), area);
        arguments.confirm("Area \"%s\" has been selected.", areaName);
    }

    public static void parseInfo(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(PERM_INFO);
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();
        
        if (arguments.isTabCompletion)
            return;

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
        AreaBase area = areaZone.getArea();

        arguments.confirm("Area \"%s\"", areaZone.getName());
        arguments.notify("  start = " + area.getLowPoint().toString());
        arguments.notify("  end   = " + area.getHighPoint().toString());
    }

    public static void parseEntryExitMessage(CommandParserArgs arguments, boolean isEntry) throws CommandException
    {
        arguments.checkPermission(PERM_SETTINGS);
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);

        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.format((isEntry ? "Entry" : "Exit") + " message for area %s:", areaZone.getName()));
            arguments.confirm(areaZone.getGroupPermission(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE));
            return;
        }

        arguments.tabComplete("clear");
        String msg = arguments.toString();
        if (msg.equalsIgnoreCase("clear"))
            msg = null;

        if (arguments.isTabCompletion)
            return;
        areaZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
    }

    public static void tabCompleteArea(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isTabCompletion && arguments.size() == 1)
        {
            for (Zone z : APIRegistry.perms.getZones())
            {
                if (z instanceof AreaZone)
                {
                    if (z.getName().startsWith(arguments.peek()))
                        arguments.tabCompleteWord(z.getName());
                    if (Integer.toString(z.getId()).startsWith(arguments.peek()))
                        arguments.tabCompleteWord(Integer.toString(z.getId()));
                }
            }
            throw new CommandParserArgs.CancelParsingException();
        }
    }

}
