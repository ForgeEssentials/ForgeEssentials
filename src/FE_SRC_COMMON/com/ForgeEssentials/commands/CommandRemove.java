package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandRemove extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "remove";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int radius = 10;
		int centerX = (int) sender.posX;
		int centerY = (int) sender.posY;
		int centerZ = (int) sender.posZ;

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
				centerX = Integer.parseInt(args[1]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
			}
			try
			{
				centerY = Integer.parseInt(args[2]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
			}
			try
			{
				centerZ = Integer.parseInt(args[3]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[3]));
			}
		}

		List<EntityItem> entityList = (List<EntityItem>) sender.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1, centerZ + radius + 1));

		int counter = 0;
		for (int i = 0; i < entityList.size(); i++)
		{
			EntityItem entity = entityList.get(i);
			counter++;
			entity.setDead();
		}
		OutputHandler.chatConfirmation(sender, Localization.format(Localization.REMOVED, counter));
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

		List<EntityItem> entityList = (List<EntityItem>) FunctionHelper.getDimension(center.dim).getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1, center.z + radius + 1));

		int counter = 0;
		for (int i = 0; i < entityList.size(); i++)
		{
			EntityItem entity = entityList.get(i);
			counter++;
			entity.setDead();
		}
		sender.sendChatToPlayer(Localization.format(Localization.REMOVED, counter));
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.commands." + getCommandName();
	}

}
