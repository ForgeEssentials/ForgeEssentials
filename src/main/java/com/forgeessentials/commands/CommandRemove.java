package com.forgeessentials.commands;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.commons.selections.WorldPoint;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandRemove extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "remove";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        int radius = 10;
        double centerX;
        double centerY;
        double centerZ;

        if (args.length == 1)
        {
            radius = parseIntWithMin(sender, args[0], 0);
            centerX = sender.posX;
            centerY = sender.posY;
            centerZ = sender.posZ;
        }
        else if (args.length == 4)
        {
            radius = parseIntWithMin(sender, args[0], 0);
            centerX = parseDouble(sender, args[1], sender.posX);
            centerY = parseDouble(sender, args[2], sender.posY);
            centerZ = parseDouble(sender, args[3], sender.posZ);
        }
        else
        {
        	throw new WrongUsageException(getCommandUsage(sender));
        }

        List<EntityItem> entityList = sender.worldObj.getEntitiesWithinAABB(EntityItem.class,
                AxisAlignedBB.getBoundingBox(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1,
                        centerZ + radius + 1));

        int counter = 0;
        for (int i = 0; i < entityList.size(); i++)
        {
            EntityItem entity = entityList.get(i);
            counter++;
            entity.setDead();
        }
        OutputHandler.chatConfirmation(sender, String.format("%d items removed.", counter));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int radius = 0;
        WorldPoint center = new WorldPoint(0, 0, 0, 0);

        if (args.length >= 4)
        {
            radius = parseIntWithMin(sender, args[0], 0);
            center.setX(parseInt(sender, args[1]));
            center.setY(parseInt(sender, args[2]));
            center.setZ(parseInt(sender, args[3]));
            if (args.length >= 5)
            {
                center.setDimension(parseInt(sender, args[3]));
            }
        }
        else
        {
        	throw new WrongUsageException(getCommandUsage(sender));
        }

        List<EntityItem> entityList = DimensionManager.getWorld(center.getDimension()).getEntitiesWithinAABB(EntityItem.class,
                AxisAlignedBB.getBoundingBox(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius + 1, center.getY() + radius + 1,
                        center.getZ() + radius + 1));

        int counter = 0;
        for (int i = 0; i < entityList.size(); i++)
        {
            EntityItem entity = entityList.get(i);
            counter++;
            entity.setDead();
        }
        OutputHandler.chatConfirmation(sender, String.format("%d items removed.", counter));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
    	if (sender instanceof EntityPlayer)
        {
    		return "/remove <radius> [x, y, z] Removes all items within a specified radius from yourself or the given coordinates.";
        }
        else
        {
        	return "/remove <radius> <x, y, z> Removes all items within a specified radius from the given coordinates.";
        }
    }

}
