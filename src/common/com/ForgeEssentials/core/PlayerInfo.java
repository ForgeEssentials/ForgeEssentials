package com.ForgeEssentials.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

public class PlayerInfo implements Serializable
{
	public transient static File FESAVES = new File(ForgeEssentials.FEDIR, "saves/");

	private transient static HashMap<String, HashMap<String, PlayerInfo>> playerInfoMap = new HashMap<String, HashMap<String, PlayerInfo>>();

	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		return playerInfoMap.get(player.worldObj.getWorldInfo().getWorldName()).get(player.username);
	}

	public static void savePlayerInfo(EntityPlayer player)
	{
		try
		{
			String world = player.worldObj.getSaveHandler().getSaveDirectoryName();
			FileOutputStream fos = new FileOutputStream(FESAVES + world + "/" + player.username + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.get(world).get(player.username));
			oos.close();
			fos.close();
		} catch (Exception e)
		{
			OutputHandler.SOP("Error while saving info for player " + player.username);
		}
	}

	private boolean hasClientMod;
	private String worldName;
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

	public PlayerInfo(EntityPlayer player)
	{
		sel1 = new Point(0, 0, 0);
		sel2 = new Point(0, 0, 0);
		selection = new Selection(sel1, sel2);
		worldName = player.worldObj.getSaveHandler().getSaveDirectoryName();
		username = player.username;
		
		if (playerInfoMap.containsKey(worldName))
		{
			try
			{
				FileInputStream fis = new FileInputStream(FESAVES + worldName + "/" + username + ".ser");
				ObjectInputStream ois = new ObjectInputStream(fis);
				playerInfoMap.get(worldName).put(username, (PlayerInfo) ois.readObject());
				ois.close();
				fis.close();
			} catch (Exception e)
			{
				playerInfoMap.get(worldName).put(username, this);
			}
		} else
		{
			HashMap<String, PlayerInfo> map = new HashMap();
			map.put(username, this);
			playerInfoMap.put(worldName, map);
		}
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
		selection.setStart(sel1);
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;
		selection.setEnd(sel2);
	}

	public Selection getSelection()
	{
		return selection;
	}
}
