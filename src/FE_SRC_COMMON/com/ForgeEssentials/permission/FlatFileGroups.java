package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class FlatFileGroups
{
	File	file;

	public FlatFileGroups(File file)
	{
		this.file = new File(file, "groups.txt");
	}

	public HashMap<String, Object> load()
	{
		ArrayList<Group> groups = new ArrayList<Group>();
		ArrayList<PromotionLadder> ladders = new ArrayList<PromotionLadder>();
		HashMap<String, HashMap<String, String[]>> connector = new HashMap<String, HashMap<String, String[]>>();

		Configuration config = new Configuration(file);

		String prefix, suffix, parent;
		int priority;
		String[] split, players;
		Group g;
		PromotionLadder ladder;
		HashMap<String, String[]> playerMap;
		for (Entry<String, ConfigCategory> e : config.categories.entrySet())
		{
			if (!e.getValue().isChild())
				continue;

			split = e.getKey().split("\\" + Configuration.CATEGORY_SPLITTER);
			
			if (split[1].equalsIgnoreCase("_ladders_"))
			{
				for (Property prop : e.getValue().getValues().values())
				{
					ladder = new PromotionLadder(prop.getName(), split[0], prop.valueList);
					ladders.add(ladder);
				}
				continue;
			}
			
			prefix = config.get(e.getKey(), "prefix", " ").value;
			suffix = config.get(e.getKey(), "suffix", " ").value;
			parent = config.get(e.getKey(), "parent", "").value;
			priority = config.get(e.getKey(), "priority", 0).getInt();

			if (parent.isEmpty())
				parent = null;

			g = new Group(split[1], prefix, suffix, parent, split[0], priority);
			groups.add(g);
			
			// now for the player things...
			players  = config.get(e.getKey(), "playersInGroup", new String[] {}).valueList;
			playerMap = connector.get(split[0]);
			if (playerMap == null)
			{
				playerMap = new HashMap<String, String[]>();
				connector.put(split[0], playerMap);
			}
			playerMap.put(split[1], players);
		}

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
			if (g.name.equals(PermissionsAPI.DEFAULT.name))
				continue;
			
			cat = g.zoneName + "." + g.name;
			config.get(cat, "prefix", g.prefix);
			config.get(cat, "suffix", g.suffix);
			config.get(cat, "parent", g.parent);
			config.get(cat, "priority", g.priority);

			list = getPlayerArray(g.name, g.zoneName, connector);
			config.get(cat, "playersInGroup", list);
		}

		for (PromotionLadder ladder : ladders)
		{
			cat = ladder.zoneID + "." + "_LADDERS_";
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

}
