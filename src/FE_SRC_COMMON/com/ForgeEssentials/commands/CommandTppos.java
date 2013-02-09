package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTppos extends ForgeEssentialsCommandBase
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

	@Override
	public String getCommandName()
	{
		return "tppos";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 3)
		{
			int x = 0, y = 0, z = 0;
			try
			{
				x = new Integer(args[0]);
			}
			catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
			try
			{
				y = new Integer(args[1]);
			}
			catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
				return;
			}
			try
			{
				z = new Integer(args[2]);
			}
			catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			EntityPlayerMP player = (EntityPlayerMP) sender;
			PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
			playerInfo.back = new WarpPoint(player);
			TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.cameraPitch, player.cameraYaw), player);
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
		}
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
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1 || args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
