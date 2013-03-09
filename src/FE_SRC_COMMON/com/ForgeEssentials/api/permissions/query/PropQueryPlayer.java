package com.ForgeEssentials.api.permissions.query;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class PropQueryPlayer extends PropQuerySpot
{
	public final String username;

	public PropQueryPlayer(EntityPlayer player, String permKey, PermPropType type)
	{
		super(new WorldPoint(player), permKey, type);
		username = player.username;
	}
	
	public PropQueryPlayer(EntityPlayer player, WorldPoint p, String permKey, PermPropType type)
	{
		super(p, permKey, type);
		username = player.username;
	}
	
	public PropQueryPlayer(String player, WorldPoint p, String permKey, PermPropType type)
	{
		super(p, permKey, type);
		username = player;
	}

}
