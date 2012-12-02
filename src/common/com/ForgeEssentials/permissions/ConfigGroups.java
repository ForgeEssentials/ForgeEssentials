package com.ForgeEssentials.permissions;

import java.io.File;
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
	public static File		groupsFile	= new File(ModulePermissions.permsFolder, "groups.cfg");

	public Configuration	config;
	
	private static final String PREFIX = "chatPrefix";
	private static final String SUFFIX = "chatSuffix";
	private static final String PROM_LADDERS = "_PROMOTION_LADDERS_";

	public ConfigGroups()
	{
		config = new Configuration(groupsFile, true);
		
		boolean generateDefaults = false;
		
		// to be used lots of places...
		String gPromote;
		Group group; // temporary group.
		
		// check for other groups. or generate
		if (config.categories.size() == 0)
		{
			generateDefaults = true;
			
			group = new Group(PermissionsAPI.GROUP_MEMBERS);
			group.prefix = config.get(PermissionsAPI.GROUP_MEMBERS, PREFIX, "", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			group.suffix = config.get(PermissionsAPI.GROUP_MEMBERS, SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.groups.put(group.name, group);
			
			group = new Group(PermissionsAPI.GROUP_OWNERS);
			group.prefix = config.get(PermissionsAPI.GROUP_OWNERS, PREFIX, OutputHandler.GOLD+"[OWNER]"+OutputHandler.WHITE, "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			group.suffix = config.get(PermissionsAPI.GROUP_OWNERS, SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.groups.put(group.name, group);
			
			group = new Group(PermissionsAPI.GROUP_ZONE_ADMINS);
			group.prefix = config.get(PermissionsAPI.GROUP_ZONE_ADMINS, PREFIX, OutputHandler.GOLD+"[OWNER]"+OutputHandler.WHITE, "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			group.suffix = config.get(PermissionsAPI.GROUP_ZONE_ADMINS, SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.groups.put(group.name, group);
		}

		// default group
		{
			GroupManager.DEFAULT.prefix = config.get(PermissionsAPI.GROUP_DEFAULT, PREFIX, OutputHandler.GREY+"[Default]", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			GroupManager.DEFAULT.suffix = config.get(PermissionsAPI.GROUP_DEFAULT, SUFFIX, "+OutputHandler.WHITE", "text to go after the username in chat. format char: \u00a7 Only works with the Chat module installed").value;
		}
		
		// all other groups...
		for (ConfigCategory cat: config.categories.values())
		{
			if (cat.getQualifiedName().equals(GroupManager.DEFAULT) || cat.getQualifiedName().equals("PROM_LADDERS"))
				continue;
			
			if (cat.isChild())
				if (cat.getFirstParent().getQualifiedName().equals(PROM_LADDERS))
					continue;
				else
					throw new RuntimeException("nested categories not allowed in Groups.cfg");
			
			group = new Group(cat.getQualifiedName());
			group.prefix = config.get(group.name, PREFIX, "", "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			group.suffix = config.get(group.name, SUFFIX, "", "text to go after the username in chat. format char: \u00a7 Only works with the Chat module installed").value;
			GroupManager.groups.put(group.name, group);
		}
		
		// ladders...
		{
			if (generateDefaults)
			{
				config.get(PROM_LADDERS, "", new String[] {PermissionsAPI.GROUP_OWNERS, PermissionsAPI.GROUP_ZONE_ADMINS, PermissionsAPI.GROUP_MEMBERS, PermissionsAPI.GROUP_DEFAULT});
				
			}
		}
		
		
		// category comments
		config.addCustomCategoryComment(PermissionsAPI.GROUP_OWNERS, "Generated group for your conveniance");
		config.addCustomCategoryComment(PermissionsAPI.GROUP_MEMBERS, "Generated group for your conveniance");
		config.addCustomCategoryComment(PermissionsAPI.GROUP_DEFAULT, "very default of all default groups. " + config.NEW_LINE + " This is also used for blanket permissions that are not applied to players but to zones");
		config.addCustomCategoryComment(PROM_LADDERS, "Top is highest, botom is lowest. A group cannot be in 2 ladders at once.");
	}
}
