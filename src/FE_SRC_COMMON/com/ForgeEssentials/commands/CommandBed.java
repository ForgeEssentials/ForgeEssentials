package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBed extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "bed";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1 && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer player : players)
				{
					ChunkCoordinates spawn = player.getBedLocation();
					if (spawn != null)
					{
						if (player.worldObj.getBlockId(spawn.posX, spawn.posY + 1, spawn.posZ) == 0 && player.worldObj.getBlockId(spawn.posX, spawn.posY + 2, spawn.posZ) == 0)
						{
							PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
							((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
							player.sendChatToPlayer(Localization.get(Localization.SPAWNED));
						}
						else
						{
							player.sendChatToPlayer(Localization.get(Localization.NOROOM));
						}
					}
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			ChunkCoordinates spawn = sender.getBedLocation();
			if (spawn != null)
			{
				if (sender.worldObj.getBlockId(spawn.posX, spawn.posY + 1, spawn.posZ) == 0 && sender.worldObj.getBlockId(spawn.posX, spawn.posY + 2, spawn.posZ) == 0)
				{
					PlayerInfo.getPlayerInfo(sender.username).back = new WarpPoint(sender);
					((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, sender.rotationYaw, sender.rotationPitch);
					sender.sendChatToPlayer(Localization.get(Localization.SPAWNED));
				}
				else
				{
					sender.sendChatToPlayer(Localization.get(Localization.NOROOM));
				}
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer player : players)
				{
					ChunkCoordinates spawn = player.getBedLocation();
					if (spawn != null)
					{
						if (player.worldObj.getBlockId(spawn.posX, spawn.posY + 1, spawn.posZ) == 0 && player.worldObj.getBlockId(spawn.posX, spawn.posY + 2, spawn.posZ) == 0)
						{
							PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
							((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
							player.sendChatToPlayer(Localization.get(Localization.SPAWNED));
						}
						else
						{
							player.sendChatToPlayer(Localization.get(Localization.NOROOM));
						}
					}
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}
}
