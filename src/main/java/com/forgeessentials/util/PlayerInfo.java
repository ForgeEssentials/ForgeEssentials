package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.core.network.PacketSelectionUpdate.Message;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.selections.ISelectionProvider;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.Selection;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@SaveableObject
public class PlayerInfo {
	
	private static HashMap<UUID, PlayerInfo> playerInfoMap = new HashMap<UUID, PlayerInfo>();

	// -------------------------------------------------------------------------------------------

	public class FESelectionProvider implements ISelectionProvider {
		private UUID user;

		protected FESelectionProvider(UUID username)
		{
			user = username;
		}

		@Override
		public Point getPoint1(EntityPlayerMP player)
		{
			return sel1;
		}

		@Override
		public Point getPoint2(EntityPlayerMP player)
		{
			return sel2;
		}

		@Override
		public Selection getSelection(EntityPlayerMP player)
		{
			return selection;
		}
	}
	
	// -------------------------------------------------------------------------------------------
	
	@UniqueLoadingKey()
	private final String name;

	@SaveableField()
	private final UUID playerID;

	// wand stuff
	private String wandID;

	private int wandDmg;

	private boolean wandEnabled = false;

	@SaveableField()
	private WarpPoint home;

	@SaveableField()
	private WarpPoint lastTeleportOrigin;

	@SaveableField()
	private String prefix;

	@SaveableField()
	private String suffix;

	// 0: Normal 1: World spawn 2: Bed 3: Home
	@SaveableField()
	private int spawnType;

	public int teleportCooldown = 0;

	private HashMap<String, Integer> kitCooldown = new HashMap<String, Integer>();

	// selection stuff
	@SaveableField()
	private Point sel1;

	@SaveableField()
	private Point sel2;

	private Selection selection;

	@SaveableField()
	private int timePlayed;

	private long loginTime;

	@SaveableField()
	private long firstJoin;

	// undo and redo stuff
	private Stack<BackupArea> undos;

	private Stack<BackupArea> redos;

	@SaveableField
	private List<ItemStack> hiddenItems;

	private ISelectionProvider selprovider;

	private PlayerInfo(UUID playerID)
	{
		sel1 = null;
		sel2 = null;
		selection = null;
		this.playerID = playerID;
		name = playerID.toString();

		undos = new Stack<BackupArea>();
		redos = new Stack<BackupArea>();

		prefix = "";
		suffix = "";

		firstJoin = System.currentTimeMillis();
		loginTime = System.currentTimeMillis();

		timePlayed = 0;

		hiddenItems = new ArrayList<ItemStack>();

		// if (!EnvironmentChecker.worldEditFEtoolsInstalled)
		selprovider = new FESelectionProvider(playerID);
	}

	@Reconstructor()
	public static PlayerInfo reconstruct(IReconstructData tag)
	{
		UUID username = UUID.fromString((String) tag.getFieldValue("username"));

		PlayerInfo info = new PlayerInfo(username);

		info.setPoint1((Point) tag.getFieldValue("sel1"));
		info.setPoint2((Point) tag.getFieldValue("sel2"));

		info.home = (WarpPoint) tag.getFieldValue("home");
		info.lastTeleportOrigin = (WarpPoint) tag.getFieldValue("back");

		info.spawnType = (Integer) tag.getFieldValue("spawnType");

		info.prefix = (String) tag.getFieldValue("prefix");
		info.suffix = (String) tag.getFieldValue("suffix");

		info.timePlayed = (Integer) tag.getFieldValue("timePlayed");

		info.firstJoin = (Long) tag.getFieldValue("firstJoin");
		return info;
	}

	/**
	 * Notifies the PlayerInfo to save itself to the Data store.
	 */
	public void save()
	{
		recalcTimePlayed();
		DataStorageManager.getReccomendedDriver().saveObject(new ClassContainer(PlayerInfo.class), this);
	}

	@SubscribeEvent
	public void initForPlayer(PlayerEvent.LoadFromFile event)
	{
		getPlayerInfo(event.entityPlayer);
	}

