package com.ForgeEssentials.permissions;

import java.util.HashMap;

public class GroupManager
{
	public static Group DEFAULT;
	public static HashMap<String, Group> groups = new HashMap<String, Group>();
	
	private Group loginGroup;
	
	public GroupManager()
	{
		Group defaults = new Group(PermissionsAPI.GROUP_DEFAULT);
		ConfigGroups config = new ConfigGroups();
	}
}
