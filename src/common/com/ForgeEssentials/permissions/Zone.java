package com.ForgeEssentials.permissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

public class Zone extends AreaBase implements Comparable, Serializable
{
	public static Zone						GLOBAL;

	private static HashMap<String, Zone>	zoneMap;

	public static void deleteZone(String zoneID)
	{
		zoneMap.remove(zoneID);
	}

	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		if (zoneMap.containsKey(zoneID))
			return false;
		zoneMap.put(zoneID, new Zone(zoneID, sel, world));
		return true;
	}

	public static Zone getWhichZoneIn(Point p1, World world)
	{
		String worldString = world.getWorldInfo().getWorldName() + "_" + world.getWorldInfo().getDimension();
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all zones this point is in...
		for (Zone zone : zoneMap.values())
			if (zone.contains(p1) && worldString.equals(zone.worldString))
				zones.add(zone);
		
		
		// only 1 zone? thats obvious
		if (zones.size() == 1)
			return zones.get(0);
		
		// now the sorting fun.
		else if (zones.size() > 1)
			return narrow(zones, GLOBAL);
		
		return GLOBAL;
	}
	
	private static Zone narrow(ArrayList<Zone> zones, Zone parent)
	{
		ArrayList<Zone> allChildren = new ArrayList<Zone>();
		ArrayList<Zone> directChildren = new ArrayList<Zone>();
		
		// we are left with everything whos parent was NOT the one specified....
		for (Zone zone : zones)
			if (parent.isParentOf(zone))
				allChildren.add(zone);
			else if (zone.parentID.equals(parent.parentID))
				directChildren.add(zone);
		
		// nothing is a child of this parent?? check priorities.
		if (allChildren.size() == 0)
		{
			// returns highest priority Zone.
			Zone priority  = null;
			
			for (Zone zone : zones)
				if (priority == null || priority.compareTo(zone) < 0)
					priority = zone;
			
			// returns highest priority
			return priority;
		}
		else if (allChildren.size() == 1)
			return allChildren.get(0);
		else if (directChildren.size() == 1)
			return narrow (allChildren, directChildren.get(0));
		else
		{
			// get higest priority of direct children.
			Zone priority  = null;
			
			for (Zone zone : zones)
				if (priority == null || priority.compareTo(zone) < 0)
					priority = zone;
			
			return narrow(allChildren, parent);
		}
	}

	// -------------------------------------------------------------------------------------------
	// ---------------------------------- Actual Class Starts Now -------------------------------
	// -------------------------------------------------------------------------------------------

	public int								priority;			// lowest priority is 0
	private String							zoneID;
	private String							parentID;
	private ArrayList<String>				children;
	private String							worldString;

	// PlayerOverrides
	HashMap<String, ArrayList<Permission>>	playerOverrides;	// <username, perm list>

	// group permissions
	HashMap<String, ArrayList<Permission>>	groupPerms;		// <groupName, perm list>

	private Zone(String ID, Selection sel, String parent)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
		parentID = parent;
		worldString = zoneMap.get(parent).worldString;
		zoneMap.get(parent).children.add(ID);
	}

	private Zone(String ID, Selection sel, World world)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
		parentID = "__GLOBAL__";
		GLOBAL.children.add(ID);
		worldString = world.getWorldInfo().getWorldName() + "_" + world.getWorldInfo().getDimension();
	}

	protected Zone(String ID)
	{
		super(new Point(0, 0, 0), new Point(0, 0, 0));
		zoneID = ID;
		parentID = "__GLOBAL__";
	}

	public Zone getParentZone()
	{
		return zoneMap.get(parentID);
	}
	
	public boolean isParentOf(Zone zone)
	{
		if (zoneID.equals(zone.parentID))
			return true;
		else if (zone.parentID.equals("__GLOBAL__"))
			return false;
		else
			return isParentOf(zoneMap.get(zone.parentID));
	}
	
	public String getParent()
	{
		return parentID;
	}

	public String getParentID()
	{
		return parentID;
	}

	public void setParentID(String parentID)
	{
		zoneMap.get(this.parentID).children.remove(zoneID);
		this.parentID = parentID;
		zoneMap.get(this.parentID).children.add(zoneID);
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
			return -1;
		else if (this.isParentOf(zone))
			return 1;
		else
			return this.priority-zone.priority;
	}

}
