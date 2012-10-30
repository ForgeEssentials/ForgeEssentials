package com.ForgeEssentials.AreaSelector;

import java.util.HashMap;

import com.ForgeEssentials.PlayerInfo;


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
			select = new Selection(PlayerInfo.getPlayerPoint1(username), PlayerInfo.getPlayerPoint2(username));
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
		else if (select.start != selection.start || select.end != selection.end)
			selections.put(username, selection);
	}
	
	public static void refreshSelection(EntityPlayer player)
	{
		refreshSelection(player.username);
	}
	
	public static void refreshSelection(String username)
	{
		setSelection(username, new Selection(PlayerInfo.getPlayerPoint1(username), PlayerInfo.getPlayerPoint1(username)));
	}
	
	public static void printInvalidSelection(EntityPlayer player)
	{
		player.addChatMessage("\u00a7e"+"Invalid Selection");
	}
	
	// --------------------------------
	//  -------  the actual class now -------
	// --------------------------------
	
	private Point start;
	private Point end;
	
	public Selection(Point point1, Point point2)
	{
		start = point1;
		end = point2;
		start.validate();
		end.validate();
	}

	public Point getEnd1()
	{
		return start;
	}

	public void setEnd1(Point end1)
	{
		this.start = end1;
		end1.validate();
	}

	public Point getEnd2()
	{
		return end;
	}

	public void setEnd2(Point end2)
	{
		this.end = end2;
		start.validate();
	}
	
	public void shift(int x, int y, int z)
	{
		Point point = new Point(x, y, z);
		start.add(point);
		end.add(point);
		
		start.validate();
		end.validate();
	}
	
	public int[] getDimensions()
	{
		int[] array = new int[3];
		array[0] = Math.abs(start.x - end.x);
		array[1] = Math.abs(start.y - end.z);
		array[2] = Math.abs(start.z - end.z);
		return array;
	}
}
