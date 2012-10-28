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
	
	public static void refreshSelection(EntityPlayer player)
	{
		refreshSelection(player.username);
	}
	
	public static void refreshSelection(String username)
	{
		setSelection(username, new Selection(Point.getPlayerPoint1(username), Point.getPlayerPoint1(username)));
	}
	
	public static void invalidSelection(EntityPlayer player)
	{
		player.addChatMessage("\u00a7e"+"Invalid Selection");
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

	public Point getEnd1()
	{
		return end1;
	}

	public void setEnd1(Point end1)
	{
		this.end1 = end1;
	}

	public Point getEnd2()
	{
		return end2;
	}

	public void setEnd2(Point end2)
	{
		this.end2 = end2;
	}

	public boolean isValid()
	{
		return isValid;
	}

	public void setValid(boolean isValid)
	{
		this.isValid = isValid;
	}
	
	public int[] getDimensions()
	{
		int[] array = new int[3];
		array[0] = Math.abs(end1.getX() - end2.getX());
		array[1] = Math.abs(end1.getY() - end2.getY());
		array[2] = Math.abs(end1.getZ() - end2.getZ());
		return array;
	}
	
	/**
	 * checks if the selection is valid. if it isn't it sends out a chat message to the player.
	 * @return if the selection is valid
	 */
	public boolean validate(EntityPlayer player)
	{
		if (!isValid)
			invalidSelection(player);
		return isValid;
	}
}
