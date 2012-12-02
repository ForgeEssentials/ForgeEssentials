package com.ForgeEssentials.permissions;

import java.util.HashMap;

public class GroupManager
{
	public static Group DEFAULT;
	public static HashMap<String, Group> groups = new HashMap<String, Group>();
	
	private Group loginGroup;
	public ConfigGroups config;
	
	public GroupManager()
	{
		Group defaults = new Group(PermissionsAPI.GROUP_DEFAULT);
		config = new ConfigGroups();
	}
	
	/**
	 * 
	 * @param name of the group
	 * @return NULL if the group does not exist
	 */
	public static Group getGroupName(String name)
	{
		return groups.get(name);
	}
	
	
}
