package com.ForgeEssentials.api.permissions.query;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public abstract class PropQueryPlayer extends PropQuery
{
	public final EntityPlayer player;

	public PropQueryPlayer(EntityPlayer player, String permKey)
	{
		super(permKey);
		this.player = player;
	}

}
