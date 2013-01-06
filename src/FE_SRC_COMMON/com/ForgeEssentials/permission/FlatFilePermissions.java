package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

public class FlatFilePermissions
{
	File file;

	public FlatFilePermissions(File file)
	{
		this.file = new File(file, "permissions.txt");
	}

	public HashMap<String, ArrayList<PermissionHolder>> load()
	{
		ArrayList<PermissionHolder> group = new ArrayList<PermissionHolder>();
		ArrayList<PermissionHolder> player = new ArrayList<PermissionHolder>();

		// TODO: save;

		HashMap<String, ArrayList<PermissionHolder>> map = new HashMap<String, ArrayList<PermissionHolder>>();
		map.put("player", player);
		map.put("group", group);
		return null;
	}

	public void save(ArrayList<PermissionHolder> players, ArrayList<PermissionHolder> groups)
	{
		Configuration config = new Configuration(file);

		for (PermissionHolder holder : players)
		{
			config.get(holder.zone + ".group." + holder.target, holder.name, holder.allowed);
		}

		for (PermissionHolder holder : groups)
		{
			config.get(holder.zone + ".group." + holder.target, holder.name, holder.allowed);
		}

		config.save();
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
