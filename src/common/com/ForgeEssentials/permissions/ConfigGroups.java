package com.ForgeEssentials.permissions;

import java.io.File;
import java.util.HashSet;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.core.ForgeEssentials;

public class ConfigGroups
{
	public static File		groupsFile	= new File(ModulePermissions.permsFolder, "groups.cfg");

	public Configuration	config;
	
	private String defaultGroup;

	public ConfigGroups()
	{
		config = new Configuration(groupsFile, true);
		
		defaultGroup = GroupManager.DEFAULT.name;

		// default group
		if (config.categories.containsKey(defaultGroup))
		{
			config.addCustomCategoryComment(defaultGroup, "very default of all default groups. "+config.NEW_LINE+" This is also used for blanket permissions that are not applied to players but to zones");
			//config...
			config.get(defaultGroup, "promoteGroup", "", "The group to which this group will promote to");
			config.get(defaultGroup, "chatPrefix", "", "text to go before the username in chat. format char: \u00a7");
			config.get(defaultGroup, "chatSuffix", "", "text to go after the username in chat. format char: \u00a7");
		}
	}
}
