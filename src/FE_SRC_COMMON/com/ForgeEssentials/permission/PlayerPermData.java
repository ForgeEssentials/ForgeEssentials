package com.ForgeEssentials.permission;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.Property;

public class PlayerPermData
{
	private HashMap<String, Property>	extraData;								// tag based extra data
	public String						prefix;
	public String						suffix;
	public String						group	= GroupManager.DEFAULT.name;
	public final String					username;
	public final String					zoneID;

	public PlayerPermData(String username)
	{
		this.username = username;
		zoneID = ZoneManager.GLOBAL.getZoneID();
		extraData = new HashMap<String, Property>();
	}

	public PlayerPermData(String username, String ZoneID)
	{
		this.username = username;
		zoneID = ZoneID;
		extraData = new HashMap<String, Property>();
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
