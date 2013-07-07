package com.ForgeEssentials.permission.query;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.AreaSelector.WorldPoint;

public class PropQueryPlayerSpot extends PropQueryPlayer
{
	public WorldPoint	spot;

	public PropQueryPlayerSpot(EntityPlayer player, String permKey)
	{
		super(player, permKey);
		spot = new WorldPoint(player);
	}

	public PropQueryPlayerSpot(EntityPlayer player, WorldPoint spot, String permKey)
	{
		super(player, permKey);
		this.spot = spot;
	}

}
