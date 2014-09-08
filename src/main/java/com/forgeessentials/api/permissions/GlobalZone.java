package com.forgeessentials.api.permissions;

import net.minecraft.entity.player.EntityPlayer;

public class GlobalZone extends Zone {

	@Override
	public boolean isPlayerInZone(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "GLOBAL";
	}

}
