package com.ForgeEssentials.permission.query;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.HasResult;

import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 * 
 */
@HasResult
public class PermQueryPlayerZone extends PermQueryPlayer
{
	public final Zone toCheck;

	public PermQueryPlayerZone(EntityPlayer player, String permission, String zoneID)
	{
		super(player, permission);
		toCheck = ZoneManager.getZone(zoneID);
	}
	
	public PermQueryPlayerZone(EntityPlayer player, String permission, Zone zone)
	{
		super(player, permission);
		toCheck = zone;
	}
	
	/**
	 * uses the WorldZone for the specified world
	 * @param world
	 */
	public PermQueryPlayerZone(EntityPlayer player, String permission, World world)
	{
		super(player, permission);
		toCheck = ZoneManager.getWorldZone(world);
	}
	
	/**
	 * Assumes GLOBAL as the zone
	 */
	public PermQueryPlayerZone(EntityPlayer player, String permission)
	{
		super(player, permission);
		toCheck = ZoneManager.GLOBAL;
	}
}
