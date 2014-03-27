package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.WorldPoint;

public class CommandRemove extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "remove";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int radius = 10;
		int centerX = (int) sender.posX;
		int centerY = (int) sender.posY;
		int centerZ = (int) sender.posZ;

		if (args.length == 1)
		{
			radius = parseIntWithMin(sender, args[0], 0);
		}
		else if (args.length == 4)
		{
			radius = parseIntWithMin(sender, args[0], 0);
			centerX = parseInt(sender, args[1], sender.posX);
			centerY = parseInt(sender, args[2], sender.posY);
			centerZ = parseInt(sender, args[3], sender.posZ);
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: " + getSyntaxPlayer(sender));
			return;
		}

		List<EntityItem> entityList = sender.worldObj.getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getAABBPool().getAABB(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1, centerZ + radius + 1));

		int counter = 0;
		for (int i = 0; i < entityList.size(); i++)
		{
			EntityItem entity = entityList.get(i);
			counter++;
			entity.setDead();
		}
		OutputHandler.chatConfirmation(sender, Localization.format("command.remove.done", counter));
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
			center.x = parseInt(sender, args[1]);
			center.y = parseInt(sender, args[2]);
			center.z = parseInt(sender, args[3]);
			if (args.length >= 5)
			{
				center.dim = parseInt(sender, args[3]);
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: " + getSyntaxConsole());
			return;
		}

		List<EntityItem> entityList = FunctionHelper.getDimension(center.dim).getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getAABBPool().getAABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1, center.z + radius + 1));

		int counter = 0;
		for (int i = 0; i < entityList.size(); i++)
		{
			EntityItem entity = entityList.get(i);
			counter++;
			entity.setDead();
		}
		OutputHandler.chatConfirmation(sender, Localization.format("command.remove.done", counter));
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.ZONE_ADMINS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
