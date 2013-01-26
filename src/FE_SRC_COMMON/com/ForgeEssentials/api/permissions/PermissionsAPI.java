package com.ForgeEssentials.api.permissions;

import com.ForgeEssentials.api.permissions.query.PermQuery;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.APIHelper;
import com.ForgeEssentials.permission.SqlHelper;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

// This is a bouncer class for all Permissions API duties.

public abstract class PermissionsAPI
{
	public static boolean checkPermAllowed(PermQuery query)
	{
		return APIHelper.checkPermAllowed(query);
	}
	
	public static PermResult checkPermResult(PermQuery query)
	{
		return APIHelper.checkPermResult(query);
	}
	
	public static Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority)
	{
		return APIHelper.createGroupInZone(groupName, zoneName, prefix, suffix, parent, priority);
	}
	
	public static String setPlayerPermission(String username, String permission, boolean allow, String zoneID)
	{
		return APIHelper.setPlayerPermission(username, permission, allow, zoneID);
	}
	
	public static String setGroupPermission(String group, String permission, boolean allow, String zoneID)
	{
		return APIHelper.setGroupPermission(group, permission, allow, zoneID);
	}
	
	public static ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults)
	{
		return APIHelper.getApplicableGroups(player, includeDefaults);
	}
	
	public static Group getGroupForName(String name)
	{
		return APIHelper.getGroupForName(name);
	}
	
	public static String setPlayerGroup(String group, String player, String zone)
	{
		return APIHelper.setPlayerGroup(group, player, zone);
	}

	public static String addPlayerToGroup(String group, String player, String zone)
	{
		return APIHelper.addPlayerToGroup(group, player, zone);
	}

	public static String clearPlayerGroup(String group, String player, String zone)
	{
		return APIHelper.clearPlayerGroup(group, player, zone);
	}

	public static String clearPlayerPermission(String player, String node, String zone)
	{
		return APIHelper.clearPlayerPermission(player, node, zone);
	}

	public static void deleteGroupInZone(String group, String zone)
	{
		APIHelper.deleteGroupInZone(group, zone);
	}

	public static boolean updateGroup(Group group)
	{
		return APIHelper.updateGroup(group);
	}

	public static String clearGroupPermission(String name, String node, String zone)
	{
		return APIHelper.clearGroupPermission(name, node, zone);
	}

	public static ArrayList getGroupsInZone(String zoneName)
	{
		return APIHelper.getGroupsInZone(zoneName);
	}

	public static String getPermissionForGroup(String target, String zone, String perm)
	{
		return APIHelper.getPermissionForGroup(target, zone, perm);
	}
}
