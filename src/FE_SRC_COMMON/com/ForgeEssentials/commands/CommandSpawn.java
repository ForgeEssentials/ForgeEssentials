package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
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

public class CommandSpawn extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "spawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
				return;
			}
			EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, args[0]);
			if (player != null)
			{
				ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
				ChunkCoordinates point = FunctionHelper.getDimension(0).provider.getSpawnPoint();

				// teleport
				server.transferPlayerToDimension((EntityPlayerMP) player, 0);
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.posX, point.posY, point.posZ, player.rotationPitch, player.rotationYaw);

				player.sendChatToPlayer(Localization.get("command.spawn.done"));
				return;
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
		else if (args.length == 0)
		{
			WarpPoint spawn;
			ChunkCoordinates point = FunctionHelper.getDimension(0).provider.getSpawnPoint();
			spawn = new WarpPoint(0, point.posX, point.posY, point.posZ, sender.rotationPitch, sender.rotationYaw);
			PlayerInfo.getPlayerInfo(sender.username).back = new WarpPoint(sender);
			TeleportCenter.addToTpQue(spawn, sender);
			sender.sendChatToPlayer(Localization.get("command.spawn.done"));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, args[0]);
			if (player != null)
			{
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);

				WarpPoint spawn;
				ChunkCoordinates point = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.getSpawnPoint();
				spawn = new WarpPoint(0, point.posX, point.posY, point.posZ, player.rotationPitch, player.rotationYaw);
				TeleportCenter.addToTpQue(spawn, player);
				player.sendChatToPlayer(Localization.get("command.spawn.done"));
				return;
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}
}
