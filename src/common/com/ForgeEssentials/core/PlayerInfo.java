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
	public transient static File							FESAVES			= new File(ForgeEssentials.FEDIR, "saves/");

	private transient static HashMap<String, PlayerInfo>	playerInfoMap	= new HashMap<String, PlayerInfo>();

	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		return playerInfoMap.get(player.username);
	}

	public static void readOrGenerateInfo(EntityPlayer player)
	{
		String worldName = player.worldObj.getWorldInfo().getWorldName() + "_" + player.worldObj.getWorldInfo().getDimension();
		String username = player.username;

		File saveFile = new File(FESAVES, worldName + "/" + username + ".ser").getAbsoluteFile();

		// read file.
		if (saveFile.exists() && saveFile.isFile() && saveFile.canRead())
		{
			try
			{
				FileInputStream fis = new FileInputStream(saveFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				PlayerInfo info = (PlayerInfo) ois.readObject();
				ois.close();
				fis.close();
				playerInfoMap.put(username, info);
				return;
			}
			catch (Exception e)
			{
				OutputHandler.SOP("Failed in reading file: " + worldName + "/" + username);
			}
		}

		// reading file failed.. continue with other stuff.
		PlayerInfo info = new PlayerInfo(player);
		playerInfoMap.put(username, info);

		try
		{
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.remove(username));
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Failed in reading file: " + worldName + "/" + username);
		}
	}

	public static void saveInfo(EntityPlayer player)
	{
		PlayerInfo info = getPlayerInfo(player);
		try
		{
			File saveFile = new File(FESAVES, info.worldName + "/" + player.username + ".ser").getAbsoluteFile();
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.get(player.username));
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error while saving info file: " + info.worldName + "/" + player.username);
		}
	}

	public static void saveAndDiscardInfo(EntityPlayer player)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		try
		{
			File saveFile = new File(FESAVES, info.worldName + "/" + player.username + ".ser").getAbsoluteFile();
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.remove(player.username));
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error while saving info for player " + player.username);
		}
	}

	private boolean		hasClientMod;
	private String		worldName;
	private String		username;

	// wand stuff
	public int			wandID;
	public int			wandDmg;
	public boolean		wandEnabled;

	// selection stuff
	private Point		sel1;
	private Point		sel2;
	private Selection	selection;

	// home
	public Point		home;

	private PlayerInfo(EntityPlayer player)
	{
		sel1 = null;
		sel2 = null;
		selection = null;
		worldName = player.worldObj.getWorldInfo().getWorldName() + "_" + player.worldObj.getWorldInfo().getDimension();
		username = player.username;
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

		if (selection == null)
		{
			if (sel1 != null && sel2 != null)
				selection = new Selection(sel1, sel2);
		}
		else
			selection.setStart(sel1);
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;

		if (selection == null)
		{
			if (sel1 != null && sel2 != null)
				selection = new Selection(sel1, sel2);
		}
		else
			selection.setEnd(sel2);
	}

	public Selection getSelection()
	{
		return selection;
	}
}
