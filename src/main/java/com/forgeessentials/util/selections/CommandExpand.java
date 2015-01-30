package com.forgeessentials.util.selections;

//Depreciated

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
            if (SelectionHandler.selectionProvider.getPoint1(player) == null || SelectionHandler.selectionProvider.getPoint2(player) == null)
            {

                OutputHandler.chatError(player, "Invalid previous selection.");
                return;
            }

            if (x == -1)
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getX() < SelectionHandler.selectionProvider.getPoint2(player).getX())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX() - expandby, SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX() - expandby, SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ()));
                }
            }
            else if (z == 1)
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getZ() < SelectionHandler.selectionProvider.getPoint2(player).getZ())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ() + expandby));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ() + expandby));
                }
            }
            else if (x == 1)
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getX() < SelectionHandler.selectionProvider.getPoint2(player).getX())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX() + expandby, SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX() + expandby, SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ()));
                }
            }
            else if (z == -1)
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getZ() < SelectionHandler.selectionProvider.getPoint2(player).getZ())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ() - expandby));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ() - expandby));
                }
            }
            else if (y == 1)
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getY() > SelectionHandler.selectionProvider.getPoint2(player).getY())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY() + expandby, SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY() + expandby, SelectionHandler.selectionProvider.getPoint2(player).getZ()));
                }
            }
            else if (y == -1)
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getY() < SelectionHandler.selectionProvider.getPoint2(player).getY())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY() - expandby, SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY() - expandby, SelectionHandler.selectionProvider.getPoint2(player).getZ()));
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
                if (SelectionHandler.selectionProvider.getPoint1(player).getZ() < SelectionHandler.selectionProvider.getPoint2(player).getZ())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ() - expandby));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ() - expandby));
                }
            }
            else if (args[0].equalsIgnoreCase("east") || args[1].equalsIgnoreCase("east"))
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getX() > SelectionHandler.selectionProvider.getPoint2(player).getX())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX() + expandby, SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX() + expandby, SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("south") || args[1].equalsIgnoreCase("south"))
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getZ() > SelectionHandler.selectionProvider.getPoint2(player).getZ())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ() + expandby));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ() + expandby));
                }
            }
            else if (args[0].equalsIgnoreCase("west") || args[1].equalsIgnoreCase("west"))
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getX() < SelectionHandler.selectionProvider.getPoint2(player).getX())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX() - expandby, SelectionHandler.selectionProvider.getPoint1(player).getY(), SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX() - expandby, SelectionHandler.selectionProvider.getPoint2(player).getY(), SelectionHandler.selectionProvider.getPoint2(player).getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("up") || args[1].equalsIgnoreCase("up"))
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getZ() > SelectionHandler.selectionProvider.getPoint2(player).getZ())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY() + expandby, SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY() + expandby, SelectionHandler.selectionProvider.getPoint2(player).getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("down") || args[1].equalsIgnoreCase("down"))
            {
                if (SelectionHandler.selectionProvider.getPoint1(player).getY() < SelectionHandler.selectionProvider.getPoint2(player).getY())
                {
                    SelectionHandler.selectionProvider.setPoint1(player,new Point(SelectionHandler.selectionProvider.getPoint1(player).getX(), SelectionHandler.selectionProvider.getPoint1(player).getY() - expandby, SelectionHandler.selectionProvider.getPoint1(player).getZ()));
                }
                else
                {
                    SelectionHandler.selectionProvider.setPoint2(player,new Point(SelectionHandler.selectionProvider.getPoint2(player).getX(), SelectionHandler.selectionProvider.getPoint2(player).getY() - expandby, SelectionHandler.selectionProvider.getPoint2(player).getZ()));
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
