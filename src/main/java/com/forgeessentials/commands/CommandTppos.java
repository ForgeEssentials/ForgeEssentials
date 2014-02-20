package com.forgeessentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTppos extends FEcmdModuleCommands
{

	/** Spawn point for each dimension */
	public static HashMap<Integer, Point>	spawnPoints	= new HashMap<Integer, Point>();

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
			int x = parseInt(sender, args[0], sender.posX), y = parseInt(sender, args[1], sender.posY), z = parseInt(sender, args[2], sender.posZ);
			EntityPlayerMP player = (EntityPlayerMP) sender;
			PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.username);
			playerInfo.back = new WarpPoint(player);
			CommandBack.justDied.remove(player.username);
			TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.cameraPitch, player.cameraYaw), player);
		}
		else
		{
			this.error(sender);
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
