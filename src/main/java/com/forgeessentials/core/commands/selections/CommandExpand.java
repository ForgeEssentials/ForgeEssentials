package com.forgeessentials.core.commands.selections;

//Depreciated

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandExpand extends ForgeEssentialsCommandBase {

    public CommandExpand()
    {
        return;
    }

    @Override
    public String getCommandName()
    {
        return "/expand";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args.length == 1)
        {
            int x = Math.round((float) player.getLookVec().xCoord);
            int y = Math.round((float) player.getLookVec().yCoord);
            int z = Math.round((float) player.getLookVec().zCoord);
            PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());
            int expandby = Integer.decode(args[0]);

            // Check to see if selection is valid for expand.
            if (info.getPoint1() == null || info.getPoint2() == null)
            {

                OutputHandler.chatError(player, "Invalid previous selection.");
                return;
            }

            if (x == -1)
            {
                if (info.getPoint1().getX() < info.getPoint2().getX())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX() - expandby, info.getPoint1().getY(), info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX() - expandby, info.getPoint2().getY(), info.getPoint2().getZ()));
                }
            }
            else if (z == 1)
            {
                if (info.getPoint1().getZ() < info.getPoint2().getZ())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY(), info.getPoint1().getZ() + expandby));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY(), info.getPoint2().getZ() + expandby));
                }
            }
            else if (x == 1)
            {
                if (info.getPoint1().getX() < info.getPoint2().getX())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX() + expandby, info.getPoint1().getY(), info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX() + expandby, info.getPoint2().getY(), info.getPoint2().getZ()));
                }
            }
            else if (z == -1)
            {
                if (info.getPoint1().getZ() < info.getPoint2().getZ())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY(), info.getPoint1().getZ() - expandby));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY(), info.getPoint2().getZ() - expandby));
                }
            }
            else if (y == 1)
            {
                if (info.getPoint1().getY() > info.getPoint2().getY())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY() + expandby, info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY() + expandby, info.getPoint2().getZ()));
                }
            }
            else if (y == -1)
            {
                if (info.getPoint1().getY() < info.getPoint2().getY())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY() - expandby, info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY() - expandby, info.getPoint2().getZ()));
                }
            }
            OutputHandler.chatConfirmation(player, "Region expanded by: " + expandby);
            return;
        }
        else if (args.length == 2)
        {
            PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());
            int expandby = 0;
            try
            {
                expandby = Integer.decode(args[0]);
            }
            catch (Exception e)
            {
                try
                {
                    expandby = Integer.decode(args[1]);
                }
                catch (Exception ex)
                {
                    OutputHandler.chatError(player, "Neither " + args[0] + " or " + args[1] + " is a number.");
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("north") || args[1].equalsIgnoreCase("north"))
            {
                if (info.getPoint1().getZ() < info.getPoint2().getZ())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY(), info.getPoint1().getZ() - expandby));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY(), info.getPoint2().getZ() - expandby));
                }
            }
            else if (args[0].equalsIgnoreCase("east") || args[1].equalsIgnoreCase("east"))
            {
                if (info.getPoint1().getX() > info.getPoint2().getX())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX() + expandby, info.getPoint1().getY(), info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX() + expandby, info.getPoint2().getY(), info.getPoint2().getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("south") || args[1].equalsIgnoreCase("south"))
            {
                if (info.getPoint1().getZ() > info.getPoint2().getZ())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY(), info.getPoint1().getZ() + expandby));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY(), info.getPoint2().getZ() + expandby));
                }
            }
            else if (args[0].equalsIgnoreCase("west") || args[1].equalsIgnoreCase("west"))
            {
                if (info.getPoint1().getX() < info.getPoint2().getX())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX() - expandby, info.getPoint1().getY(), info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX() - expandby, info.getPoint2().getY(), info.getPoint2().getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("up") || args[1].equalsIgnoreCase("up"))
            {
                if (info.getPoint1().getZ() > info.getPoint2().getZ())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY() + expandby, info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY() + expandby, info.getPoint2().getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("down") || args[1].equalsIgnoreCase("down"))
            {
                if (info.getPoint1().getY() < info.getPoint2().getY())
                {
                    PlayerInfo.selectionProvider.setPoint1(player,new Point(info.getPoint1().getX(), info.getPoint1().getY() - expandby, info.getPoint1().getZ()));
                }
                else
                {
                    PlayerInfo.selectionProvider.setPoint2(player,new Point(info.getPoint2().getX(), info.getPoint2().getY() - expandby, info.getPoint2().getZ()));
                }
            }
            else
            {
                OutputHandler.chatError(player, "Invalid Direction");
            }
            OutputHandler.chatConfirmation(player, "Region expanded by: " + expandby);
            return;
        }
        else
        {
            throw new CommandException(getCommandUsage(player));
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.expand";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "//expand [direction] <number of blocks to expand> Expands the currently selected area.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }

}
