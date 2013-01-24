package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

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

		Configuration config = new Configuration(file);
		
		PermissionHolder holder;
		String catName;
		boolean allowed;
		String[] split;
		for (ConfigCategory cat: config.categories.values())
		{
			if (!cat.isChild())
				continue;
			
			catName = cat.getQualifiedName();
			
			// ensures that the player and group catNameegories don't get in.
			if (catName.indexOf('.') == catName.lastIndexOf('.'))
				continue;
			
			split = splitCat(catName);
			
			if (catName.contains(".player."))
			{
				for (Property prop : cat.getValues().values())
				{
					holder = new PermissionHolder(split[1], prop.getName(), prop.getBoolean(false), split[0]);
					player.add(holder);
				}
			}
			else if (catName.contains(".group."))
			{
				for (Property prop : cat.getValues().values())
				{
					holder = new PermissionHolder(split[1], prop.getName(), prop.getBoolean(false), split[0]);
					group.add(holder);
				}
			}
		}

		HashMap<String, ArrayList<PermissionHolder>> map = new HashMap<String, ArrayList<PermissionHolder>>();
		map.put("playerPerms", player);
		map.put("groupPerms", group);
		return map;
	}

	public void save(ArrayList<PermissionHolder> players, ArrayList<PermissionHolder> groups)
	{
		// clear it.
		if (file.exists())
			file.delete();
		
		Configuration config = new Configuration(file);

		for (PermissionHolder holder : players)
		{
			config.get(holder.zone + ".player." + holder.target, holder.name, holder.allowed);
		}

		for (PermissionHolder holder : groups)
		{
			config.get(holder.zone + ".group." + holder.target, holder.name, holder.allowed);
		}
		
		config.addCustomCategoryComment(ZoneManager.GLOBAL.getZoneName()+".group."+PermissionsAPI.DEFAULT.name, "The group used to as a placeholder for zone flags and such.");

		config.save();
	}

	private String[] splitCat(String qualifiedName)
	{
		String[] names = qualifiedName.split("\\" + Configuration.CATEGORY_SPLITTER, 3);
		return new String[] {names[0], names[2]};
	}

}
