package com.ForgeEssentials;

import java.util.ArrayList;
import java.util.HashMap;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.CopyArea;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;

public class PlayerInfo extends ConsoleInfo
{
	private static HashMap<String, PlayerInfo> playerInfoMap = new HashMap<String, PlayerInfo>();
	
	public static PlayerInfo getPlayerInfo(String username)
	{
		return playerInfoMap.get(username);
	}
	
	private boolean hasClientMod;
	private String username;
	
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
