package com.ForgeEssentials.core;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.misc.LoginMessage;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker{

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		PlayerInfo.getPlayerInfo(player.username);
		LoginMessage.sendLoginMessage(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		PlayerInfo.discardInfo(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		// Not sure if we need to do anything here.
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		// Not sure if we need to do anything here.
	}
}
