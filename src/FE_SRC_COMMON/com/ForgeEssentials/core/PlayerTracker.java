package com.ForgeEssentials.core;

import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{	
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		PlayerInfo.getPlayerInfo(player);
		OutputHandler.chatConfirmation(player, "Forge Essentials is still in alpha. There are plenty of incomplete features in the mod. We hope to seek your understanding.");
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
		info.save();
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
