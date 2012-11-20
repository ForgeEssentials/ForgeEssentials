package com.ForgeEssentials.api.permissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

public class Zone extends AreaBase implements Comparable, Serializable
{
	public int											priority;			// lowest priority is 0
	private String										zoneID;			// unique string name
	private Zone										parent;			// the unique name of the parent.
	private ArrayList<String>							children;			// list of all children of this zone
	private String										worldString;		// the WorldString of world this zone exists in.
	private int											childStatus;		// tells us how many parents this zone is under
	public final boolean								isWorldZone;		// flag for WorldZones
	public final boolean								isGlobalZone;		// flag for GLOBAL zones

	// permission maps
	protected HashMap<String, ArrayList<Permission>>	playerOverrides;	// <username, perm list>
	protected HashMap<String, ArrayList<Permission>>	groupPerms;		// <groupName, perm list>

	public Zone(String ID, Selection sel, Zone parent)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
		this.parent = parent;
		worldString = parent.worldString;
		parent.children.add(zoneID);
		childStatus = parent.childStatus+1;

		playerOverrides = new HashMap<String, ArrayList<Permission>>();
		groupPerms = new HashMap<String, ArrayList<Permission>>();

		isWorldZone = isGlobalZone = false;
	}

	public Zone(String ID, Selection sel, World world)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
		parent = ZoneManager.getWorldZone(world);
		parent.children.add(ID);
		childStatus = 3;

		playerOverrides = new HashMap<String, ArrayList<Permission>>();
		groupPerms = new HashMap<String, ArrayList<Permission>>();

		isWorldZone = isGlobalZone = false;
	}

	/**
	 * used to construct Global and World zones.
	 * @param ID
	 */
	public Zone(String ID)
	{
		super(new Point(0, 0, 0), new Point(0, 0, 0));
		zoneID = ID;

		if (!ID.equals("__GLOBAL__"))
		{
			parent = ZoneManager.GLOBAL;
			isGlobalZone = false;
			isWorldZone = true;
			childStatus = 1;
		}
		else
		{
			isGlobalZone = true;
			isWorldZone = false;
			childStatus = 0;
		}

		playerOverrides = new HashMap<String, ArrayList<Permission>>();
		groupPerms = new HashMap<String, ArrayList<Permission>>();
	}

	public boolean isParentOf(Zone zone)
	{
		if (zoneID.equals(zone.parent.zoneID))
			return true;
		else if (zone.isGlobalZone)
			return false;
		else if (zone.isWorldZone && !isGlobalZone)
			return false;
		else
			return isParentOf(zone.parent);
	}

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
		else if (this.isParentOf(zone))
			return 100;
		else
		{
			int priority =  this.priority - zone.priority;
			
			if (priority == 0)
				return this.childStatus - zone.childStatus;
			else
				return priority;
		}
	}

	/**
	 * Gets the result of a permission for a player in this area
	 * @param player the player to check.
	 * @param check The permissionChecker to check against
	 * @return DEFAULT if the permission is not specified in this area for this player. ALLOW/DENY if the Permission was found and read.
	 */
	public Result getPlayerOverride(EntityPlayer player, PermissionChecker check)
	{
		if (this.groupPerms.containsKey(player.username))
		{
			ArrayList<Permission> perms = groupPerms.get(player.username);
			Permission smallest = null;
			for (Permission perm : perms)
			{
				if (check.equals(perm))
					return perm.allowed;
				else if (check.matches(perm))
				{
					if (smallest == null)
						smallest = perm;
					else if (smallest.isChildOf(perm))
						smallest = perm;
				}
			}
			if (smallest != null)
				return smallest.allowed;
		}
		return Result.DEFAULT;
	}

	/**
	 * Gets the result of a permission for a group in this area
	 * @param groupname the group to check.
	 * @param check The permissionChecker to check against
	 * @return DEFAULT if the permission is not specified in this area for this group. ALLOW/DENY if the Permission was found and read.
	 */
	public Result getGroupOverride(String groupname, PermissionChecker check)
	{
		if (this.groupPerms.containsKey(groupname))
		{
			ArrayList<Permission> perms = groupPerms.get(groupname);
			Permission smallest = null;
			for (Permission perm : perms)
			{
				if (check.equals(perm))
					return perm.allowed;
				else if (check.matches(perm))
				{
					if (smallest == null)
						smallest = perm;
					else if (smallest.isChildOf(perm))
						smallest = perm;
				}
			}
			if (smallest != null)
				return smallest.allowed;
		}
		return Result.DEFAULT;
	}
}
