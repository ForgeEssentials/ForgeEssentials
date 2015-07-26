package com.forgeessentials.worldborder;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class CommandWorldBorder extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "worldborder";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "wb" };
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/wb: Configure worldborder";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleWorldBorder.PERM_ADMIN;
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
    public void parse(CommandParserArgs arguments)
    {

        if (arguments.isEmpty())
        {
            arguments.confirm("/wb enable|disable");
            arguments.confirm("/wb center here: Set worldborder center");
            arguments.confirm("/wb size <xz> [z]: Set worldborder size");
            arguments.confirm("/wb shape box|ellipse: Set worldborder center");
            WorldBorder border = ModuleWorldBorder.getInstance().getBorder(arguments.senderPlayer.worldObj);
            if (border == null)
            {
                arguments.error("No worldborder set for this world");
                return;
            }
            arguments.notify("Worldborder info:");
            arguments.notify("  center  = " + border.getCenter());
            arguments.notify("  size    = " + border.getSize().getX() + " x " + border.getSize().getZ());
            arguments.notify("  start   = " + border.getArea().getLowPoint());
            arguments.notify("  end     = " + border.getArea().getHighPoint());
            arguments.notify("  shape   = " + (border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            if (border.isEnabled())
                arguments.confirm("  enabled = true");
            else
                arguments.error("  enabled = false");
            return;
        }

        WorldBorder border = ModuleWorldBorder.getInstance().getBorder((WorldServer) arguments.senderPlayer.worldObj, new WorldPoint(arguments.senderPlayer));

        arguments.tabComplete("center", "disable", "enable", "shape", "size");
        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "enable":
            if (arguments.isTabCompletion)
                return;
            border.setEnabled(true);
            border.save();
            arguments.confirm("Worldborder enabled");
            break;
        case "disable":
            if (arguments.isTabCompletion)
                return;
            border.setEnabled(false);
            border.save();
            arguments.confirm("Worldborder disabled");
            break;
        case "shape":
            parseShape(arguments, border);
            break;
        case "center":
            parseCenter(arguments, border);
            break;
        case "size":
            parseRadius(arguments, border);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
    }

    public static void parseCenter(CommandParserArgs arguments, WorldBorder border)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(String.format("Worldborder center at %s", border.getCenter()));
            arguments.confirm("/wb center here: Set worldborder center");
            return;
        }

        arguments.tabComplete("here");
        if (arguments.isTabCompletion)
            return;

        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "here":
            border.setCenter(new Point(arguments.senderPlayer));
            border.save();
            arguments.confirm("Worldborder center set to current location");
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    public static void parseRadius(CommandParserArgs arguments, WorldBorder border)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(String.format("Worldborder size: %d x %d", border.getSize().getX(), border.getSize().getZ()));
            arguments.confirm("/wb size <xz> [z]: Set worldborder size");
            return;
        }
        int xSize = arguments.parseInt();
        if (arguments.isEmpty())
        {
            border.getSize().setX(xSize);
            border.getSize().setZ(xSize);
        }
        else
        {
            int zSize = arguments.parseInt();
            border.getSize().setX(xSize);
            border.getSize().setZ(zSize);
        }
        border.updateArea();
        border.save();
        arguments.confirm(String.format("Worldborder size set to %d x %d", border.getSize().getX(), border.getSize().getZ()));
    }

    public static void parseShape(CommandParserArgs arguments, WorldBorder border)
    {
        if (arguments.isEmpty())
        {
            arguments.notify(String.format("Worldborder shape: %s", border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            arguments.confirm("/wb shape box|ellipse");
            return;
        }

        arguments.tabComplete("box", "ellipse");
        if (arguments.isTabCompletion)
            return;

        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "box":
            border.setShape(AreaShape.BOX);
            border.save();
            arguments.confirm("Worldborder shape set to box");
            break;
        case "ellipse":
            border.setShape(AreaShape.ELLIPSOID);
            border.save();
            arguments.confirm("Worldborder shape set to ellipse");
            break;
        default:
            throw new TranslatedCommandException("Unknown shape type %s", subCommand);
        }
    }

}
