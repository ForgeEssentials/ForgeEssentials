package com.ForgeEssentials.permission.query;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.Zone;

public class PropQueryPlayerZone extends PropQueryPlayer
{
	public Zone		zone;
	public boolean	checkParents;

	public PropQueryPlayerZone(EntityPlayer player, String permKey, Zone zone, boolean checkParents)
	{
		super(player, permKey);
		this.zone = zone;
		this.checkParents = checkParents;
	}

}
