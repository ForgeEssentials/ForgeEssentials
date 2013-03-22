package com.ForgeEssentials.WorldBorder;

import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.util.AreaSelector.Point;

@SaveableObject
public class WorldBorder
{
	@UniqueLoadingKey
	@SaveableField
	public String		zone;
	
	@SaveableField
	public Point		center;
	
	@SaveableField
	public int			rad;
	
	@SaveableField
	public byte			shapeByte; // 1 = square, 2 = round.
	
	@SaveableField
	public boolean		enabled;
	
	/**
	 * For new borders
	 * @param world
	 */
	public WorldBorder(Zone zone, Point center, int rad, byte shape)
	{
		if (zone.isGlobalZone() || zone.isWorldZone())
		{
			this.zone = zone.getZoneName();
			this.center = center;
			this.rad = rad;
			this.shapeByte = shape;
			this.enabled = true;
		}
		else
		{
			throw new RuntimeException(zone.getZoneName() + " is not the global zone or a worldzone");
		}
	}

	public WorldBorder(Zone zone)
	{
		if (zone.isGlobalZone() || zone.isWorldZone())
		{
			this.zone = zone.getZoneName();
			this.center = new Point(0, 0, 0);
			this.rad = 0;
			this.shapeByte = 0;
			this.enabled = false;
		}
		else
		{
			throw new RuntimeException(zone.getZoneName() + " is not the global zone or a worldzone");
		}
	}
	
	public WorldBorder(String zone, Object center, Object rad, Object shapeByte, Object enabled)
	{
		this.zone = zone;
		this.center = (Point) center;
		this.rad = (Integer) rad;
		this.shapeByte = (Byte) shapeByte;
		this.enabled = (Boolean) enabled;
	}

	@Reconstructor
	private static WorldBorder reconstruct(IReconstructData tag)
	{
		return new WorldBorder(tag.getUniqueKey(), tag.getFieldValue("center"), tag.getFieldValue("rad"), tag.getFieldValue("shapeByte"), tag.getFieldValue("enabled"));
	}
	
	public void check(EntityPlayerMP player)
	{
		// 1 = square
		if (shapeByte == 1)
		{
			int dist = ModuleWorldBorder.getDistanceRound(center, player);
			if (dist > rad)
			{
				ModuleWorldBorder.executeClosestEffects(this, dist, player);
			}
		}
		// 2 = round
		else if (shapeByte == 2)
		{
			if (player.posX < (center.x - rad))
			{
				ModuleWorldBorder.executeClosestEffects(this, player.posX - (center.x - rad), player);
			}
			if (player.posX > (center.x + rad))
			{
				ModuleWorldBorder.executeClosestEffects(this, player.posX - (center.x + rad), player);
			}
			if (player.posZ < (center.z - rad))
			{
				ModuleWorldBorder.executeClosestEffects(this, player.posZ - (center.z - rad), player);
			}
			if (player.posZ > (center.z + rad))
			{
				ModuleWorldBorder.executeClosestEffects(this, player.posZ - (center.z + rad), player);
			}
		}
	}

	public int getETA()
	{
		try
		{
			// 1 = square
			if (shapeByte == 1)
			{
				return (int) Math.pow((rad*2), 2);
			}
			// 2 = round
			else if (shapeByte == 2)
			{
				return (int) (Math.pow(rad, 2) * Math.PI);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public void save()
	{
		DataStorageManager.getReccomendedDriver().saveObject(ModuleWorldBorder.con, this);
	}

	public String getShape()
	{
		switch(shapeByte)
		{
			case 1:
				return "square";
			case 2:
				return "round";
			default:
				return "not set";
		}
	}
}
