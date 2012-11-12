package com.ForgeEssentials.core;

import net.minecraft.src.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		PlayerInfo.readOrGenerateInfo(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		PlayerInfo.saveAndDiscardInfo(player);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		PlayerInfo.saveAndDiscardInfo(player);
		PlayerInfo.readOrGenerateInfo(player);
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
	}

}
