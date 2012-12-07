package com.ForgeEssentials.permission;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraftforge.common.Property;

public class PlayerPermData
{
	private HashMap<String, Property>	extraData;		// tag based extra data
	private String						parent;
	public String						prefix;
	public String						suffix;
	public String 						group = GroupManager.DEFAULT.name;
	public final String					username;
	public final String					zoneID;
	
	public PlayerPermData(String username)
	{
		this.username = username;
		this.zoneID = ZoneManager.GLOBAL.getZoneID();
		extraData = new HashMap<String, Property>();
	}
	
	public PlayerPermData(String username, String ZoneID)
	{
		this.username = username;
		this.zoneID = ZoneID;
		extraData = new HashMap<String, Property>();
	}
	
	public boolean hasParent()
	{
		return parent == null || parent.isEmpty();
	}
	
	public boolean isParentGroup()
	{
		return parent.toLowerCase().startsWith("g:");
	}
	
	public boolean isParentPlayer()
	{
		return parent.toLowerCase().startsWith("p:");
	}

	public String getParent()
	{
		return parent.substring(2);
	}

	public void setParent(String parent)
	{
		String lowP = parent.toLowerCase(Locale.US);
		if (parent == null || parent.isEmpty() ||parent.startsWith("p:") || parent.startsWith("p:"))
			this.parent = parent;
		else
			throw new RuntimeException("unusable parent for player");
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
