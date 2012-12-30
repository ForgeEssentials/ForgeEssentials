package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.FunctionHelper;

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
	 * NEVER includes the default group.
	 * @param player
	 */
	public static ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults)
	{
		TreeSet<Group> list = new TreeSet<Group>();
		Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(player));
		PlayerPermData playerData;

		while (zone != null)
		{
			playerData = PlayerManager.getPlayerData(zone.getZoneID(), player.username);
			for (String group : playerData.getGroupList())
			{
				if (!includeDefaults && group.equals(DEFAULT.name))
					continue;
				list.add(GroupManager.getGroupName(group));
			}
			
			
			zone = ZoneManager.getZone(zone.parent);
		}
		
		if (includeDefaults)
			list.add(DEFAULT);
		
		ArrayList<Group> returnable = new ArrayList<Group>();
		returnable.addAll(list);
		return returnable;
	}
	
	public static Group getHighestGroup(EntityPlayer player)
	{
		Group high;
		Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(player));
		PlayerPermData playerData;
		TreeSet<Group> list = new TreeSet<Group>();

		while (zone != null && list.size() <= 0)
		{
			playerData = PlayerManager.getPlayerData(zone.getZoneID(), player.username);
			
			if (playerData.getGroupList().isEmpty())
			{
				zone = ZoneManager.getZone(zone.parent);
				continue;
			}
			
			for (String group : playerData.getGroupList())
				list.add(GroupManager.getGroupName(group));
			
			zone = ZoneManager.getZone(zone.parent);
		}
		
		if (list.size() == 0)
			return DEFAULT;
		else
			return list.pollFirst();
	}
}
