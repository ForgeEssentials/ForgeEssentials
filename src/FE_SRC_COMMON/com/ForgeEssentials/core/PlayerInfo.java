package com.ForgeEssentials.core;

import java.util.HashMap;
import java.util.Stack;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.core.network.PacketSelectionUpdate;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

@SaveableObject
public class PlayerInfo extends TimerTask
{
	private static HashMap<String, PlayerInfo>	playerInfoMap	= new HashMap<String, PlayerInfo>();

	// @Deprecated Why? it doesn't have to be removed?
	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		return getPlayerInfo(player.username);
	}

	public static PlayerInfo getPlayerInfo(String username)
	{
		PlayerInfo info = playerInfoMap.get(username);

		// load or create one
		if (info == null)
		{
			// Attempt to populate this info with some data from our storage.
			info = (PlayerInfo) DataStorageManager.getReccomendedDriver().loadObject(new ClassContainer(PlayerInfo.class), username);

			if (info == null)
			{
				info = new PlayerInfo(username);
			}

			TaskRegistry.registerRecurringTask(info, 0, 1, 0, 0, 0, 1, 0, 0);
			playerInfoMap.put(username, info);
		}

		return info;
	}

	public static void discardInfo(String username)
	{
		PlayerInfo info = playerInfoMap.remove(username);
		info.save();
	}

	@Reconstructor()
	public static PlayerInfo reconstruct(IReconstructData tag)
	{
		String username = (String) tag.getFieldValue("username");

		PlayerInfo info = new PlayerInfo(username);

		info.setPoint1((Point) tag.getFieldValue("sel1"));
		info.setPoint2((Point) tag.getFieldValue("sel2"));

		info.home = (WarpPoint) tag.getFieldValue("home");
		info.back = (WarpPoint) tag.getFieldValue("back");

		info.spawnType = (Integer) tag.getFieldValue("spawnType");

		info.prefix = (String) tag.getFieldValue("prefix");
		info.suffix = (String) tag.getFieldValue("suffix");

		info.timePlayed = (Integer) tag.getFieldValue("timePlayed");

		info.firstJoin = (Long) tag.getFieldValue("firstJoin");

		return info;
	}

	// -------------------------------------------------------------------------------------------
	// ---------------------------------- Actual Class Starts Now --------------------------------
	// -------------------------------------------------------------------------------------------
	@UniqueLoadingKey()
	@SaveableField()
	public final String				username;

	// wand stuff
	public int						wandID		= 0;
	public int						wandDmg		= 0;
	public boolean					wandEnabled	= false;

	// selection stuff
	@SaveableField()
	private Point					sel1;

	@SaveableField()
	private Point					sel2;

	private Selection				selection;

	@SaveableField()
	public WarpPoint				home;

	@SaveableField()
	public WarpPoint				back;

	@SaveableField()
	public String					prefix;

	@SaveableField()
	public String					suffix;

	// 0: Normal 1: World spawn 2: Bed 3: Home
	@SaveableField()
	public int						spawnType;

	@SaveableField()
	public int						timePlayed;

	@SaveableField()
	private long					firstJoin;

	// undo and redo stuff
	private Stack<BackupArea>		undos;
	private Stack<BackupArea>		redos;

	public int						TPcooldown	= 0;
	public HashMap<String, Integer>	kitCooldown	= new HashMap<String, Integer>();

	private PlayerInfo(String username)
	{
		sel1 = null;
		sel2 = null;
		selection = null;
		this.username = username;

		undos = new Stack<BackupArea>();
		redos = new Stack<BackupArea>();

		prefix = "";
		suffix = "";

		firstJoin = System.currentTimeMillis();
		
		timePlayed = 0;
	}

	/**
	 * Notifies the PlayerInfo to save itself to the Data store.
	 */
	public void save()
	{
		DataStorageManager.getReccomendedDriver().saveObject(new ClassContainer(PlayerInfo.class), this);
		TaskRegistry.removeTask(this);
	}

	public long getFirstJoin()
	{
		return firstJoin;
	}

	// ----------------------------------------------
	// ---------------- TP stuff --------------------
	// ----------------------------------------------

	public void TPcooldownTick()
	{
		if (TPcooldown != 0)
		{
			TPcooldown--;
		}
	}

	// ----------------------------------------------
	// ------------- Command stuff ------------------
	// ----------------------------------------------

	public void KitCooldownTick()
	{
		for (String key : kitCooldown.keySet())
		{
			if (kitCooldown.get(key) == 0)
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
				{
					selection = new Selection(sel1, sel2);
				}
			}
			else
			{
				selection.setStart(sel1);
			}
		}

		// send packets.
		EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(username);
		PacketDispatcher.sendPacketToPlayer(new PacketSelectionUpdate(this).getPayload(), (Player) player);
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
				{
					selection = new Selection(sel1, sel2);
				}
			}
			else
			{
				selection.setEnd(sel2);
			}
		}

		// send packets.
		EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(username);
		PacketDispatcher.sendPacketToPlayer(new PacketSelectionUpdate(this).getPayload(), (Player) player);
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
		if (undos.empty())
			return null;

		BackupArea back = undos.pop();
		redos.push(back);
		return back;
	}

	public BackupArea getNextRedo()
	{
		if (redos.empty())
			return null;

		BackupArea back = redos.pop();
		undos.push(back);
		return back;
	}

	public void clearSelection()
	{
		selection = null;
		sel1 = null;
		sel2 = null;
		EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(username);
		PacketDispatcher.sendPacketToPlayer(new PacketSelectionUpdate(this).getPayload(), (Player) player);
	}

	@Override
	public void run()
	{
		try
		{
			timePlayed ++;
			OutputHandler.debug(this.username + ":" + this.timePlayed);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
