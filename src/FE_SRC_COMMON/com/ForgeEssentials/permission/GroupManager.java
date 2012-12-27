package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

import com.ForgeEssentials.util.FunctionHelper;

import net.minecraft.entity.player.EntityPlayer;

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

	/**
	 * Returns the list of all the groups the player is in at a given time. It is in order of priority the first bieng the highest.
	 * It will always have at least the DEFAULT groups.
	 * @param player
	 */
	public static ArrayList<Group> getApplicableGroups(EntityPlayer player)
	{
		TreeSet<Group> list = new TreeSet<Group>();
		Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(player));
		PlayerPermData playerData;

		while (zone != null)
		{
			playerData = PlayerManager.getPlayerData(zone.getZoneID(), player.username);
			for (String group : playerData.getGroupList())
				list.add(GroupManager.getGroupName(group));
			
			
			zone = ZoneManager.getZone(zone.parent);
		}
		
		list.add(DEFAULT);
		
		ArrayList<Group> returnable = new ArrayList<Group>();
		returnable.addAll(list);
		return returnable;
	}
}
