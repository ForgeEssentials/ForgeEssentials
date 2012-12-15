package com.ForgeEssentials.core;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{	
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		PlayerInfo.getPlayerInfo(player);
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
		// This is old code. I don't think we need it if we don't track which dimension players are in.
		/*String oldWorld = player.worldObj.getWorldInfo().getWorldName() + "_" + player.worldObj.getWorldInfo().getDimension();
		PlayerInfo oldInfo = PlayerInfo.getPlayerInfo(player);

		// if different
		if (!oldWorld.equals(oldInfo.getWorldName()))
			// do the dimensionCHange stuff.. because he went from one world, to the spawn world.
			onPlayerChangedDimension(player);*/
	}
}
