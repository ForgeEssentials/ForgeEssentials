package com.ForgeEssentials.permission;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FlatFileGroups
{
	File file;

	public FlatFileGroups(File file)
	{
		this.file = new File(file, "groups.txt");
		
	}

	public HashMap<String, Object> load()
	{
		ArrayList<Group> groups = new ArrayList<Group>();
		ArrayList<PermissionHolder> ladders = new ArrayList<PermissionHolder>();
		HashMap<String, HashMap<String, ArrayList<String>>> connector = new HashMap<String, HashMap<String, ArrayList<String>>>(); 

		// TODO: load

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("groups", groups);
		map.put("ladders", ladders);
		map.put("connector", connector);
		return map;
	}

	public void save(ArrayList<Group> groups, ArrayList<PromotionLadder> ladders, HashMap<String, HashMap<String, ArrayList<String>>> connector)
	{
		// clear it.
		if (file.exists())
			file.delete();
		
		Configuration config = new Configuration(file);
		
		String cat;
		String[] list;
		for (Group g : groups)
		{
			cat = g.zoneName+"."+g.name;
			config.get(cat, "prefix", g.prefix);
			config.get(cat, "suffix", g.suffix);
			config.get(cat, "priority", g.priority);
			
			list = getPlayerArray(g.name, g.zoneName, connector);
			config.get(cat, "playersInGroup", list);
		}
		
		for (PromotionLadder ladder : ladders)
		{
			cat = ladder.zoneID+"."+"_LADDERS_";
			config.get(cat, ladder.name, ladder.getListGroup());
		}

		config.save();
	}
	
	/**
	 * @param name
	 * @param zone
	 * @return empty list if none..
	 */
	private String[] getPlayerArray(String name, String zone, HashMap<String, HashMap<String, ArrayList<String>>> connector)
	{
		HashMap<String, ArrayList<String>> map = connector.get(zone);
		if (map == null)
			return new String[] {};
		
		ArrayList list = map.get(name);
		if (list == null)
			return new String[] {};
		
		return (String[]) list.toArray(new String[list.size()]);
	}
	

	private ArrayList<String> getCategoryChildren(Configuration config, ConfigCategory category)
	{
		ArrayList<String> categories = new ArrayList<String>();

		for (ConfigCategory cat : config.categories.values())
		{
			if (!cat.isChild())
			{
				continue;
			}

			if (cat.getQualifiedName().startsWith(category.getQualifiedName()))
			{
				categories.add(cat.getQualifiedName());
			}
		}

		return categories;
	}

	private String getPlayerNameFromCategory(String qualifiedName)
	{
		String[] names = qualifiedName.split("\\" + Configuration.CATEGORY_SPLITTER);

		if (names.length == 0)
		{
			return qualifiedName;
		}
		else
		{
			return names[names.length - 1];
		}
	}

}
