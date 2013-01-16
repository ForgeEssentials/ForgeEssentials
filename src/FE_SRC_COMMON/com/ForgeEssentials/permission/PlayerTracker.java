package com.ForgeEssentials.permission;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		// add payer to DB if one doesn't exist...
		// add player to EntryPlayer groups.. if they are generated...
		SqlHelper.generatePlayer(player.username);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		// don;t care
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		// don;t care
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		// don;t care
	}

}
