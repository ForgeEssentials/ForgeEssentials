package com.ForgeEssentials.core;

import java.util.HashMap;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

public class PlayerInfo
{
	private static HashMap<String, PlayerInfo> playerInfoMap = new HashMap<String, PlayerInfo>();

	public static PlayerInfo getPlayerInfo(String username)
	{

		return playerInfoMap.get(username);
	}

	private boolean hasClientMod;
	private String username;

	// wand stuff
	public int wandID;
	public boolean wandEnabled;

	// selection stuff
	private Point sel1;
	private Point sel2;
	private Selection selection;

	// home
	public Point home;

	public PlayerInfo(String username)
	{
		sel1 = new Point(0, 0, 0);
		sel2 = new Point(0, 0, 0);
		selection = new Selection(sel1, sel2);
		this.username = username;
		playerInfoMap.put(username, this);
	}

	public boolean isHasClientMod()
	{
		return hasClientMod;
	}

	public String getUsername()
	{
		return username;
	}

	public Point getPoint1()
	{
		return sel1;
	}

	public void setPoint1(Point sel1)
	{
		this.sel1 = sel1;
		selection.start = sel1;
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;
		selection.end = sel2;
	}

	public Selection getSelection()
	{
		return selection;
	}
}
