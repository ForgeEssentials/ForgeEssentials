package com.ForgeEssentials.permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

@SaveableObject
public class Zone extends AreaBase implements Comparable
{
	@SaveableField
	public int		priority;	// lowest priority is 0

	@UniqueLoadingKey
	@SaveableField
	private String	zoneID;		// unique string name

	@SaveableField
	public String	parent;	// the unique name of the parent.

	@SaveableField
	private int		dimension;	// the WorldString of world this zone exists in.

	public Zone(String name, Selection sel, Zone parent)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		this.zoneID = name;
		this.parent = parent.zoneID;
		dimension = parent.dimension;
	}

	public Zone(String name, Selection sel, World world)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		this.zoneID = name;
		parent = FunctionHelper.getZoneWorldString(world);
		dimension = world.provider.dimensionId;
	}

	/**
	 * used to construct Global and World zones.
	 * @param name
	 */
	public Zone(String name, World world)
	{
		super(new Point(0, 0, 0), new Point(0, 0, 0));
		this.zoneID = name;

		if (!name.equals("_GLOBAL_"))
		{
			parent = ZoneManager.GLOBAL.zoneID;
			dimension = world.provider.dimensionId;
		}
	}

	// used for reconstruct method only.
	private Zone(Selection sel)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
	}

	public boolean isParentOf(Zone zone)
	{
		if (parent == null)
			return true;
		else if (zoneID.equals(zone.parent))
			return true;
		else if (zone.parent == null)
			return false;
		else if (zone.parent.equals(ZoneManager.GLOBAL.zoneID))
			return false;
		else
			return isParentOf(ZoneManager.getZone(zone.parent));
	}

	/**
	 * @return if this Permission is a child of the given Permission.
	 */
	public boolean isChildOf(Zone zone)
	{
		if (zone.parent == null)
			return true;
		else if (zone.parent.equals(ZoneManager.GLOBAL.zoneID))
			return dimension == zone.dimension;
		else if (zone.zoneID.equals(parent))
			return true;
		else
			return ZoneManager.getZone(parent).isChildOf(zone);
	}

	/**
	 * @return The Unique ID of this Zone.
	 */
	public String getZoneID()
	{
		return zoneID;
	}

	@Override
	public int compareTo(Object o)
	{
		Zone zone = (Zone) o;
		if (zone.isParentOf(this))
			return -100;
		else if (isParentOf(zone))
			return 100;
		else
			return priority - zone.priority;
	}

	public boolean isGlobalZone()
	{
		return this.parent == null;
	}

	public boolean isWorldZone()
	{
		return this.parent.equals(ZoneManager.GLOBAL.zoneID);
	}

	@Reconstructor
	private static void reconstruct(TaggedClass tag)
	{
		Selection sel = new Selection((Point) tag.getFieldValue("high"), (Point) tag.getFieldValue("low"));

		Zone zone = new Zone(sel);

		zone.zoneID = (String) tag.getFieldValue("name");
		zone.parent = (String) tag.getFieldValue("parent");
		zone.dimension = (Integer) tag.getFieldValue("dimension");
		zone.priority = (Integer) tag.getFieldValue("priority");

		ZoneManager.zoneMap.put(zone.zoneID, zone);
	}
}
