package com.ForgeEssentials.permissions;

import java.io.File;
import java.util.HashSet;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigGroups
{
	public static File		groupsFile	= new File(ModulePermissions.permsFolder, "groups.cfg");

	public Configuration	config;

	private String			defaultGroup;

	public ConfigGroups()
	{
		config = new Configuration(groupsFile, true);

		defaultGroup = GroupManager.DEFAULT.name;
		
		// check for other groups. or generate
		if (config.categories.size() == 0)
		{
			config.addCustomCategoryComment("members", "Generated group for your conveniance");
			config.get("members", "promoteGroup", "owners", "The group to which this group will promote to");
			config.get("members", "chatPrefix", "", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed");
			config.get("members", "chatSuffix", "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed");
			
			config.addCustomCategoryComment("owners", "Generated group for your conveniance");
			config.get("owners", "promoteGroup", "", "The group to which this group will promote to");
			config.get("owners", "chatPrefix", OutputHandler.GOLD+"[OWNER]", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed");
			config.get("owners", "chatSuffix", "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed");
			
			
		}

		// default group
		{
			config.addCustomCategoryComment(defaultGroup, "very default of all default groups. " + config.NEW_LINE + " This is also used for blanket permissions that are not applied to players but to zones");
			String gPromote = config.get(defaultGroup, "promoteGroup", "members", "The group to which this group will promote to").value;
			String gPrefix = config.get(defaultGroup, "chatPrefix", OutputHandler.GREY+"[Default]", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			String gSuffix = config.get(defaultGroup, "chatSuffix", "", "text to go after the username in chat. format char: \u00a7 Only works with the Chat module installed").value;
		}
	}
}
