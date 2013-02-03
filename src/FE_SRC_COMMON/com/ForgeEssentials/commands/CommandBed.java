package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBed extends ForgeEssentialsCommandBase
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

	@Override
	public String getCommandName()
	{
		return "bed";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer player = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (player != null)
			{
				ChunkCoordinates spawn = player.getBedLocation();
				if (spawn != null)
				{
					if (player.worldObj.getBlockId(spawn.posX, spawn.posY + 1, spawn.posZ) == 0 && player.worldObj.getBlockId(spawn.posX, spawn.posY + 2, spawn.posZ) == 0)
					{
						PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
						((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
						player.sendChatToPlayer(Localization.get(Localization.SPAWNED));
					} else
						player.sendChatToPlayer(Localization.get(Localization.NOROOM));
				}
			} else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		} else
		{
			ChunkCoordinates spawn = sender.getBedLocation();
			if (spawn != null)
			{
				if (sender.worldObj.getBlockId(spawn.posX, spawn.posY + 1, spawn.posZ) == 0 && sender.worldObj.getBlockId(spawn.posX, spawn.posY + 2, spawn.posZ) == 0)
				{
					PlayerInfo.getPlayerInfo(sender.username).back = new WarpPoint(sender);
					((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, sender.rotationYaw, sender.rotationPitch);
					sender.sendChatToPlayer(Localization.get(Localization.SPAWNED));
				} else
					sender.sendChatToPlayer(Localization.get(Localization.NOROOM));
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer player = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (player != null)
			{
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
				ChunkCoordinates spawn = player.getBedLocation();
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
				player.sendChatToPlayer(Localization.get(Localization.SPAWNED));
			} else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		} else
		{
			return null;
		}
	}
}
