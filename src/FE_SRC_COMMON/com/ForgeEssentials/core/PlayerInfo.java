package com.ForgeEssentials.core;

import java.util.HashMap;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

@SaveableObject
public class PlayerInfo
{
	private static HashMap<String, PlayerInfo> playerInfoMap = new HashMap<String, PlayerInfo>();

	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		PlayerInfo info = playerInfoMap.get(player.username);

		// load or create one
		if (info == null)
		{
			// Attempt to populate this info with some data from our storage.  TODO: get the actual config-given choice...
			info = (PlayerInfo) DataStorageManager.getDriverOfName("ForgeConfig").loadObject(PlayerInfo.class, player.username);
			
			if (info == null)
				info = new PlayerInfo(player);
			
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
	
	@Reconstructor()
	private static PlayerInfo reconstruct(TaggedClass tag)
	{
		String username = (String) tag.getFieldValue("username");
		
		PlayerInfo info = new PlayerInfo(FunctionHelper.getPlayerFromUsername(username));
		
		info.setPoint1((Point) tag.getFieldValue("sel1"));
		info.setPoint2((Point) tag.getFieldValue("sel2"));
		
		info.home = (WorldPoint) tag.getFieldValue("home");
		info.lastDeath = (WorldPoint) tag.getFieldValue("lastDeath");
		
		info.spawnType = (Integer) tag.getFieldValue("spawnType");
		
		return null;
	}

	// -------------------------------------------------------------------------------------------
	// ---------------------------------- Actual Class Starts Now --------------------------------
	// -------------------------------------------------------------------------------------------
	@UniqueLoadingKey()
	@SaveableField()
	public final String username;

	// wand stuff
	public int wandID = 0;
	public int wandDmg = 0;
	public boolean wandEnabled = false;

	// selection stuff
	@SaveableField(nullableField = true)
	private Point sel1;
	
	@SaveableField(nullableField = true)
	private Point sel2;
	
	private Selection selection;

	@SaveableField(nullableField = true)
	public WorldPoint home;
	
	@SaveableField(nullableField = true)
	public WorldPoint lastDeath;
	
	// 0: Normal 1: World spawn 2: Bed 3: Home
	@SaveableField
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
		username = player.username;

		undos = new Stack<BackupArea>();
		redos = new Stack<BackupArea>();
	}
	
	/**
	 * Notifies the PlayerInfo to save itself to the Data store. 
	 */
	public void save()
	{
		// TODO: get the actual config-given choice...
		DataStorageManager.getDriverOfName("ForgeConfig").saveObject(this);
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

		if (sel1 != null)
		{
			if (selection == null)
			{
				if (sel1 != null && sel2 != null)
					selection = new Selection(sel1, sel2);
			} else
				selection.setStart(sel1);
		}

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

		if (sel2 != null)
		{
			if (selection == null)
			{
				if (sel1 != null && sel2 != null)
					selection = new Selection(sel1, sel2);
			} else
				selection.setEnd(sel2);
		}

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
