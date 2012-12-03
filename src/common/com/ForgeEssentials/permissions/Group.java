package com.ForgeEssentials.permissions;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.Property;

public class Group
{
	private HashMap<String, String>		promote;
	private HashMap<String, String>		demote;
	private HashMap<String, Property>	extraData;
	public String						parent;
	public String						prefix;
	public String						suffix;
	public final String					name;
	public final String					zoneID;

	public Group(String name)
	{
		this(name, ZoneManager.GLOBAL.getZoneID());
	}

	public Group(String name, String zone)
	{
		this.name = name;
		zoneID = zone;
		promote = new HashMap<String, String>();
		demote = new HashMap<String, String>();
		extraData = new HashMap<String, Property>();
	}

	/**
	 * Sets what group is the promotion of this one.
	 * @param above Group to promote to when /promote command is used
	 */
	public void setLadderAbove(Group above, String zoneID)
	{
		if (above == null)
		{
			promote.remove(zoneID);
			return;
		}

		promote.put(zoneID, above.name);
		above.demote.put(zoneID, name);
	}

	/**
	 * 
	 * @param zoneID when in doubt use GLOBAL
	 */
	public String getPromotion(String zoneID)
	{
		return promote.get(zoneID);
	}

	/**
	 * 
	 * @param zoneID when in doubt use GLOBAL
	 */
	public String getDemotion(String zoneID)
	{
		return demote.get(zoneID);
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

}
