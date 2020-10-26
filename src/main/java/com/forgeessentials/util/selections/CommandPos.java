package com.forgeessentials.util.selections;

//Depreciated

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPos extends ForgeEssentialsCommandBase
{
    private int type;

    public CommandPos(int type)
    {
        this.type = type;
    }

    @Override
    public String getPrimaryAlias()
    {
        return "/fepos" + type;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP player, String[] args) throws CommandException
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
                    SelectionHandler.setStart(player, new Point(x, y, z));
                }
                else
                {
                    SelectionHandler.setEnd(player, new Point(x, y, z));
                }

                ChatOutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
                return;

            }
            else
            {
                throw new TranslatedCommandException(getUsage(player));
            }
        }

        if (args.length > 0)
        {
            if (args.length < 3)
            {
                throw new TranslatedCommandException(getUsage(player));
            }

            try
            {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e)
            {
                throw new TranslatedCommandException(getUsage(player));
            }

            if (type == 1)
            {
                SelectionHandler.setStart(player, new Point(x, y, z));
            }
            else
            {
                SelectionHandler.setEnd(player, new Point(x, y, z));
            }

            ChatOutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
            return;
        }

        RayTraceResult mop = PlayerUtil.getPlayerLookingSpot(player);

        if (mop == null)
            throw new TranslatedCommandException("You must first look at the ground!");

        x = mop.getBlockPos().getX();
        y = mop.getBlockPos().getY();
        z = mop.getBlockPos().getZ();

        WorldPoint point = new WorldPoint(player.dimension, x, y, z);
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(player), point, getPermissionNode()))
            throw new TranslatedCommandException("Insufficient permissions.");

        if (type == 1)
        {
            SelectionHandler.setStart(player, point);
        }
        else
        {
            SelectionHandler.setEnd(player, point);
        }

        ChatOutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
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
    public String getUsage(ICommandSender sender)
    {

        return "/" + getName() + " [<x> <y> <z] or [here] Sets selection positions";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

}
