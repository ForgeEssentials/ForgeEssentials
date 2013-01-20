package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.permission.APIHelper;
import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.query.PermQuery;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;

// A flat-fork proof of concept Perms API, needs work.
// Treat this like you would ModLoader.java or MinecraftForge.java.

public class PermissionsAPI
{

	/**
	 * Use this to check AllOrNothing Area queries, Player Queries, or Point Queries.
	 * 
	 * @param query
	 * @return TRUE if the permission is allowed. FALSE if the permission is denied or partially allowed.
	 */
	public static boolean checkPermAllowed(PermQuery query)
	{
		return APIHelper.checkPermAllowed(query);
	}

	/**
	 * Use this with Area Queries, so you can know if the Permission is partially allowed.
	 * 
	 * @param query
	 * @return the Result of the query
	 */
	public static PermResult checkPermResult(PermQuery query)
	{
		return APIHelper.checkPermResult(query);
	}

	/**
	 * Constructs, registers, and returns a group.
	 * 
	 * @param groupName
	 * @param ZoneID
	 * @return NULL if the construction or registration fails.
	 */
	public static Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority)
	{
		return APIHelper.createGroupInZone(groupName, zoneName, prefix, suffix, parent, priority);
	}

	/**
	 * Sets a permission for a player in a zone.
	 * 
	 * @param username
	 *            player to apply the permission to.
	 * @param permission
	 *            Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow
	 * @return Reason for set cancellation NULL if the set succeeds. EMpty String if it fails but has no reason.
	 */
	public static String setPlayerPermission(String username, String permission, boolean allow, String zoneID)
	{
		return APIHelper.setPlayerPermission(username, permission, allow, zoneID);
	}

	/**
	 * Sets a permission for a group in a zone.
	 * 
	 * @param username
	 *            player to apply the permission to.
	 * @param permission
	 *            Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow
	 * @return Reason for set cancellation NULL if the set succeeds. EMpty String if it fails but has no reason.
	 */
	public static String setGroupPermission(String group, String permission, boolean allow, String zoneID)
	{
		return APIHelper.setGroupPermission(group, permission, allow, zoneID);

	}
	/**
	 * Returns the list of all the groups the player is in at a given time. It is in order of priority the first bieng the highest.
	 * @param player
	 * @param includeDefaults  if the DEFAULT groups of each zone should be added to the list.
	 */
	public static ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults)
	{
		return APIHelper.getApplicableGroups(player, includeDefaults);
	}

	// needs javadoc 
	
	public static Group getHighestGroup(EntityPlayer player)
	{
		return APIHelper.getHighestGroup(player);
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
		return APIHelper.getPermissionForGroup(target, perm, zone);
	}
}
