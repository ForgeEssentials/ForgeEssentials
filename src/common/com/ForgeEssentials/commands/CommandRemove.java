package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandRemove extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "remove";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int radius = 10;
		double centerX = player.posX;
		double centerY = player.posY;
		double centerZ = player.posZ;

		if (args.length == 1)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(player, "That won't work. try " + getSyntaxPlayer(player));
			}
		} else if (args.length == 4)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
				centerX = Double.parseDouble(args[1]);
				centerY = Double.parseDouble(args[2]);
				centerZ = Double.parseDouble(args[3]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(player, "That won't work. try " + getSyntaxPlayer(player));
			}
		}

		List<EntityItem> entityList = (List<EntityItem>) player.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1, centerZ + radius + 1));

		int counter = 0;
		for (int i = 0; i < entityList.size(); i++)
		{
			EntityItem entity = entityList.get(i);
			counter++;
			entity.setDead();
		}

		OutputHandler.chatConfirmation(player, counter + " item" + (counter == 1 ? "" : "s") + " removed");

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// check permissions.
		return true;
	}

	@Override
	public String getSyntaxConsole()
	{
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/remove [radius] [<x> <y> <z>]";
	}

	@Override
	public String getInfoConsole()
	{
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Removes all item entities around you/the specifies point within the radius";
	}

}
