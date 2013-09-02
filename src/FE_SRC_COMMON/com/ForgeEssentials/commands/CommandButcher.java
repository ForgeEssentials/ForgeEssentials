package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.AxisAlignedBB;

import com.ForgeEssentials.api.EnumMobType;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.CommandButcherTickTask;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.tasks.TaskRegistry;

public class CommandButcher extends FEcmdModuleCommands
{
	public static List<String>	typeList	= new ArrayList<String>();

	static
	{
		for (EnumMobType type : EnumMobType.values())
		{
			typeList.add(type.name());
		}
	}

	@Override
	public String getCommandName()
	{
		return "butcher";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "-1");
		else if (args.length == 2)
			return getListOfStringsFromIterableMatchingLastWord(args, typeList);
		else
			return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int radius = -1;
		int X = (int) sender.posX;
		int Y = (int) sender.posY;
		int Z = (int) sender.posZ;
		String mobType = EnumMobType.HOSTILE.toString();

		if (args.length > 0)
		{
			radius = args[0].equalsIgnoreCase("world") ? -1 : parseIntWithMin(sender, args[0], 0);
		}
		if (args.length > 1)
		{
			if (typeList.contains(args[1]))
			{
				mobType = args[1];
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + FunctionHelper.niceJoin(typeList.toArray()));
				return;
			}
		}
		if (args.length > 2)
		{
			String splitter = "";
			if (args[2].contains(", "))
			{
				splitter = ", ";
			}
			else if (args[2].contains(","))
			{
				splitter = ",";
			}
			else if (args[2].contains(" "))
			{
				splitter = " ";
			}

			String[] split = args[2].split(splitter);
			if (split.length != 3)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "x, y, z");
				return;
			}
			else
			{
				X = parseInt(sender, split[0], sender.posX);
				Y = parseInt(sender, split[1], sender.posY);
				Z = parseInt(sender, split[2], sender.posZ);
			}
		}
		AxisAlignedBB pool = AxisAlignedBB.getAABBPool().getAABB(X - radius, Y - radius, Z - radius, X + radius + 1, Y + radius + 1, Z + radius + 1);
		TaskRegistry.registerTask(new CommandButcherTickTask(sender, mobType, pool, radius, sender.dimension));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int radius = -1;
		int worldID = 0;
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
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
			return;
		}
		if (args.length > 0)
		{
			radius = args[0].equalsIgnoreCase("world") ? -1 : parseIntWithMin(sender, args[0], 0);
		}
		if (args.length > 1)
		{
			if (typeList.contains(args[1]))
			{
				mobType = args[1];
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + FunctionHelper.niceJoin(typeList.toArray()));
				return;
			}
		}
		if (args.length > 2)
		{
			String splitter = "";
			if (args[2].contains(", "))
			{
				splitter = ", ";
			}
			else if (args[2].contains(","))
			{
				splitter = ",";
			}
			else if (args[2].contains(" "))
			{
				splitter = " ";
			}

			String[] split = args[2].split(splitter);
			if (split.length != 3)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "x, y, z");
				return;
			}
			else
			{
				x = parseInt(sender, split[0]);
				y = parseInt(sender, split[1]);
				z = parseInt(sender, split[2]);
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
				ChatUtils.sendMessage(sender, Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
		}
		WorldPoint center = new WorldPoint(worldID, x, y, z);
		AxisAlignedBB pool = AxisAlignedBB.getAABBPool().getAABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1, center.z + radius + 1);
		TaskRegistry.registerTask(new CommandButcherTickTask(sender, mobType, pool, radius, worldID));
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
		return RegGroup.OWNERS;
	}

}
