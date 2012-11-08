package com.ForgeEssentials.permissions;

import java.util.ArrayList;
import java.util.HashMap;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

public class Zone extends AreaBase
{
	HashMap<String, ArrayList<String>> playerOverrides;
	
	// ----------------------
	// -- Actual Class ------
	// ----------------------

	public Zone(Point start, Point end, String ID)
	{
		super(start, end);
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
