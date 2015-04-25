package com.forgeessentials.util.selections;

//Depreciated

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandPos extends ForgeEssentialsCommandBase {
    private int type;

    public CommandPos(int type)
    {
        this.type = type;
    }

    @Override
    public String getCommandName()
    {
        return "/fepos" + type;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        int x, y, z;

        if (args.length == 1)
        {
            if (args[0].toLowerCase().equals("here"))
            {
                x = (int) player.posX;
                y = (int) player.posY;
                z = (int) player.posZ;

                if (type == 1)
                {
                    SelectionHandler.selectionProvider.setStart(player, new Point(x, y, z));
                }
                else
                {
                    SelectionHandler.selectionProvider.setEnd(player, new Point(x, y, z));
                }

                OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
                return;

            }
            else
            {
                throw new TranslatedCommandException(getCommandUsage(player));
            }
        }

        if (args.length > 0)
        {
            if (args.length < 3)
            {
                throw new TranslatedCommandException(getCommandUsage(player));
            }

            try
            {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e)
            {
                throw new TranslatedCommandException(getCommandUsage(player));
            }

            if (type == 1)
            {
                SelectionHandler.selectionProvider.setStart(player, new Point(x, y, z));
            }
            else
            {
                SelectionHandler.selectionProvider.setEnd(player, new Point(x, y, z));
            }

            OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
            return;
        }

        MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(player);

        if (mop == null)
            throw new TranslatedCommandException("You must first look at the ground!");

        x = mop.blockX;
        y = mop.blockY;
        z = mop.blockZ;

        WorldPoint point = new WorldPoint(player.dimension, x, y, z);
        if (!APIRegistry.perms.checkUserPermission(new UserIdent(player), point, getPermissionNode()))
            throw new TranslatedCommandException("Insufficient permissions.");

        if (type == 1)
        {
            SelectionHandler.selectionProvider.setStart(player, point);
        }
        else
        {
            SelectionHandler.selectionProvider.setEnd(player, point);
        }

        OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
        return;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.pos";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/" + getCommandName() + " [<x> <y> <z] or [here] Sets selection positions";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
