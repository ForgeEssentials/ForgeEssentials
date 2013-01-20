package com.ForgeEssentials.permission;

import net.minecraft.world.World;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldArea;

@SaveableObject
public class Zone extends WorldArea implements Comparable
{
	@SaveableField
	public int priority; // lowest priority is 0

	@UniqueLoadingKey
	@SaveableField
	private String zoneID; // unique string name

	@SaveableField
	public String parent; // the unique name of the parent.

	public Zone(String name, Selection sel, Zone parent)
	{
		super(parent.dim, sel);
		zoneID = name;
		this.parent = parent.zoneID;
	}

	public Zone(String name, Selection sel, World world)
	{
		super(world, sel);
		zoneID = name;
		parent = FunctionHelper.getZoneWorldString(world);
	}

	/**
	 * used to construct Global and World zones.
	 * 
	 * @param name
	 */
	public Zone(String name, int dimension)
	{
		super(dimension, new Point(0, 0, 0), new Point(0, 0, 0));
		zoneID = name;

		if (!name.equals("_GLOBAL_"))
		{
			parent = ZoneManager.GLOBAL.zoneID;
		}
	}

	// used for reconstruct method only.
	private Zone(Selection sel, int dim)
	{
		super(dim, sel.getLowPoint(), sel.getHighPoint());
	}

	public boolean isParentOf(Zone zone)
	{
		if (parent == null)
		{
			return true;
		}
		else if (zoneID.equals(zone.parent))
		{
			return true;
		}
		else if (zone.parent == null)
		{
			return false;
		}
		else if (zone.parent.equals(ZoneManager.GLOBAL.zoneID))
		{
			return false;
		}
		else
		{
			return isParentOf(ZoneManager.getZone(zone.parent));
		}
	}

	/**
	 * @return if this Permission is a child of the given Permission.
	 */
	public boolean isChildOf(Zone zone)
	{
		if (zone.parent == null)
		{
			return true;
		}
		else if (zone.parent.equals(ZoneManager.GLOBAL.zoneID))
		{
			return dim == zone.dim;
		}
		else if (zone.zoneID.equals(parent))
		{
			return true;
		}
		else
		{
			return ZoneManager.getZone(parent).isChildOf(zone);
		}
	}

	/**
	 * @return The Unique ID of this Zone.
	 */
	public String getZoneName()
	{
		return zoneID;
	}

	@Override
	public int compareTo(Object o)
	{
		Zone zone = (Zone) o;
		if (zone.isParentOf(this))
		{
			return -100;
		}
		else if (isParentOf(zone))
		{
			return 100;
		}
		else
		{
			return priority - zone.priority;
		}
	}

	public boolean isGlobalZone()
	{
		return parent == null;
	}

	public boolean isWorldZone()
	{
		if (parent == null)
			return false;
		return parent.equals(ZoneManager.GLOBAL.zoneID);
	}

	@Reconstructor
	private static Zone reconstruct(TaggedClass tag)
	{
		Point high = (Point) tag.getFieldValue("high");
		Point low = (Point) tag.getFieldValue("low");
		Selection sel = new Selection(high, low);
		int dim = (Integer) tag.getFieldValue("dim");

		Zone zone = new Zone(sel, dim);

		zone.zoneID = (String) tag.getFieldValue("zoneID");
		zone.parent = (String) tag.getFieldValue("parent");
		zone.priority = (Integer) tag.getFieldValue("priority");

		return zone;
	}
	
	@Override
	public String toString()
	{
		return zoneID+" "+super.toString();
	}
}
