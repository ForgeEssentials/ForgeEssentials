package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTp extends FEcmdModuleCommands
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point>	spawnPoints	= new HashMap<Integer, Point>();

	@Override
	public String getCommandName()
	{
		return "tp";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				target = FunctionHelper.getPlayerForName(sender, args[0]);
			}
			if (target != null)
			{
				EntityPlayerMP player = (EntityPlayerMP) sender;
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
				playerInfo.back = new WarpPoint(player);
				TeleportCenter.addToTpQue(new WarpPoint(target), player);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else if (args.length == 2)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[0]);
				if (PlayerSelector.hasArguments(args[1]))
				{
					target = FunctionHelper.getPlayerForName(sender, args[1]);
				}
				if (target != null)
				{
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
					playerInfo.back = new WarpPoint(player);
					TeleportCenter.addToTpQue(new WarpPoint(target), player);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[1]));
					return;
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
		else if (args.length >= 3)
		{
			if (args.length == 3)
			{
				int x = parseInt(sender, args[0]), y = parseInt(sender, args[1]), z = parseInt(sender, args[2]);
				EntityPlayerMP player = (EntityPlayerMP) sender;
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
				playerInfo.back = new WarpPoint(player);
				TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
			}
			else if (args.length == 4)
			{
				int x = parseInt(sender, args[1]), y = parseInt(sender, args[2]), z = parseInt(sender, args[3]);
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player != null)
				{
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
					playerInfo.back = new WarpPoint(player);
					TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[0]);
				if (PlayerSelector.hasArguments(args[1]))
				{
					target = FunctionHelper.getPlayerForName(sender, args[1]);
				}
				if (target != null)
				{
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
					playerInfo.back = new WarpPoint(player);
					TeleportCenter.addToTpQue(new WarpPoint(target), player);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[1]));
					return;
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
		else if (args.length == 4)
		{
			int x = parseInt(sender, args[1]), y = parseInt(sender, args[2]), z = parseInt(sender, args[3]);
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
				playerInfo.back = new WarpPoint(player);
				TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX));
		}
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1 || args.length == 2)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
