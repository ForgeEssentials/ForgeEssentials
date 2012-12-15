package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

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
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
			}
		} else if (args.length == 4)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
			}
			try
			{
				centerX = Double.parseDouble(args[1]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
			}
			try
			{
				centerY = Double.parseDouble(args[2]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
			}
			try
			{
				centerZ = Double.parseDouble(args[3]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[3]));
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
		OutputHandler.chatConfirmation(sender, Localization.format(Localization.BUTCHERED, counter));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int radius = 0;
		WorldPoint center = new WorldPoint(0, 0, 0, 0);

		if (args.length >= 4)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
			}
			try
			{
				center.x = Integer.parseInt(args[1]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[1]));
			}
			try
			{
				center.y = Integer.parseInt(args[2]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
			}
			try
			{
				center.z = Integer.parseInt(args[3]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
			}
			if (args.length >= 5)
			{
				try
				{
					center.dim = Integer.parseInt(args[3]);
				} catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				}
			}
		} else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX));
			return;
		}

		List<EntityLiving> entityList = (List<EntityLiving>) FunctionHelper.getDimension(center.dim).getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1, center.z + radius + 1));

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
		sender.sendChatToPlayer(Localization.format(Localization.BUTCHERED, counter));
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
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
