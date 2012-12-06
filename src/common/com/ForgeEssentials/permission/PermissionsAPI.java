package com.ForgeEssentials.permission;

import java.util.HashSet;

import com.ForgeEssentials.permission.query.PermQuery;
import com.ForgeEssentials.permission.query.PermissionQueryBus;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventBus;
import net.minecraftforge.event.Event.Result;


public class PermissionsAPI
{
	/**
	 * This is automatically assigned to the server owner when they make a world available to the LAN.
	 * This is also best kep for layers that have direct access to the server console.
	 * **CAUTION! MAY OR MAYNOT EXIST**
	 */
	public static final String GROUP_OWNERS = "Owners";
	
	/**
	 * This is usually for players that are actually members of the server.
	 * They will most likely be able to use basic commands as well as break blocks and stuff in the world.
	 * **CAUTION MAY OR MAY NOT EXIST**
	 */
	public static final String GROUP_MEMBERS = "Members";
	
	/**
	 * This is usually for players that are admins or owners of a given zone
	 * They will most likely have WorldEdit access, as well as the power to edit permissions in the zone.
	 * **CAUTION MAY OR MAY NOT EXIST**
	 */
	public static final String GROUP_ZONE_ADMINS = "ZoneAdmins";
	
	/**
	 * Used for blankets permissions tied to no particular layer or group in a zone.
	 * This is the also the group all players are assigned to if they are members of no other groups.
	 * This includes new players when they first log in.
	 * The players in this group are usually denied commands and breaking blocks before they are promoted to members.
	 * This group is guaranteed existence
	 */
	public static final String GROUP_DEFAULT = "_DEFAULT_";
	
    public static final PermissionQueryBus QUERY_BUS = new PermissionQueryBus();
	
	/**
	 * Use this to check AllOrNothing Area queries, Player Queries, or Point Queries.
	 * @param query
	 * @return TRUE if the permission is allowed. FALSE if the permission is denied or partially allowed.
	 */
	public static boolean checkPermAllowed(PermQuery query)
	{
		QUERY_BUS.post(query);
		return query.isAllowed();
	}

	/**
	 * Use this with Area Queries, so you can know if the Permission is partially allowed.
	 * @param query
	 * @return the Result of the query
	 */
	public static PermResult checkPermResult(PermQuery query)
	{
		QUERY_BUS.post(query);
		return query.getResult();
	}
	
	/**
	 * Constructs, registers, and returns a group.
	 * @param groupName
	 * @param ZoneID
	 * @return NULL if the construction or registration fails.
	 */
	public static Group createGroupInZone(String groupName, String ZoneID)
	{
		if (GroupManager.groups.containsKey(groupName))
			return null;
		
		Group newG = new Group(groupName, ZoneID);
		GroupManager.groups.put(groupName, newG);
		return newG;
	}
	
	public static void setPlayerPermission(String username, String permission, boolean allow, String zoneID)
	{
		Zone zone = ZoneManager.getZone(zoneID);
		if (zone == null)
			return;
		
		Permission perm = new Permission(permission, allow);
		HashSet<Permission> perms = zone.playerOverrides.get(username);
		
		if (perms == null)
		{
			perms = new HashSet<Permission>();
			perms.add(perm);
			zone.playerOverrides.put(username, perms);
		}
		else
		{
			PermissionChecker checker = new PermissionChecker(permission);
			if (perms.contains(checker))
				perms.remove(checker);
			perms.add(perm);
		}
	}
	
	/**
	 * Sets a permission for a group in a zone.
	 * Does nothing if the Group or the Zone do not exist.
	 * @param username player to apply the permission to.
	 * @param permission Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow
	 */
	public static void setGroupPermission(String group, String permission, boolean allow, String zoneID)
	{
		Zone zone = ZoneManager.getZone(zoneID);
		if (!GroupManager.groups.containsKey(group) || zone == null)
			return;
		
		Permission perm = new Permission(permission, allow);
		HashSet<Permission> perms = zone.groupOverrides.get(group);
		
		if (perms == null)
		{
			perms = new HashSet<Permission>();
			perms.add(perm);
			zone.groupOverrides.put(group, perms);
		}
		else
		{
			PermissionChecker checker = new PermissionChecker(permission);
			if (perms.contains(checker))
				perms.remove(checker);
			perms.add(perm);
		}
	}
	
}
