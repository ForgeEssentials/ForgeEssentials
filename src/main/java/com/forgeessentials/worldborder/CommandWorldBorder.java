package com.forgeessentials.worldborder;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
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
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return null;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleWorldBorder.PERM_ADMIN;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
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
            WorldBorder border = ModuleWorldBorder.getInstance().getBorder(arguments.senderPlayer.worldObj);
            if (border == null)
            {
                arguments.error("No worldborder set for this world");
                return;
            }
            arguments.confirm("Worldborder info:");
            arguments.confirm("  center  = " + border.getCenter());
            arguments.confirm("  size    = " + border.getSize().getX() + " x " + border.getSize().getZ());
            arguments.confirm("  start   = " + border.getArea().getLowPoint());
            arguments.confirm("  end     = " + border.getArea().getHighPoint());
            if (border.isEnabled())
                arguments.confirm("  enabled = true");
            else
                arguments.error("  enabled = false");
            return;
        }

        WorldBorder border = ModuleWorldBorder.getInstance().getBorder((WorldServer) arguments.senderPlayer.worldObj, new WorldPoint(arguments.senderPlayer));

        arguments.tabComplete("center", "size", "enable", "disable");
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

    public void parseCenter(CommandParserArgs arguments, WorldBorder border)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(String.format("Worldborder center at %s", border.getCenter()));
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

    private void parseRadius(CommandParserArgs arguments, WorldBorder border)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(String.format("Worldborder size: %d x %d", border.getSize().getX(), border.getSize().getZ()));
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

}
