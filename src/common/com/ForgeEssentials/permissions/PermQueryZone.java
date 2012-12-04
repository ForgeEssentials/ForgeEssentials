package com.ForgeEssentials.permissions;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.event.Event.HasResult;

import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 * 
 */
@HasResult
public class PermQueryZone extends PermQueryBase
{
	public final Zone toCheck;

	public PermQueryZone(EntityPlayer player, String permission, String zoneID)
	{
		super(player, permission);
		toCheck = ZoneManager.getZone(zoneID);
	}
	
	public PermQueryZone(EntityPlayer player, String permission, Zone zone)
	{
		super(player, permission);
		toCheck = zone;
	}
	
	/**
	 * uses the WorldZone for the specified world
	 * @param world
	 */
	public PermQueryZone(EntityPlayer player, String permission, World world)
	{
		super(player, permission);
		toCheck = ZoneManager.getWorldZone(world);
	}
	
	/**
	 * Assumes GLOBAL as the zone
	 */
	public PermQueryZone(EntityPlayer player, String permission)
	{
		super(player, permission);
		toCheck = ZoneManager.GLOBAL;
	}
}