	// @Deprecated Why? it doesn't have to be removed?
	public static PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		return getPlayerInfo(player.getPersistentID());
	}

	public static PlayerInfo getPlayerInfo(UUID playerID)
	{
		PlayerInfo info = playerInfoMap.get(playerID);
		// load or create one
		if (info == null)
		{
			// Attempt to populate this info with some data from our storage.
			info = (PlayerInfo) DataStorageManager.getReccomendedDriver().loadObject(new ClassContainer(PlayerInfo.class), playerID.toString());
			if (info == null)
			{
				info = new PlayerInfo(playerID);
			}
			playerInfoMap.put(playerID, info);
		}
		return info;
	}

	public static void discardInfo(UUID username)
	{
		PlayerInfo info = playerInfoMap.remove(username);
		if (info != null)
		{
			info.save();
		}
	}

	// ----------------------------------------------

	public String getWandID()
	{
		return wandID;
	}

	public void setWandID(String wandID)
	{
		this.wandID = wandID;
	}

	public boolean isWandEnabled()
	{
		return wandEnabled;
	}

	public void setWandEnabled(boolean wandEnabled)
	{
		this.wandEnabled = wandEnabled;
	}

	public int getWandDmg()
	{
		return wandDmg;
	}

	public void setWandDmg(int wandDmg)
	{
		this.wandDmg = wandDmg;
	}

	// ----------------------------------------------

	public long getFirstJoin()
	{
		return firstJoin;
	}

	public int getTimePlayed()
	{
		recalcTimePlayed();
		return timePlayed;
	}

	public void recalcTimePlayed()
	{
		long current = System.currentTimeMillis() - loginTime;
		int min = (int) (current / 60000);
		timePlayed += min;
		loginTime = System.currentTimeMillis();
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

	public void setPoint1(Point sel1)
	{
		if (EnvironmentChecker.worldEditFEtoolsInstalled)
			return;

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

		FunctionHelper.netHandler.sendTo(new Message(this), FunctionHelper.getPlayerForUUID(playerID));
	}

	public void setPoint2(Point sel2)
	{
		if (EnvironmentChecker.worldEditFEtoolsInstalled)
			return;

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

		FunctionHelper.netHandler.sendTo(new Message(this), FunctionHelper.getPlayerForUUID(playerID));
	}

	public Point getPoint1()
	{
		return selprovider.getPoint1(FunctionHelper.getPlayerForUUID(playerID));
	}

	public String getName()
	{
		return name;
	}

	public Selection getSelection()
	{
		return selprovider.getSelection(FunctionHelper.getPlayerForUUID(playerID));
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
		{
			return null;
		}

		BackupArea back = undos.pop();
		redos.push(back);
		return back;
	}

	public BackupArea getNextRedo()
	{
		if (redos.empty())
		{
			return null;
		}

		BackupArea back = redos.pop();
		undos.push(back);
		return back;
	}

	public void clearSelection()
	{
		if (EnvironmentChecker.worldEditFEtoolsInstalled)
			return;
		selection = null;
		sel1 = null;
		sel2 = null;
		FunctionHelper.netHandler.sendTo(new Message(this), FunctionHelper.getPlayerForUUID(playerID));
	}

	public List<ItemStack> getHiddenItems()
	{
		return hiddenItems;

	}

	public HashMap<String, Integer> getKitCooldown()
	{
		return kitCooldown;
	}

	// ----------------------------------------------

	public WarpPoint getLastTeleportOrigin()
	{
		return lastTeleportOrigin;
	}

	public void setLastTeleportOrigin(WarpPoint lastTeleportStart)
	{
		this.lastTeleportOrigin = lastTeleportStart;
	}

	public int getTeleportCooldown()
	{
		return teleportCooldown;
	}

	public void setTeleportCooldown(int teleportCooldown)
	{
		this.teleportCooldown = teleportCooldown;
	}


	public WarpPoint getHome()
	{
		return home;
	}

	public void setHome(WarpPoint home)
	{
		this.home = home;
	}

	// ----------------------------------------------

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public Point getPoint2()
	{
		return selprovider.getPoint2(FunctionHelper.getPlayerForUUID(playerID));
	}

}
