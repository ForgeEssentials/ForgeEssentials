package com.forgeessentials.teleport.portal;

import java.util.Map.Entry;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandPortal extends ParserCommandBase
{

    public static final String PERM = "fe.teleport.portal";

    @Override
    public String getCommandName()
    {
        return "portal";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/portal delete|create|recreate|list|target [name] [x y z] [dim] Manage portals.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(getCommandUsage(arguments.sender));
            return;
        }

        arguments.tabComplete("create", "recreate", "target", "delete", "list");

        String subcommand = arguments.remove().toLowerCase();
        switch (subcommand)
        {
        case "create":
            parseCreate(arguments, false);
            break;
        case "recreate":
            parseCreate(arguments, true);
            break;
        case "target":
            parseTarget(arguments);
            break;
        case "delete":
            parseDelete(arguments);
            break;
        case "list":
            listPortals(arguments);
            break;
        default:
            arguments.warn("Valid subcommands: delete, create, recreate, list, target");
            throw new TranslatedCommandException("Unknown subcommand " + subcommand);
        }
    }

    private static void parseCreate(CommandParserArgs arguments, boolean recreate) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/portal create <name> [frame|noframe] [x y z] [dim]");
            return;
        }

        String name = arguments.remove();
        if (!recreate && PortalManager.getInstance().portals.containsKey(name))
            throw new TranslatedCommandException("Portal by that name already exists. Use recreate!");

        arguments.tabComplete("noframe", "frame");

        boolean frame = true;
        if (!arguments.isEmpty())
        {
            switch (arguments.peek().toLowerCase())
            {
            case "noframe":
                frame = false;
                arguments.remove();
                break;
            case "frame":
                frame = true;
                arguments.remove();
                break;
            }
        }

        NamedWorldPoint target = new NamedWorldPoint(arguments.senderPlayer);
        if (!arguments.isEmpty())
        {
            if (arguments.size() < 3)
                throw new TranslatedCommandException("Expected arguments [x y z]");
            int x = parseInt(arguments.remove());
            int y = parseInt(arguments.remove());
            int z = parseInt(arguments.remove());
            int dim = arguments.senderPlayer.dimension;
            if (!arguments.isEmpty())
                dim = parseInt(arguments.remove());
            target = new NamedWorldPoint(dim, x, y, z);
        }

        if (arguments.isTabCompletion)
            return;

        Selection selection = SelectionHandler.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Missing selection");

        Point size = selection.getSize();
        if (size.getX() > 0 && size.getY() > 0 && size.getZ() > 0)
            throw new TranslatedCommandException("Portal selection must be flat in one axis");

        Portal portal = new Portal(new NamedWorldArea(selection.getDimension(), selection), target, frame);
        PortalManager.getInstance().add(name, portal);
        arguments.confirm("Created new portal leading to %s", target.toString());
    }

    private static void parseTarget(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/portal target <name> [x y z] [dim]");
            arguments.confirm("  Set portal's target to the current / specified location");
            return;
        }

        arguments.tabComplete(PortalManager.getInstance().portals.keySet());

        String name = arguments.remove();
        if (!PortalManager.getInstance().portals.containsKey(name))
            throw new TranslatedCommandException("Portal by that name does not exist.");

        NamedWorldPoint target = new NamedWorldPoint(arguments.senderPlayer);
        if (!arguments.isEmpty())
        {
            if (arguments.size() < 3)
                throw new TranslatedCommandException("Expected arguments [x y z]");
            int x = parseInt(arguments.remove());
            int y = parseInt(arguments.remove());
            int z = parseInt(arguments.remove());
            int dim = arguments.senderPlayer.dimension;
            if (!arguments.isEmpty())
                dim = parseInt(arguments.remove());
            target = new NamedWorldPoint(dim, x, y, z);
        }

        if (arguments.isTabCompletion)
            return;

        PortalManager.getInstance().get(name).target = target;
        arguments.confirm("Set target for portal %s to %s", name, target.toString());
    }

    private static void parseDelete(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/portal delete <name>");
            return;
        }

        arguments.tabComplete(PortalManager.getInstance().portals.keySet());
        if (arguments.isTabCompletion)
            return;

        String name = arguments.remove();
        if (!PortalManager.getInstance().portals.containsKey(name))
            throw new TranslatedCommandException("Portal by that name does not exist.");

        PortalManager.getInstance().remove(name);
        arguments.confirm("Deleted portal " + name);
    }

    /**
     * Print lists of portals, their locations and dimensions
     */
    private static void listPortals(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        arguments.confirm("Registered portals:");
        for (Entry<String, Portal> entry : PortalManager.getInstance().portals.entrySet())
        {
            arguments.confirm("- " + entry.getKey() + ": " + entry.getValue().getPortalArea().toString());
        }
    }

}
