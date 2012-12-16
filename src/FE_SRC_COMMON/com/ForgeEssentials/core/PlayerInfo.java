package com.ForgeEssentials.core;

import java.util.HashMap;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class PlayerInfo
{
	private static HashMap<String, PlayerInfo> playerInfoMap = new HashMap<String, PlayerInfo>();

	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		PlayerInfo info = playerInfoMap.get(player.username);

		if (info == null)
		{
			info = new PlayerInfo(player);
			
			// Attempt to populate this info with some data from our storage.
			if (!ForgeEssentials.getInstanceDataDriver().loadObject(player.username, info))
			{
				// Loading was unsuccessful. Save this object while we can.
				info.save();
			}
			
			playerInfoMap.put(player.username, info);
		}

		return info;
	}
	
	public static PlayerInfo getPlayerInfo(String username)
	{
		PlayerInfo info = playerInfoMap.get(username);
		
		return info;
	}
	
	public static void discardInfo(String username)
	{
		playerInfoMap.remove(username);
	}

	// -------------------------------------------------------------------------------------------
	// ---------------------------------- Actual Class Starts Now
	// --------------------------------
	// -------------------------------------------------------------------------------------------

	private boolean hasClientMod;
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

	public WorldPoint home;
	public WorldPoint lastDeath;
	// 0: Normal 1: World spawn 2: Bed 3: Home
	public int spawnType;

	// undo and redo stuff
	private Stack<BackupArea> undos;
	private Stack<BackupArea> redos;

	public int TPcooldown = 0;
	public HashMap<String, Integer> kitCooldown = new HashMap<String, Integer>();
	
	private PlayerInfo(EntityPlayer player)
	{
		sel1 = null;
		sel2 = null;
		selection = null;
		worldName = player.worldObj.getWorldInfo().getWorldName() + "_" + player.worldObj.getWorldInfo().getDimension();
		username = player.username;

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
	
	/**
	 * Notifies the PlayerInfo to save itself to the Data store. 
	 */
	public void save()
	{
		ForgeEssentials.getInstanceDataDriver().saveObject(this);
	}
	
	// ----------------------------------------------
	// ---------------- TP stuff --------------------
	// ----------------------------------------------
	
	public void TPcooldownTick() 
	{
		if(TPcooldown != 0)
		{
			TPcooldown--;
		}
	}
	
	// ----------------------------------------------
	// ------------- Command stuff ------------------
	// ----------------------------------------------
	
	public void KitCooldownTick()
	{
		for(String key : kitCooldown.keySet())
		{
			if(kitCooldown.get(key) == 0)
			{
				kitCooldown.remove(key);
			}
			else
			{
				kitCooldown.put(key, kitCooldown.get(key) - 1);
			}
		}
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
		} else
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
		} else
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
}
