package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
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
				CommandBack.justDied.remove(player.username);
				TeleportCenter.addToTpQue(new WarpPoint(target), player);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else if (args.length == 2 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{

			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[1]);

				if (target != null)
				{
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
					playerInfo.back = new WarpPoint(player);
					WarpPoint point = new WarpPoint(target);
					FunctionHelper.setPlayer(player, point);
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
			    EntityPlayerMP player = (EntityPlayerMP) sender;
			    int x = parseInt(sender, args[0], player.posX), y = parseInt(sender, args[1], player.posY), z = parseInt(sender, args[2], player.posZ);
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
				playerInfo.back = new WarpPoint(player);
				TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
			}
			else if (args.length == 4)
			{
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player != null)
				{
				    int x = parseInt(sender, args[1], player.posX), y = parseInt(sender, args[2], player.posY), z = parseInt(sender, args[3], player.posZ);
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
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
			    int x = parseInt(sender, args[1], player.posX), y = parseInt(sender, args[2], player.posY), z = parseInt(sender, args[3], player.posZ);
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
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX));
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
