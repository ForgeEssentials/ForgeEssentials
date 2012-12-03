package com.ForgeEssentials.permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigGroups
{
	public static File			groupsFile		= new File(ModulePermissions.permsFolder, "groups.cfg");

	public Configuration		config;

	private static final String	PREFIX			= "chatPrefix";
	private static final String	SUFFIX			= "chatSuffix";
	private static final String	PARENT			= "parent";
	private static final String	PROM_LADDERS	= "_PROMOTION_LADDERS_";

	public ConfigGroups()
	{
		config = new Configuration(groupsFile, true);

		boolean generateDefaults = false;

		// to be used lots of places...
		String gPromote;
		Group tempGroup; // temporary group.

		// check for other groups. or generate
		if (config.categories.get(ZoneManager.GLOBAL.getZoneID()) == null || doesCategoryHaveChilldren(config.categories.get(ZoneManager.GLOBAL.getZoneID())))
		{
			generateDefaults = true;
			tempGroup = new Group(PermissionsAPI.GROUP_MEMBERS);
			tempGroup.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_MEMBERS, PREFIX, "", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempGroup.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_MEMBERS, SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.groups.put(tempGroup.name, tempGroup);

			tempGroup = new Group(PermissionsAPI.GROUP_OWNERS);
			tempGroup.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_OWNERS, PREFIX, OutputHandler.GOLD + "[OWNER]" + OutputHandler.WHITE, "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempGroup.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_OWNERS, SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.groups.put(tempGroup.name, tempGroup);

			tempGroup = new Group(PermissionsAPI.GROUP_ZONE_ADMINS);
			tempGroup.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_ZONE_ADMINS, PREFIX, OutputHandler.GOLD + "[OWNER]" + OutputHandler.WHITE, "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempGroup.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_ZONE_ADMINS, SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.groups.put(tempGroup.name, tempGroup);
		}

		// default group
		{
			GroupManager.DEFAULT.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_DEFAULT, PREFIX, OutputHandler.GREY + "[Default]", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.DEFAULT.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_DEFAULT, SUFFIX, "+OutputHandler.WHITE", "text to go after the username in chat. format char: \u00a7 Only works with the Chat module installed").value;
		}

		// default Ladders...
		if (generateDefaults)
		{
			String[] ladder = config.get(ZoneManager.GLOBAL.getZoneID() + "." + PROM_LADDERS, "", new String[] { PermissionsAPI.GROUP_OWNERS, PermissionsAPI.GROUP_ZONE_ADMINS, PermissionsAPI.GROUP_MEMBERS, PermissionsAPI.GROUP_DEFAULT }).valueList;
			loadLadderFromList(ladder, ZoneManager.GLOBAL.getZoneID());
		}
		
		forceLoadConfig();

		// category comments since it doesn't set these if its null...
		config.addCustomCategoryComment(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_OWNERS, "Generated group for your conveniance");
		config.addCustomCategoryComment(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_MEMBERS, "Generated group for your conveniance");
		config.addCustomCategoryComment(ZoneManager.GLOBAL.getZoneID() + "." + PermissionsAPI.GROUP_DEFAULT, "very default of all default groups. " + config.NEW_LINE + " This is also used for blanket permissions that are not applied to players but to zones");
		config.addCustomCategoryComment(ZoneManager.GLOBAL.getZoneID() + "." + PROM_LADDERS, "Top is highest, botom is lowest. A group cannot be in 2 ladders at once.");
	}
	
	public void forceLoadConfig()
	{
		Group tempGroup;
		for (ConfigCategory cat : config.categories.values())
		{
			if (cat.isChild())
				continue;

			// all the world/Global categories now...

			ArrayList<String> childrenWithLadders = new ArrayList<String>();

			// Iterate through children.
			for (String group : getCategoryChildren(cat))
			{
				if (group.endsWith(PROM_LADDERS))
				{
					childrenWithLadders.add(group);
					continue;
				}
				else
				{
					// read group...
					tempGroup = new Group(getGroupNameFromCategory(group), cat.getQualifiedName());
					tempGroup.parent = config.get(group, PARENT, "", "the group from which this group will inherit permissions").value;
					tempGroup.prefix = config.get(group, PREFIX, "", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
					tempGroup.suffix = config.get(group, SUFFIX, "", "text to go after the username in chat. format char: \u00a7 Only works with the Chat module installed").value;

					for (Property prop : config.categories.get(group).getValues().values())
						if (prop.getName().equals(PREFIX) || prop.getName().equals(SUFFIX))
							continue;
						else
							tempGroup.addData(prop);

					GroupManager.groups.put(tempGroup.name, tempGroup);
				}
			}

			for (String group : childrenWithLadders)
				for (Property prop : config.categories.get(group).getValues().values())
				{
					if (!prop.isList())
						throw new RuntimeException("only ladders lists are allowed in the ladders ");
					loadLadderFromList(prop.valueList, cat.getQualifiedName());
				}
		}
	}
	
	public void forceSaveConfigs()
	{
		for (Group group: GroupManager.groups.values())
		{
			String category = (new StringBuilder()).append(group.zoneID).append('.').append(group.name).toString();
			config.get(category, PARENT, "").value = group.parent;
			config.get(category, PREFIX, "").value = group.prefix;
			config.get(category, SUFFIX, "").value = group.suffix;
			
			ConfigCategory cat = config.categories.get(category);
			cat.putAll(group.getData());
		}
	}

	private ArrayList<String> getCategoryChildren(ConfigCategory category)
	{
		ArrayList<String> categories = new ArrayList<String>();

		for (ConfigCategory cat : config.categories.values())
		{
			if (!cat.isChild())
				continue;

			if (cat.getQualifiedName().startsWith(category.getQualifiedName()))
				categories.add(cat.getQualifiedName());
		}

		return categories;
	}

	private boolean doesCategoryHaveChilldren(ConfigCategory cat)
	{
		boolean hasChildren = false;

		for (ConfigCategory other : config.categories.values())
		{
			if (!cat.isChild())
				continue;

			if (cat.getQualifiedName().startsWith(cat.getQualifiedName()))
				return true;
		}
		return false;
	}

	private String getGroupNameFromCategory(String qualifiedName)
	{
		String[] names = qualifiedName.split("\\" + config.CATEGORY_SPLITTER);

		if (names.length == 0)
			return qualifiedName;
		else
			return names[names.length - 1];
	}

	private void loadLadderFromList(String[] ladderList, String zoneID)
	{
		Group temp = null;
		// increments up to down.
		for (int i = ladderList.length - 2; i > 0; i--)
		{
			temp = GroupManager.getGroupName(ladderList[i]);
			if (temp == null)
				throw new RuntimeException("Non-existant group: " + ladderList[i]);
			temp.setLadderAbove(GroupManager.getGroupName(ladderList[i + 1]), zoneID);
		}
	}

}
