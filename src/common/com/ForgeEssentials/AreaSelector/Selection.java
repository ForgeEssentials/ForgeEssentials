package com.ForgeEssentials.AreaSelector;

import java.util.HashMap;


import net.minecraft.src.EntityPlayer;

public class Selection
{
	private static HashMap<String, Selection> selections;
	
	public static Selection getPlayerSelection(EntityPlayer player)
	{
		return getPlayerSelection(player.username);
	}
	
	public static Selection getPlayerSelection(String username)
	{
		Selection select = selections.get(username);
		if (select == null)
			select = new Selection(Point.getPlayerPoint1(username), Point.getPlayerPoint2(username));
		return select;
	}
	
	public static void setSelection(EntityPlayer player, Selection selection)
	{
		setSelection(player.username, selection);
	}
	
	public static void setSelection(String username, Selection selection)
	{
		Selection select = selection.getPlayerSelection(username);
		if (select == null)
			selections.put(username, selection);
		else if (select.end1 != selection.end1 || select.end2 != selection.end2)
			selections.put(username, selection);
	}
	
	// --------------------------------
	//  -------  the actual class now -------
	// --------------------------------
	
	private Point end1;
	private Point end2;
	boolean isValid;
	
	public Selection(Point point1, Point point2)
	{
		end1 = point1;
		end2 = point2;
		isValid = end1.isValid() && end2.isValid();
	}
}
