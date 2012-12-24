package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.Property;

public class PlayerPermData
{
	private HashMap<String, Property>	extraData;	// tag based extra data
	public String						prefix;
	public String						suffix;
	private ArrayList<String>			groupList;
	public final String					username;
	public final String					zoneID;

	public PlayerPermData(String username)
	{
		this(username, ZoneManager.GLOBAL.getZoneID());
	}

	public PlayerPermData(String username, String ZoneID)
	{
		this.username = username;
		zoneID = ZoneID;
		extraData = new HashMap<String, Property>();
		groupList = new ArrayList<String>();
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

	public void removeFromGroup(String group)
	{
		if (group != null)
			groupList.remove(group);
	}

	public void addGroup(String group)
	{
		if (group != null && !groupList.contains(group))
			groupList.add(group);
	}

	public void addGroupAll(String[] groups)
	{
		if (groups != null)
			for (String group : groups)
				if (!groupList.contains(group))
					groupList.add(group);
	}
	
	public ArrayList<String> getGroupList()
	{
		return groupList;
	}
}
