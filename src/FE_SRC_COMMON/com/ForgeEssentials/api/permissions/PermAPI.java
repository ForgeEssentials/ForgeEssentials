package com.ForgeEssentials.api.permissions;

import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.permission.query.PermQuery;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public abstract class PermAPI
{
	public static boolean checkPermAllowed(PermQuery query)
	{
		return PermissionsAPI.checkPermAllowed(query);
	}
	
	public static PermResult checkPermResult(PermQuery query)
	{
		return PermissionsAPI.checkPermResult(query);
	}
	
	public static Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority)
	{
		return PermissionsAPI.createGroupInZone(groupName, zoneName, prefix, suffix, parent, priority);
	}
	
	public static String setPlayerPermission(String username, String permission, boolean allow, String zoneID)
	{
		return PermissionsAPI.setPlayerPermission(username, permission, allow, zoneID);
	}
	
	public static String setGroupPermission(String group, String permission, boolean allow, String zoneID)
	{
		return PermissionsAPI.setGroupPermission(group, permission, allow, zoneID);
	}
	
	public static ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults)
	{
		return PermissionsAPI.getApplicableGroups(player, includeDefaults);
	}
	
	public static Group getGroupForName(String name)
	{
		return PermissionsAPI.getGroupForName(name);
	}
	
	public static String setPlayerGroup(String group, String player, String zone)
	{
		return PermissionsAPI.setPlayerGroup(group, player, zone);
	}

	public static String addPlayerToGroup(String group, String player, String zone)
	{
		return PermissionsAPI.addPlayerToGroup(group, player, zone);
	}

	public static String clearPlayerGroup(String group, String player, String zone)
	{
		return PermissionsAPI.clearPlayerGroup(group, player, zone);
	}

	public static String clearPlayerPermission(String player, String node, String zone)
	{
		return PermissionsAPI.clearPlayerPermission(player, node, zone);
	}

	public static void deleteGroupInZone(String group, String zone)
	{
		PermissionsAPI.deleteGroupInZone(group, zone);
	}

	public static boolean updateGroup(Group group)
	{
		return PermissionsAPI.updateGroup(group);
	}

	public static String clearGroupPermission(String name, String node, String zone)
	{
		return PermissionsAPI.clearGroupPermission(name, node, zone);
	}

	public static ArrayList getGroupsInZone(String zoneName)
	{
		return PermissionsAPI.getGroupsInZone(zoneName);
	}

	public static String getPermissionForGroup(String target, String zone, String perm)
	{
		return PermissionsAPI.getPermissionForGroup(target, zone, perm);
	}
}
