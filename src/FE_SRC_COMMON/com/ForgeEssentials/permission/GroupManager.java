package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupManager
{
	public static Group						DEFAULT;
	
	protected static HashMap<String, Group>	groups	= new HashMap<String, Group>();

	private Group							loginGroup;
	public ConfigGroup						config;

	public GroupManager()
	{
		DEFAULT = new Group(PermissionsAPI.GROUP_DEFAULT);
		loginGroup = DEFAULT;
		config = new ConfigGroup();
	}

	/**
	 * @param name of the group
	 * @return NULL if the group does not exist
	 */
	public static Group getGroupName(String name)
	{
		return groups.get(name);
	}

	/**
	 * Gets all the groups that were explicitly created in the given zone. these groups will only apply
	 * to the given Zone and all of its children.
	 * @param zoneID zone to check.
	 * @return List of Groups. may be an empty list, but never null.
	 */
	protected static ArrayList<Group> getAllGroupsCreatedForZone(String zoneID)
	{
		ArrayList<Group> gs = new ArrayList<Group>();
		for (Group g : groups.values())
			if (g.zoneID.equals(zoneID))
				gs.add(g);

		return gs;
	}
}
