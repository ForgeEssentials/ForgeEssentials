package com.ForgeEssentials.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;

import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.permissions.Zone;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class PlayerInfo implements Serializable
{
	public transient static File FESAVES = new File(ForgeEssentials.FEDIR, "saves/");

	private transient static HashMap<String, PlayerInfo> playerInfoMap	= new HashMap<String, PlayerInfo>();

	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		PlayerInfo info = playerInfoMap.get(player.username);

		if (info == null)
		{
			readOrGenerateInfo(player);
			return playerInfoMap.get(player.username);
		}

		return info;
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
				e.printStackTrace();
			}
		}

		// reading file failed.. continue with other stuff.
		PlayerInfo info = new PlayerInfo(player);
		playerInfoMap.put(username, info);

		try
		{
			if (!saveFile.exists())
			{
				saveFile.getParentFile().mkdirs();
				saveFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.remove(username));
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Failed in reading file: " + worldName + "/" + username);
			e.printStackTrace();
		}

		// send packets.
		ForgeEssentials.proxy.updateInfo(info, player);
	}

	public static void saveInfo(EntityPlayer player)
	{
		PlayerInfo info = getPlayerInfo(player);
		try
		{
			File saveFile = new File(FESAVES, info.worldName + "/" + player.username + ".ser").getAbsoluteFile();
			if (!saveFile.exists())
			{
				saveFile.getParentFile().mkdirs();
				saveFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.get(player.username));
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error while saving info file: " + info.worldName + "/" + player.username);
			e.printStackTrace();
		}
	}

	public static void saveAndDiscardInfo(EntityPlayer player)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		try
		{
			File saveFile = new File(FESAVES, info.worldName + "/" + player.username + ".ser").getAbsoluteFile();
			if (!saveFile.exists())
			{
				saveFile.getParentFile().mkdirs();
				saveFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerInfoMap.remove(player.username));
			oos.close();
			fos.close();
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error while saving info for player " + player.username);
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------
	// ---------------------------------- Actual Class Starts Now --------------------------------
	// -------------------------------------------------------------------------------------------

	private boolean	hasClientMod;
	private String worldName;
	private String username;

	// wand stuff
	public int wandID;
	public int wandDmg;
	public boolean wandEnabled;

	// selection stuff
	private Point sel1;
	private Point sel2;
	private Selection selection;

	// permissions stuff
	private HashMap<String, String>	areaGroupMap;

	// home
	public Point home;

	// last death location
	public Point lastDeath;

	// undo and redo stuff
	private Stack<BackupArea> undos;
	private Stack<BackupArea> redos;

	private PlayerInfo(EntityPlayer player)
	{
		sel1 = null;
		sel2 = null;
		selection = null;
		worldName = player.worldObj.getWorldInfo().getWorldName() + "_" + player.worldObj.getWorldInfo().getDimension();
		username = player.username;

		areaGroupMap = new HashMap<String, String>();
		areaGroupMap.put(worldName, "default");

		undos = new Stack<BackupArea>();
		redos = new Stack<BackupArea>();
	}

	public boolean isHasClientMod()
	{
		return hasClientMod;
	}

	public void setHasClientMod(boolean hasClient)
	{
		hasClientMod = hasClient;
	}

	public String getUsername()
	{
		return username;
	}

	public String getWorldName()
	{
		return worldName;
	}

	// ----------------------------------------------
	// ------------ Selection stuff -----------------
	// ----------------------------------------------

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

		// send packets.
		EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(username);
		ForgeEssentials.proxy.updateInfo(this, player);
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

		// send packets.
		EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(username);
		ForgeEssentials.proxy.updateInfo(this, player);
	}

	public Selection getSelection()
	{
		return selection;
	}

	// ----------------------------------------------
	// ------------ Undo/Redo stuff -----------------
	// ----------------------------------------------

	public void addUndoAction(BackupArea backup)
	{
		undos.push(backup);
		redos.clear();
	}

	public BackupArea getNextUndo()
	{
		BackupArea back = undos.pop();
		redos.push(back);
		return back;
	}

	public BackupArea getNextRedo()
	{
		BackupArea back = redos.pop();
		undos.push(back);
		return back;
	}

	// ----------------------------------------------
	// --------- Group/Permission stuff -------------
	// ----------------------------------------------

	public String getGroupForZone(Zone zone)
	{
		String group = areaGroupMap.get(zone.getZoneID());
		if (group == null)
			return "DEFAULT";
		else
			return group;
	}
}
