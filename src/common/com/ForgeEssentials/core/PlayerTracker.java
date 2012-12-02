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
		String oldWorld = player.worldObj.getWorldInfo().getWorldName() + "_" + player.worldObj.getWorldInfo().getDimension();
		PlayerInfo oldInfo = PlayerInfo.getPlayerInfo(player);

		// if different
		if (!oldWorld.equals(oldInfo.getWorldName()))
			// do the dimensionCHange stuff.. because he went from one world, to the spawn world.
			onPlayerChangedDimension(player);
	}
}
