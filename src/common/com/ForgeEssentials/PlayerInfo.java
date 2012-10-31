package com.ForgeEssentials;

import java.util.ArrayList;
import java.util.HashMap;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.CopyArea;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;

public class PlayerInfo
{
	private static HashMap<String, PlayerInfo> playerInfoMap;
	
	public static PlayerInfo getPlayerInfo(String username)
	{
		return playerInfoMap.get(username);
	}
	
	private boolean hasClientMod;
	private String username;
	private Point sel1;
	private Point sel2;
	private Selection selection;
	private CopyArea copy;
	private ArrayList<BackupArea> backups; // max = 5 backups.
	
	public PlayerInfo(String username)
	{
		this.username = username;
	}

	public Point getPoint1()
	{
		return sel1;
	}

	public void setPoint1(Point sel1)
	{
		this.sel1 = sel1;
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;
	}
	
	public Selection getSelection()
	{
		return selection;
	}

	public CopyArea getCopy()
	{
		return copy;
	}

	public void setCopy(CopyArea copy)
	{
		this.copy = copy;
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
