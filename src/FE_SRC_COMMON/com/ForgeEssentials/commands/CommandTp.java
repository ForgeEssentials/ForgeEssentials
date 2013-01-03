package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTp extends ForgeEssentialsCommandBase
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

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
			EntityPlayer target = FunctionHelper.getPlayerFromUsername(args[0]);
			if (target != null)
			{
				EntityPlayerMP player = (EntityPlayerMP)sender;
				if(player.dimension != target.dimension)
				{
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, target.dimension);
				}
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
				playerInfo.back = new WorldPoint(player);
				player.setPositionAndRotation(target.posX, target.posY, target.posZ, target.cameraYaw, target.cameraPitch);
				player.sendChatToPlayer("Poof!");
			}
			else
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
		}
		else if(args.length == 2)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerFromUsername(args[0]);
			EntityPlayer target = FunctionHelper.getPlayerFromUsername(args[1]);
			if (player != null && target != null)
			{
				if(player.dimension != target.dimension)
				{
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, target.dimension);
				}
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
				playerInfo.back = new WorldPoint(player);
				player.setPositionAndRotation(target.posX, target.posY, target.posZ, target.cameraYaw, target.cameraPitch);
				player.sendChatToPlayer("Poof!");
			}
			else
			{
				if(player == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
				if(target == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[1]));
				}
				return;
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if(args.length == 2)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerFromUsername(args[0]);
			EntityPlayer target = FunctionHelper.getPlayerFromUsername(args[1]);
			if (player != null && target != null)
			{
				if(player.dimension != target.dimension)
				{
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, target.dimension);
				}
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
				playerInfo.back = new WorldPoint(player);
				player.setPositionAndRotation(target.posX, target.posY, target.posZ, target.cameraYaw, target.cameraPitch);
				player.sendChatToPlayer("Poof!");
			}
			else
			{
				if(player == null)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
				if(target == null)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[1]));
				}
				return;
			}
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
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	if(args.length == 1 || args.length == 2)
    	{
    		return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
    	}
    	else
    	{
    		return null;
    	}
    }
}
