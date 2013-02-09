package com.ForgeEssentials.api.permissions.query;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.HasResult;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;

/**
 * Reuslts are: default, allow, deny.
 * 
 * @author AbrarSyed
 * 
 */
@HasResult
public class PermQueryPlayerZone extends PermQueryPlayer
{
	public final Zone	toCheck;

	public PermQueryPlayerZone(EntityPlayer player, String permission, String zoneID)
	{
		this(player, permission, ZoneManager.getZone(zoneID));
	}

	public PermQueryPlayerZone(EntityPlayer player, String permission, Zone zone)
	{
		super(player, permission);
		toCheck = zone;
		checkForward = false;
	}

	/**
	 * uses the WorldZone for the specified world
	 * 
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
		toCheck = ZoneManager.getGLOBAL();
	}

	public PermQueryPlayerZone(EntityPlayer player, String permission, String zoneID, boolean checkForward)
	{
		this(player, permission, ZoneManager.getZone(zoneID));
		this.checkForward = checkForward;
	}

	public PermQueryPlayerZone(EntityPlayer player, String permission, Zone zone, boolean checkForward)
	{
		this(player, permission, zone);
		this.checkForward = checkForward;
	}

	public PermQueryPlayerZone(EntityPlayer player, String permission, World world, boolean checkForward)
	{
		this(player, permission, ZoneManager.getWorldZone(world));
		this.checkForward = checkForward;
	}

	public PermQueryPlayerZone(EntityPlayer player, String permission, boolean checkForward)
	{
		this(player, permission, ZoneManager.getGLOBAL());
		this.checkForward = checkForward;
	}
}
