package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.permission.events.PermissionSetEvent;
import com.ForgeEssentials.permission.query.PermQuery;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.permission.query.PermissionQueryBus;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;

public class PermissionsAPI
{

	public static final PermissionQueryBus QUERY_BUS = new PermissionQueryBus();
	public static final String EntryPlayer = "_ENTRY_PLAYER_";

	/**
	 * Used for blankets permissions tied to no particular layer or group in a zone. This is the the group all players are assigned to if they are members of no
	 * other groups. This group is guaranteed existence
	 */
	public static Group DEFAULT = new Group(RegGroup.ZONE.toString(), " ", " ", null, ZoneManager.GLOBAL.getZoneName(), 0);

	/**
	 * Use this to check AllOrNothing Area queries, Player Queries, or Point Queries.
	 * 
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
	 * 
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
	 * 
	 * @param groupName
	 * @param ZoneID
	 * @return NULL if the construction or registration fails.
	 */
	public static Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority)
	{
		Group g = new Group(groupName, prefix, suffix, parent, zoneName, priority);
		SqlHelper.createGroup(g);
		return g;
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
		try
		{
			Zone zone = ZoneManager.getZone(zoneID);
			if (zone == null)
			{
				return Localization.format(Localization.ERROR_ZONE_NOZONE, zoneID);
			}

			Permission perm = new Permission(permission, allow);

			// send out permission string.
			PermissionSetEvent event = new PermissionSetEvent(perm, zone, "p:" + username);
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				return event.getCancelReason();
			}

			boolean worked = SqlHelper.setPermission(username, false, perm, zoneID);

			if (!worked)
			{
				return Localization.get(Localization.ERROR_PERM_SQL);
			}
		}
		catch (Throwable t)
		{
			return t.getLocalizedMessage();
		}

		return null;
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
		try
		{
			Zone zone = ZoneManager.getZone(zoneID);

			if (zone == null)
			{
				return Localization.format(Localization.ERROR_ZONE_NOZONE, zoneID);
			}

			Group g = SqlHelper.getGroupForName(group);
			if (g == null)
			{
				return Localization.format("message.error.nogroup", group);
			}

			Permission perm = new Permission(permission, allow);

			// send out permission string.
			PermissionSetEvent event = new PermissionSetEvent(perm, zone, "g:" + group);
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				return event.getCancelReason();
			}

			boolean worked = SqlHelper.setPermission(group, true, perm, zoneID);

			if (!worked)
			{
				return Localization.get(Localization.ERROR_PERM_SQL);
			}
		}
		catch (Throwable t)
		{
			return t.getMessage();
		}

		return null;
	}

	// ill recreate it when I need it...
	//
	// /**
	// * Gets all the groups that were explicitly created in the given zone.
	// these groups will only apply
	// * to the given Zone and all of its children.
	// * @param zoneID zone to check.
	// * @return List of Groups. may be an empty list, but never null.
	// */
	// protected static ArrayList<Group> getAllGroupsCreatedForZone(String
	// zoneID)
	// {
	// ArrayList<Group> gs = new ArrayList<Group>();
	// for (Group g : groups.values())
	// if (g.zoneID.equals(zoneID))
	// gs.add(g);
	//
	// return gs;
	// }

	/**
	 * Returns the list of all the groups the player is in at a given time. It is in order of priority the first bieng the highest. NEVER includes the default
	 * group.
	 * 
	 * @param player
	 */
	public static ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults)
	{
		ArrayList<Group> list = new ArrayList<Group>();
		Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(player));

		ArrayList<Group> temp;
		// while (zone != null)
		// {
		temp = SqlHelper.getGroupsForPlayer(player.username, zone.getZoneName());
		list.addAll(temp);
		// }

		if (includeDefaults)
		{
			list.add(DEFAULT);
		}

		return list;
	}

	public static Group getHighestGroup(EntityPlayer player)
	{
		Group high;
		Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(player));
		TreeSet<Group> list = new TreeSet<Group>();

		ArrayList<Group> temp;
		while (zone != null && list.size() <= 0)
		{
			temp = SqlHelper.getGroupsForPlayer(player.username, zone.getZoneName());

			if (!temp.isEmpty())
			{
				list.addAll(temp);
			}

			zone = ZoneManager.getZone(zone.parent);
		}

		if (list.size() == 0)
		{
			return DEFAULT;
		}
		else
		{
			return list.pollFirst();
		}
	}

	public static Group getGroupForName(String name)
	{
		return SqlHelper.getGroupForName(name);
	}
	
	public static String setPlayerToGroup(String group, String player, String zone)
	{
		return SqlHelper.setPlayerGroup(group, player, zone);
	}

	public static String addPlayerToGroup(String group, String player, String zone)
	{
		return SqlHelper.addPlayerGroup(group, player, zone);
	}

	public static String clearPlayerGroup(String group, String player, String zone)
	{
		return SqlHelper.removePlayerGroup(group, player, zone);
	}

	public static String clearPlayerPermission(String player, String node, String zone)
	{
		return SqlHelper.removePermission(player, false, node, zone);
	}

	public static void deleteGroupInZone(String group, String zone)
	{
		SqlHelper.deleteGroupInZone(group, zone);
	}

	public static void updateGroup(Group group)
	{
		SqlHelper.updateGroup(group);
	}

	public static void clearGroupPermission(String name, String node, String zone)
	{
		SqlHelper.removePermission(name, true, node, zone);
	}
}
