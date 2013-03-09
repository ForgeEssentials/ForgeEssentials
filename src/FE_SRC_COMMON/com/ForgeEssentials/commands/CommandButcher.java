package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.AxisAlignedBB;

import com.ForgeEssentials.api.commands.EnumMobType;
import com.ForgeEssentials.commands.util.CommandButcherTickTask;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.tasks.TaskRegistry;

public class CommandButcher extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "butcher";
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "-1");
		else if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, "passive", "villager", "hostile", "tamed", "all", "golem", "world", "boss");
		else
			return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int radius = 10;
		double centerX = sender.posX;
		double centerY = sender.posY;
		double centerZ = sender.posZ;
		String mobType = EnumMobType.HOSTILE.toString();

		if (args.length > 0)
		{
			try
			{
				radius = args[0].equalsIgnoreCase("world") ? -1 : Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
		}
		if (args.length > 1)
		{
			if (args[1].equalsIgnoreCase("golem") || args[1].equalsIgnoreCase("passive") || args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("villager") || args[1].equalsIgnoreCase("tamed") || args[1].equalsIgnoreCase("hostile")
					|| args[1].equalsIgnoreCase("boss"))
			{
				mobType = args[1];
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "all, boss, golem, hostile, passive, tamed, or villager");
				return;
			}
		}
		if (args.length > 2)
		{
			String[] split = args[2].split(",");
			if (split.length != 3)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "x,y,z");
				return;
			}
		}
		if (args.length > 3)
		{

		}
		TaskRegistry.registerTask(new CommandButcherTickTask(sender, mobType, AxisAlignedBB.getAABBPool().getAABB(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1, centerZ + radius + 1),
				radius, sender.dimension));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int radius = 10;
		int worldID = -2;
		int x = 0, y = 0, z = 0;
		if (sender instanceof TileEntityCommandBlock)
		{
			TileEntityCommandBlock cb = (TileEntityCommandBlock) sender;
			worldID = cb.worldObj.provider.dimensionId;
			x = cb.xCoord;
			y = cb.yCoord;
			z = cb.zCoord;
		}

		String mobType = "hostile";

		if (args.length != 4 && !(sender instanceof TileEntityCommandBlock))
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + "/butcher <radius> <type> <x,y,z> <worldID>");
			return;
		}
		if (args.length > 0)
		{
			try
			{
				radius = args[0].equalsIgnoreCase("world") ? -1 : Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
		}
		if (args.length > 1)
		{
			if (args[1].equalsIgnoreCase("golem") || args[1].equalsIgnoreCase("passive") || args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("villager") || args[1].equalsIgnoreCase("tamed") || args[1].equalsIgnoreCase("hostile")
					|| args[1].equalsIgnoreCase("boss"))
			{
				mobType = args[1];
			}
			else
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + "all, boss, golem, hostile, passive, tamed, or villager");
				return;
			}
		}
		if (args.length > 2)
		{
			String[] split = args[2].split(",");
			if (split.length != 3)
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + "x,y,z");
				return;
			}
			else
			{
				try
				{
					x = Integer.parseInt(split[0]);
					y = Integer.parseInt(split[1]);
					z = Integer.parseInt(split[2]);
				}
				catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
					return;
				}
			}
		}
		if (args.length == 4)
		{
			try
			{
				worldID = Integer.parseInt(args[3]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
		}
		WorldPoint center = new WorldPoint(worldID, x, y, z);
		TaskRegistry.registerTask(new CommandButcherTickTask(sender, mobType, AxisAlignedBB.getAABBPool().getAABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1,
				center.z + radius + 1), radius, worldID));
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

}
