package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTameable;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.ICommandSender;

public class CommandButcher extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "butcher";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int radius = 10;
		double centerX = sender.posX;
		double centerY = sender.posY;
		double centerZ = sender.posZ;

		if (args.length == 1)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.get("message.error.nan"));
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
				OutputHandler.chatError(sender, Localization.get("message.error.nan"));
			}
		}

		List<EntityLiving> entityList = (List<EntityLiving>) sender.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1, centerZ + radius + 1));

		int counter = 0;
		for (int i = 0; i < entityList.size(); i++)
		{
			EntityLiving entity = entityList.get(i);

			if (entity instanceof EntityPlayer || entity instanceof EntityVillager)
				continue;

			if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
				continue;

			counter++;
			entity.setDead();
		}
		OutputHandler.chatConfirmation(sender, Localization.formatLocalizedString("message.butchered", counter));
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
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
