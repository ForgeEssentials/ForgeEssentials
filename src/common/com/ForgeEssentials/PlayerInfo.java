package com.ForgeEssentials;

import java.util.HashMap;

public class PlayerInfo extends ConsoleInfo
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
	
	public PlayerInfo(String username)
	{
		super();
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
	
}
