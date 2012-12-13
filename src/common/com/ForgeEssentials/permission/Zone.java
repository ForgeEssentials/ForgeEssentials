package com.ForgeEssentials.permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class Zone extends AreaBase implements Comparable
{
	public int										priority;			// lowest priority is 0
	private String									zoneID;			// unique string name
	public String									parent;			// the unique name of the parent.
	private String									worldString;		// the WorldString of world this zone exists in.
	public final boolean							isWorldZone;		// flag for WorldZones
	public final boolean							isGlobalZone;		// flag for GLOBAL zones

	// permission maps
	protected HashMap<String, HashSet<Permission>>	playerOverrides;	// <username, perm list>
	protected HashMap<String, HashSet<Permission>>	groupOverrides;	// <groupName, perm list>
	protected HashMap<String, PromotionLadder>		ladders;			// the ladders present in this zone

	public Zone(String ID, Selection sel, Zone parent)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
		this.parent = parent.zoneID;
		worldString = parent.worldString;
		isWorldZone = isGlobalZone = false;
		initMaps();
	}

	public Zone(String ID, Selection sel, World world)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
		parent = FunctionHelper.getZoneWorldString(world);
		worldString = FunctionHelper.getWorldString(world);
		isWorldZone = isGlobalZone = false;
		initMaps();
	}

	/**
	 * used to construct Global and World zones.
	 * @param ID
	 */
	public Zone(String ID, World world)
	{
		super(new Point(0, 0, 0), new Point(0, 0, 0));
		zoneID = ID;

		if (!ID.equals("__GLOBAL__"))
		{
			parent = ZoneManager.GLOBAL.zoneID;
			isGlobalZone = false;
			isWorldZone = true;
		}
		else
		{
			isGlobalZone = true;
			isWorldZone = false;
			worldString = FunctionHelper.getWorldString(world);
		}

		initMaps();
	}

	private void initMaps()
	{
		ladders = new HashMap<String, PromotionLadder>();
		playerOverrides = new HashMap<String, HashSet<Permission>>();
		groupOverrides = new HashMap<String, HashSet<Permission>>();
		groupOverrides.put("_DEFAULT_", new HashSet<Permission>());
	}

	public boolean isParentOf(Zone zone)
	{
		if (isGlobalZone)
			return true;
		else if (zoneID.equals(zone.parent))
			return true;
		else if (zone.isGlobalZone)
			return false;
		else if (zone.isWorldZone && !isGlobalZone)
			return false;
		else
			return isParentOf(ZoneManager.getZone(zone.parent));
	}

	/**
	 * @return if this Permission is a child of the given Permission.
	 */
	public boolean isChildOf(Zone zone)
	{
		if (zone.isGlobalZone)
			return true;
		else if (zone.isWorldZone)
			return zone.worldString.equals(worldString);
		else if (zone.zoneID.equals(parent))
			return true;
		else
			return ZoneManager.getZone(parent).isChildOf(zone);
	}

	/**
	 * @return The Unique ID of this Zone.
	 */
	public String getZoneID()
	{
		return zoneID;
	}

	@Override
	public int compareTo(Object o)
	{
		Zone zone = (Zone) o;
		if (zone.isParentOf(this))
			return -100;
		else if (isParentOf(zone))
			return 100;
		else
			return priority - zone.priority;
	}

	/**
	 * @param name
	 * @return NULL if the ladder doesn't exist here
	 */
	public PromotionLadder getLadder(String name)
	{
		return ladders.get(name);
	}

	/**
	 * Gets the result of a permission for a player in this area
	 * @param player the player to check.
	 * @param check The permissionChecker to check against
	 * @return DEFAULT if the permission is not specified in this area for this player. ALLOW/DENY if the Permission was found and read.
	 */
	public PermResult getPlayerOverride(EntityPlayer player, PermissionChecker check)
	{
		if (groupOverrides.containsKey(player.username))
		{
			HashSet<Permission> perms = groupOverrides.get(player.username);
			Permission smallest = null;
			for (Permission perm : perms)
				if (check.equals(perm))
					return perm.allowed;
				else if (check.matches(perm))
					if (smallest == null)
						smallest = perm;
					else if (smallest.isChildOf(perm))
						smallest = perm;
			if (smallest != null)
				return smallest.allowed;
		}
		return PermResult.UNKNOWN;
	}

	/**
	 * Gets the result of a permission for a group in this area
	 * @param groupname the group to check.
	 * @param check The permissionChecker to check against
	 * @return DEFAULT if the permission is not specified in this area for this group. ALLOW/DENY if the Permission was found and read.
	 */
	public PermResult getGroupOverride(String groupname, PermissionChecker check)
	{
		Group group = GroupManager.groups.get(groupname);
		if (groupOverrides.containsKey(groupname))
		{
			HashSet<Permission> perms = groupOverrides.get(groupname);
			Permission smallest = null;
			for (Permission perm : perms)
				if (check.equals(perm))
					return perm.allowed;
				else if (check.matches(perm))
					if (smallest == null)
						smallest = perm;
					else if (smallest.isChildOf(perm))
						smallest = perm;
			if (smallest != null)
				return smallest.allowed;
			else if (group != null && group.hasParent() && groupOverrides.containsKey(group.getParent()))
			{
				perms = groupOverrides.get(group.getParent());
				for (Permission perm : perms)
					if (check.equals(perm))
						return perm.allowed;
					else if (check.matches(perm))
						if (smallest == null)
							smallest = perm;
						else if (smallest.isChildOf(perm))
							smallest = perm;

				if (smallest != null)
					return smallest.allowed;
			}
		}
		// this zone doesn't contain this groupname. check for blankets.
		else
		{
			HashSet<Permission> perms = groupOverrides.get(PermissionsAPI.GROUP_DEFAULT);
			Permission smallest = null;
			for (Permission perm : perms)
				if (check.equals(perm))
					return perm.allowed;
				else if (check.matches(perm))
					if (smallest == null)
						smallest = perm;
					else if (smallest.isChildOf(perm))
						smallest = perm;
			if (smallest != null)
				return smallest.allowed;
		}
		return PermResult.UNKNOWN;
	}

	public String getWorldString()
	{
		return worldString;
	}

	public Set<String> getPlayersOverriden()
	{
		return playerOverrides.keySet();
	}

	public Set<String> getGroupsOverriden()
	{
		return groupOverrides.keySet();
	}

	public static void load(String id, String parentID, String worldString, int priority, Selection area)
	{
		Zone zone = ZoneManager.getZone(id);
		Zone parent = ZoneManager.getZone(parentID);
		World world = FunctionHelper.getWorldFromWorldString(worldString);

		if (parent == null)
			parent = ZoneManager.getWorldZone(world);

		if (zone == null)
			zone = new Zone(id, area, parent);

		zone.priority = priority;

		ZoneManager.zoneMap.put(id, zone);
	}
}
