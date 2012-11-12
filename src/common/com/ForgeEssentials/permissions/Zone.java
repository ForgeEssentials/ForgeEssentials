package com.ForgeEssentials.permissions;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

public class Zone extends AreaBase
{
	private static HashMap<String, Zone>	zoneMap;

	public static void deleteZone(String zoneID)
	{
		zoneMap.remove(zoneID);
	}

	public static void createZone(String zoneID, Selection sel)
	{
		zoneMap.put(zoneID, new Zone(zoneID, sel));
	}

	// ----------------------
	// -- Actual Class ------
	// ----------------------

	public int								priority;			// lowest priority is 0
	private String							zoneID;
	public String							parentID;

	// PlayerOverrides
	HashMap<String, ArrayList<Permission>>	playerOverrides;	// <username, perm list>

	// group permissions
	HashMap<String, ArrayList<Permission>>	groupPerms;		// <groupName, perm list>

	private Zone(String ID, Selection sel)
	{
		super(sel.getLowPoint(), sel.getHighPoint());
		zoneID = ID;
	}

	/**
	 * @param start new start point
	 * @param end new end point;
	 */
	public void redefine(Point start, Point end)
	{
		this.setStart(start);
		this.setEnd(end);
	}

}
