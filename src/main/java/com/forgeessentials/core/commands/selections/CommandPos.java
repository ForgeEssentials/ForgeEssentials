package com.forgeessentials.core.commands.selections;

//Depreciated

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayerArea;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class CommandPos extends ForgeEssentialsCommandBase {
    private int type;

    public CommandPos(int type)
    {
        this.type = type;
    }

    @Override
    public String getCommandName()
    {
        return "fepos" + type;
    }

    @Override
    public void processCommandPlayer(EntityPlayer player, String[] args)
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
                    PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));
                }
                else
                {
                    PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));
                }

                OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
                return;

            }
            else
            {
                error(player);
                return;
            }
        }

        if (args.length > 0)
        {
            if (args.length < 3)
            {
                error(player);
                return;
            }

            try
            {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e)
            {
                error(player);
                return;
            }

            if (type == 1)
            {
                PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));
            }
            else
            {
                PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));
            }

            OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
            return;
        }

        MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(player, true);

        if (mop == null)
        {
            OutputHandler.chatError(player, "You must first look at the ground!");
            return;
        }

        x = mop.blockX;
        y = mop.blockY;
        z = mop.blockZ;

        Point point = new Point(x, y, z);
        if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayerArea(player, getCommandPerm(), point)))
        {
            OutputHandler.chatError(player, "Insufficient permissions.");
            return;
        }

        if (type == 1)
        {
            PlayerInfo.getPlayerInfo(player.username).setPoint1(point);
        }
        else
        {
            PlayerInfo.getPlayerInfo(player.username).setPoint2(point);
        }

        OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
        return;
    }

    @Override
    public String getCommandPerm()
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
    public RegGroup getReggroup()
    {

        return RegGroup.MEMBERS;
    }

}
