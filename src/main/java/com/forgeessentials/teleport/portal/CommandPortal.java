package com.forgeessentials.teleport.portal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

import com.forgeessentials.util.selections.SelectionHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

/**
 * @author Olee
 *
 */
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
        return "/portal list|delete|create <name> [x y z] [dimension]";
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
            parseCreate(sender, args);
            break;
        case "delete":
            parseDelete(sender, args);
            break;
        case "list":
            parseList(sender, args);
            break;
        default:
            throw new CommandException("Unknown subcommand " + subcommand);
        }
    }

    private static void parseCreate(EntityPlayerMP sender, Queue<String> args)
    {
        if (args.isEmpty())
        {
            OutputHandler.chatConfirmation(sender, "/portal create <name> [x y z] <dimension>");
            return;
        }

        String name = args.remove();
        if (PortalManager.getInstance().portals.containsKey(name))
            throw new CommandException("Portal by that name already exists.");

        NamedWorldPoint target = new NamedWorldPoint(sender);
        if (!args.isEmpty())
        {
            if (args.size() < 3)
                throw new CommandException("Expected arguments [x y z]");
            int x = parseInt(sender, args.remove());
            int y = parseInt(sender, args.remove());
            int z = parseInt(sender, args.remove());
            
            int dimension = sender.dimension;
            if(args.size() == 1)
            	dimension = parseInt(sender, args.remove());
            	
            target = new NamedWorldPoint(dimension, x, y, z);
        }

        Selection selection = SelectionHandler.selectionProvider.getSelection(sender);
        if (selection == null)
            throw new CommandException("Missing selection");
        
        Point size = selection.getSize();
        if (size.getX() > 0 && size.getY() > 0 && size.getZ() > 0)
            throw new CommandException("Portal selection must be flat in one axis");
        
        Portal portal = new Portal(new NamedWorldArea(sender.dimension, selection), target);
        PortalManager.getInstance().add(name, portal);
        OutputHandler.chatConfirmation(sender, String.format("Created new portal leading to %s", target.toString()));
    }

    private static void parseDelete(EntityPlayerMP sender, Queue<String> args)
    {
        if (args.isEmpty())
        {
            OutputHandler.chatConfirmation(sender, "/portal delete <name>");
            return;
        }

        String name = args.remove();
        if (PortalManager.getInstance().portals.containsKey(name))
        	OutputHandler.chatConfirmation(sender, "Portal '" + name + "' deleted.");
        else
            throw new CommandException("Portal '" + name + "'  does not exist.");

        PortalManager.getInstance().remove(name);
    }
    
    /**
     * Print lists of portals, their locations and dimensions
     */
    private static void parseList(EntityPlayerMP sender, Queue<String> args)
    {
    	if (!args.isEmpty())
    		throw new CommandException("No arguments are accepted for /portal list");
        
        for (HashMap.Entry<String, Portal> entry : PortalManager.getInstance().portals.entrySet()) {
            String name = entry.getKey();
            Portal portal = entry.getValue();
            
            OutputHandler.chatConfirmation(sender, name + ": " + portal.getPortalArea().toString());
        }
    }

}
