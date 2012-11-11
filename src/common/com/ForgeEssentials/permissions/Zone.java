package com.ForgeEssentials.permissions;

import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

public class Zone extends AreaBase
{
	// to change to Permissions Object
	//HashMap<String, ArrayList<String>> playerOverrides;
	
	// ----------------------
	// -- Actual Class ------
	// ----------------------
	
	private int worldID;

	public Zone(Point start, Point end, String ID, World world)
	{
		super(start, end);
		worldID = world.getWorldInfo().getDimension();
	}
	
	/**
	 * 
	 * @param start  new start point
	 * @param end  new end point;
	 */
	public void redefine(Point start, Point end)
	{
		this.setStart(start);
		this.setEnd(end);
	}

}
