package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet6SpawnPosition;

import com.ForgeEssentials.commands.CommandSetSpawn;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PlayerTrackerCommands implements IPlayerTracker
{

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		CommandSetSpawn.spawns.remove(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		// send to spawn point
		WarpPoint p = CommandSetSpawn.spawns.get(player.username);
		if (p != null)
		{
			FunctionHelper.setPlayer((EntityPlayerMP) player, p);
			player.posX = p.xd;
			player.posY = p.yd;
			player.posZ = p.zd;
		}
	}
}
