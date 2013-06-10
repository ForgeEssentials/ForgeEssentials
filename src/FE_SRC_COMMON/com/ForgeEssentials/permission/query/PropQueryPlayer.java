package com.ForgeEssentials.permission.query;

import net.minecraft.entity.player.EntityPlayer;

public abstract class PropQueryPlayer extends PropQuery
{
	public final EntityPlayer	player;

	public PropQueryPlayer(EntityPlayer player, String permKey)
	{
		super(permKey);
		this.player = player;
	}

}
