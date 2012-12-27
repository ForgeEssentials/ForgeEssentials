package com.ForgeEssentials.permission;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.Property;

public class Group implements Comparable
{
	private HashMap<String, String>		ladderNames;	// zoneID, ladderName
	private HashMap<String, Property>	extraData;		// tag based extra data
	public String						parent;
	public String						prefix;
	public String						suffix;
	public final String					name;
	public final String					zoneID;
	public int							priority;		// lowest priority is 0

	public Group(String name)
	{
		this(name, ZoneManager.GLOBAL.getZoneID());
	}

	public Group(String name, String zone)
	{
		this.name = name;
		zoneID = zone;
		ladderNames = new HashMap<String, String>();
		extraData = new HashMap<String, Property>();
	}

	/**
	 * You really shouldn't use this.. get the ladder somehow and check that...
	 * @param zoneID when in doubt use GLOBAL
	 */
	public String getPromotion(String zoneID)
	{
		return ZoneManager.getZone(zoneID).getLadder(ladderNames.get(zoneID)).getPromotion(name);
	}

	/**
	 * You really shouldn't use this.. get the ladder somehow and check that...
	 * @param zoneID when in doubt use GLOBAL
	 */
	public String getDemotion(String zoneID)
	{
		return ZoneManager.getZone(zoneID).getLadder(ladderNames.get(zoneID)).getDemotion(name);
	}

	public boolean hasParent()
	{
		return parent == null || parent.isEmpty();
	}

	public void addData(Property prop)
	{
		extraData.put(prop.getName(), prop);
	}

	public Property getData(String dataKey)
	{
		return extraData.get(dataKey);
	}

	public Map<String, Property> getData()
	{
		return extraData;
	}

	/**
	 * @param zoneID
	 * return NULL if this group has no ladder in this zone.
	 */
	public String getLadderName(String zoneID)
	{
		return ladderNames.get(zoneID);
	}
	
	@Override
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Group))
			return Integer.MIN_VALUE;
		
		Group g = (Group) obj;
		
		if (this.equals(g))
			return 0;
		
		Zone my = ZoneManager.getZone(zoneID);
		Zone their = ZoneManager.getZone(g.zoneID);
		
		int end = my.compareTo(their);
		
		if (end == 0)
			end =  priority - their.priority;
		
		if (end == 0)
			throw new RuntimeException("COLLIDING GROUPS! "+this.name+" in "+this.zoneID+" : -- : "+g.name+" in "+g.zoneID);
		
		return end;
	}

}
