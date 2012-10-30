package com.ForgeEssentials;

import java.util.HashMap;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;

public class PlayerInfo
{
	private static HashMap<String, PlayerInfo> playerInfoMap;
	private static HashMap<String, Point> selP1;
	private static HashMap<String, Point> selP2;
	
	public static Point getPlayerPoint1(String username)
	{
		Point point = playerInfoMap.get(username).sel1;

		if (point == null)
			point = new Point(0, 0, 0);

		return point;
	}

	// GET's

	public static Point getPlayerPoint2(String username)
	{
		Point point = playerInfoMap.get(username).sel2;

		if (point == null)
			point = new Point(0, 0, 0);

		return point;
	}

	// SET's

	public static void setPlayerPoint1(String username, Point newPoint)
	{
		Point point = playerInfoMap.get(username).sel1;

		if (point == null)
			point = newPoint;
		else
			point.update(newPoint);
		Selection.refreshSelection(username);
	}

	public static void setPlayerPoint1(EntityPlayer player, Point point)
	{
		setPlayerPoint1(player.username, point);
	}

	public static void setPlayerPoint2(String username, Point newPoint)
	{
		Point point = playerInfoMap.get(username).sel2;

		if (point == null)
			point = newPoint;
		else
			point.update(newPoint);
		Selection.refreshSelection(username);
	}

	public static void setPlayerPoint2(EntityPlayer player, Point point)
	{
		setPlayerPoint2(player.username, point);
	}
	
	// --------------------------------
	// ------- the actual class now -------
	// --------------------------------
	
	private Point sel1;
	private Point sel2;
	
	public PlayerInfo(EntityPlayerMP player)
	{
		
	}
}
