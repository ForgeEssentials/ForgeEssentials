package com.forgeessentials.util.selections;

//Depreciated

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandExpand extends ForgeEssentialsCommandBase
{

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
    public void processCommandPlayer(EntityPlayerMP player, String[] args) throws CommandException
    {
        Selection sel = SelectionHandler.getSelection(player);
        if (sel == null)
            throw new TranslatedCommandException("Invalid selection.");

        if (args.length == 1)
        {
            int x = Math.round((float) player.getLookVec().xCoord);
            int y = Math.round((float) player.getLookVec().yCoord);
            int z = Math.round((float) player.getLookVec().zCoord);
            int expandby = Integer.decode(args[0]);

            if (x == -1)
            {
                if (sel.getStart().getX() < sel.getEnd().getX())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX() - expandby, sel.getStart().getY(), sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX() - expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
            }
            else if (z == 1)
            {
                if (sel.getStart().getZ() < sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() + expandby));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() + expandby));
                }
            }
            else if (x == 1)
            {
                if (sel.getStart().getX() < sel.getEnd().getX())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX() + expandby, sel.getStart().getY(), sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX() + expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
            }
            else if (z == -1)
            {
                if (sel.getStart().getZ() < sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() - expandby));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() - expandby));
                }
            }
            else if (y == 1)
            {
                if (sel.getStart().getY() > sel.getEnd().getY())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY() + expandby, sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY() + expandby, sel.getEnd().getZ()));
                }
            }
            else if (y == -1)
            {
                if (sel.getStart().getY() < sel.getEnd().getY())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY() - expandby, sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY() - expandby, sel.getEnd().getZ()));
                }
            }
            ChatOutputHandler.chatConfirmation(player, "Region expanded by: " + expandby);
            return;
        }
        else if (args.length == 2)
        {
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
                    throw new TranslatedCommandException("Neither %s or %s is a number", args[0], args[1]);
                }
            }
            if (args[0].equalsIgnoreCase("north") || args[1].equalsIgnoreCase("north"))
            {
                if (sel.getStart().getZ() < sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() - expandby));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() - expandby));
                }
            }
            else if (args[0].equalsIgnoreCase("east") || args[1].equalsIgnoreCase("east"))
            {
                if (sel.getStart().getX() > sel.getEnd().getX())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX() + expandby, sel.getStart().getY(), sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX() + expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("south") || args[1].equalsIgnoreCase("south"))
            {
                if (sel.getStart().getZ() > sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() + expandby));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() + expandby));
                }
            }
            else if (args[0].equalsIgnoreCase("west") || args[1].equalsIgnoreCase("west"))
            {
                if (sel.getStart().getX() < sel.getEnd().getX())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX() - expandby, sel.getStart().getY(), sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX() - expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("up") || args[1].equalsIgnoreCase("up"))
            {
                if (sel.getStart().getZ() > sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY() + expandby, sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY() + expandby, sel.getEnd().getZ()));
                }
            }
            else if (args[0].equalsIgnoreCase("down") || args[1].equalsIgnoreCase("down"))
            {
                if (sel.getStart().getY() < sel.getEnd().getY())
                {
                    SelectionHandler.setStart(player, new Point(sel.getStart().getX(), sel.getStart().getY() - expandby, sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(sel.getEnd().getX(), sel.getEnd().getY() - expandby, sel.getEnd().getZ()));
                }
            }
            else
                throw new TranslatedCommandException("Invalid Direction");
            ChatOutputHandler.chatConfirmation(player, "Region expanded by: " + expandby);
            return;
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(player));
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
    public PermissionLevel getPermissionLevel()
    {

        return PermissionLevel.TRUE;
    }

}
