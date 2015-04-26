package com.forgeessentials.teleport.portal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandPortal extends ForgeEssentialsCommandBase {

    public static final String PERM = "fe.teleport.portal";

    @Override
    public String getCommandName()
    {
        return "portal";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/portal delete|create|recreate|list [name] [x y z] [dim]";
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] argsArray)
    {
        Queue<String> args = new LinkedList<>(Arrays.asList(argsArray));

        if (args.isEmpty())
        {
            OutputHandler.chatConfirmation(sender, getCommandUsage(sender));
            return;
        }

        String subcommand = args.remove().toLowerCase();
        switch (subcommand)
        {
        case "create":
            parseCreate(sender, args, false);
            break;
        case "recreate":
            parseCreate(sender, args, true);
            break;
        case "target":
            parseTarget(sender, args);
            break;
        case "delete":
            parseDelete(sender, args);
            break;
        case "list":
            listPortals(sender, args);
            break;
        default:
            throw new TranslatedCommandException("Unknown subcommand " + subcommand);
        }
    }

    private static void parseCreate(EntityPlayerMP sender, Queue<String> args, boolean recreate)
    {
        if (args.isEmpty())
        {
            OutputHandler.chatConfirmation(sender, "/portal create <name> [frame|noframe] [x y z] [dim]");
            return;
        }

        String name = args.remove();
        if (!recreate && PortalManager.getInstance().portals.containsKey(name))
            throw new TranslatedCommandException("Portal by that name already exists. Use recreate!");

        boolean frame = true;
        if (!args.isEmpty())
        {
            switch (args.peek().toLowerCase())
            {
            case "noframe":
                frame = false;
                args.remove();
                break;
            case "frame":
                frame = true;
                args.remove();
                break;
            }
        }
        
        NamedWorldPoint target = new NamedWorldPoint(sender);
        if (!args.isEmpty())
        {
            if (args.size() < 3)
                throw new TranslatedCommandException("Expected arguments [x y z]");
            int x = parseInt(sender, args.remove());
            int y = parseInt(sender, args.remove());
            int z = parseInt(sender, args.remove());
            int dim = sender.dimension;
            if(!args.isEmpty())
                dim = parseInt(sender, args.remove());
            target = new NamedWorldPoint(dim, x, y, z);
        }

        Selection selection = SelectionHandler.selectionProvider.getSelection(sender);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Missing selection");
        
        Point size = selection.getSize();
        if (size.getX() > 0 && size.getY() > 0 && size.getZ() > 0)
            throw new TranslatedCommandException("Portal selection must be flat in one axis");
        
        Portal portal = new Portal(new NamedWorldArea(selection.getDimension(), selection), target, frame);
        PortalManager.getInstance().add(name, portal);
        OutputHandler.chatConfirmation(sender, Translator.format("Created new portal leading to %s", target.toString()));
    }

    private static void parseTarget(EntityPlayerMP sender, Queue<String> args)
    {
        if (args.isEmpty())
        {
            OutputHandler.chatConfirmation(sender, "/portal target <name> [x y z] [dim]");
            OutputHandler.chatConfirmation(sender, "  Set portal's target to the current / specified location");
            return;
        }

        String name = args.remove();
        if (!PortalManager.getInstance().portals.containsKey(name))
            throw new TranslatedCommandException("Portal by that name does not exist.");

        NamedWorldPoint target = new NamedWorldPoint(sender);
        if (!args.isEmpty())
        {
            if (args.size() < 3)
                throw new TranslatedCommandException("Expected arguments [x y z]");
            int x = parseInt(sender, args.remove());
            int y = parseInt(sender, args.remove());
            int z = parseInt(sender, args.remove());
            int dim = sender.dimension;
            if(!args.isEmpty())
                dim = parseInt(sender, args.remove());
            target = new NamedWorldPoint(dim, x, y, z);
        }
        
        PortalManager.getInstance().get(name).target = target;
        OutputHandler.chatConfirmation(sender, Translator.format("Set target for portal %s to %s", name, target.toString()));
    }

    private static void parseDelete(EntityPlayerMP sender, Queue<String> args)
    {
        if (args.isEmpty())
        {
            OutputHandler.chatConfirmation(sender, "/portal delete <name>");
            return;
        }

        String name = args.remove();
        if (!PortalManager.getInstance().portals.containsKey(name))
            throw new TranslatedCommandException("Portal by that name does not exist.");

        PortalManager.getInstance().remove(name);
        OutputHandler.chatConfirmation(sender, "Deleted portal " + name);
    }

    /**
     * Print lists of portals, their locations and dimensions
     */
    private static void listPortals(EntityPlayerMP sender, Queue<String> args)
    {
        OutputHandler.chatConfirmation(sender, "Registered portals:");
        for (Entry<String, Portal> entry : PortalManager.getInstance().portals.entrySet()) {
            OutputHandler.chatConfirmation(sender, "- " + entry.getKey() + ": " + entry.getValue().getPortalArea().toString());
        }
    }
    
}
