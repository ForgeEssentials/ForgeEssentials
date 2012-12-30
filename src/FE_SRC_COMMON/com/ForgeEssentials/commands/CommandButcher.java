package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import com.ForgeEssentials.commands.util.CommandButcherTickTask;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandButcher extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "butcher";
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		return getListOfStringsMatchingLastWord(par2ArrayOfStr, "passive", "villager",
				"hostile", "tamed", "all", "golem", "world");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int radius = 10;
		double centerX = sender.posX;
		double centerY = sender.posY;
		double centerZ = sender.posZ;
		String mobType = "hostile";

		if(args.length > 0)
		{
			try
			{
				radius = args[0].equalsIgnoreCase("world") ? -1 : Integer.parseInt(args[0]);
			} catch(NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
		}
		if(args.length > 1)
		{
			if(args[1].equalsIgnoreCase("golem") || args[1].equalsIgnoreCase("passive") ||
				args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("villager") ||
				args[1].equalsIgnoreCase("tamed") || args[1].equalsIgnoreCase("hostile"))
			{
				mobType = args[1];
			}
			else
			{
				OutputHandler.chatError(sender, Localization.ERROR_BADSYNTAX + "all, golem, hostile, passive, tamed, or villager");
				return;
			}
		}
		if(args.length > 2)
		{
			String[] split = args[2].split(",");
			if(split.length != 3)
			{
				OutputHandler.chatError(sender, Localization.ERROR_BADSYNTAX + "x,y,z");
				return;
			}
		}
		if(args.length > 3)
		{
			
		}
		TickTaskHandler.addTask(new CommandButcherTickTask(sender, mobType, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(centerX - radius, centerY - radius, centerZ - radius, centerX + radius + 1, centerY + radius + 1, centerZ + radius + 1), radius));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int radius = 0;
		int worldID = -2;
		WorldPoint center = new WorldPoint(0, 0, 0, 0);

		String mobType = "hostile";
		
		if(args.length != 4)
		{
			sender.sendChatToPlayer(Localization.ERROR_BADSYNTAX + "/butcher <radius> <type> <x,y,z> <worldID>");
			return;
		}
		
		try
		{
			radius = args[0].equalsIgnoreCase("world") ? -1 : Integer.parseInt(args[0]);
		} catch(NumberFormatException e)
		{
			sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
			return;
		}
		if(args[1].equalsIgnoreCase("golem") || args[1].equalsIgnoreCase("passive") ||
			args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("villager") ||
			args[1].equalsIgnoreCase("tamed") || args[1].equalsIgnoreCase("hostile"))
		{
			mobType = args[1];
		}
		else
		{
			sender.sendChatToPlayer(Localization.ERROR_BADSYNTAX + "all, golem, hostile, passive, tamed, or villager");
			return;
		}
		String[] split = args[2].split(",");
		if(split.length != 3)
		{
			sender.sendChatToPlayer(Localization.ERROR_BADSYNTAX + "x,y,z");
			return;
		}
		try
		{
			worldID = Integer.parseInt(args[3]);
		} catch(NumberFormatException e)
		{
			sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
			return;
		}
		TickTaskHandler.addTask(new CommandButcherTickTask(sender, mobType, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1, center.z + radius + 1), radius, worldID));
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
