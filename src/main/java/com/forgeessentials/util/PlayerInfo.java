package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.Expose;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerInfo implements Loadable {

    private static HashMap<UUID, PlayerInfo> playerInfoMap = new HashMap<UUID, PlayerInfo>();

    public static boolean persistSelections;

    private final UserIdent ident;

    private WarpPoint home;

    private WarpPoint lastTeleportOrigin;

    private WarpPoint lastDeathLocation;

    protected HashMap<String, Date> namedTimeout = new HashMap<String, Date>();

    protected Point sel1;

    protected Point sel2;

    protected int selDim;

    private int timePlayed = 0;

    private Map<String, List<ItemStack>> inventoryGroups = new HashMap<>();

    private String activeInventoryGroup = "default";

    private long firstJoin = System.currentTimeMillis();

    @Expose(serialize = false)
    private long loginTime = System.currentTimeMillis();

    @Expose(serialize = false)
    private String wandID;

    @Expose(serialize = false)
    private int wandDmg;

    @Expose(serialize = false)
    private boolean wandEnabled = false;

    @Expose(serialize = false)
    private long lastTeleportTime = 0;

    @Expose(serialize = false)
    private boolean hasFEClient = false;

    // undo and redo stuff
    @Expose(serialize = false)
    private Stack<BackupArea> undos = new Stack<BackupArea>();

    @Expose(serialize = false)
    private Stack<BackupArea> redos = new Stack<BackupArea>();

    @Override
    public void afterLoad()
    {
        if (namedTimeout == null)
            namedTimeout = new HashMap<String, Date>();
    }

    protected PlayerInfo(UUID uuid)
    {
        this.ident = new UserIdent(uuid);
    }

    /**
     * Notifies the PlayerInfo to save itself to the Data store.
     */
    public void save()
    {
        recalcTimePlayed();
        DataManager.getInstance().save(this, ident.getUuid().toString());
    }

    @SubscribeEvent
    public void initForPlayer(PlayerEvent.LoadFromFile event)
    {
        getPlayerInfo(event.entityPlayer);
    }

    public static boolean playerInfoExists(UUID playerID)
    {
        if (playerInfoMap.containsKey(playerID))
            return true;
        PlayerInfo info = load(playerID.toString());
        if (info != null)
            return true;
        return false;
    }

    private static PlayerInfo load(String key)
    {
        PlayerInfo info = DataManager.getInstance().load(PlayerInfo.class, key);
        return info;
    }

    public static PlayerInfo getPlayerInfo(EntityPlayer player)
    {
        return getPlayerInfo(player.getPersistentID());
    }

    public static PlayerInfo getPlayerInfo(UserIdent ident)
    {
        if (!ident.hasUUID())
            return null;
        return getPlayerInfo(ident.getUuid());
    }

    public static PlayerInfo getPlayerInfo(UUID playerID)
    {
        PlayerInfo info = playerInfoMap.get(playerID);
        if (info == null)
        {
            // Attempt to populate this info with some data from our storage.
            info = load(playerID.toString());
            if (info == null)
            {
                EntityPlayerMP player = UserIdent.getPlayerByUuid(playerID);
                if (player != null)
                    APIRegistry.getFEEventBus().post(new NoPlayerInfoEvent(player));
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

    /**
     * Saves all player-infos
     */
    public static void saveAll()
    {
        for (PlayerInfo info : playerInfoMap.values())
        {
            info.save();
        }
    }

    /**
     * Clear player-infos
     */
    public static void clear()
    {
        playerInfoMap.clear();
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
    // --------------- Timeouts ---------------------
    // ----------------------------------------------

    /**
     * Check, if a timeout passed
     */
    public boolean checkTimeout(String name)
    {
        Date timeout = namedTimeout.get(name);
        if (timeout == null)
            return true;
        if (timeout.after(new Date()))
            return false;
        namedTimeout.remove(name);
        return true;
    }

    /**
     * Get the remaining timeout in milliseconds
     */
    public long getRemainingTimeout(String name)
    {
        Date timeout = namedTimeout.get(name);
        if (timeout == null)
            return 0;
        return timeout.getTime() - new Date().getTime();
    }

    /**
     * Start a named timeout.
     * Use {@link #checkTimeout(String)} to check if the timeout has passed.
     * 
     * @param name Unique name of the timeout
     * @param milliseconds Timeout in milliseconds
     */
    public void startTimeout(String name, int milliseconds)
    {
        Date date = new Date();
        date.setTime(date.getTime() + milliseconds);
        namedTimeout.put(name, date);
    }

    // ----------------------------------------------
    // ------------ Selection stuff -----------------
    // ----------------------------------------------

    public Point getSel1()
    {
        return sel1;
    }

    public Point getSel2()
    {
        return sel2;
    }

    public int getSelDim()
    {
        return selDim;
    }

    public void setSel1(Point point)
    {
        sel1 = point;
    }

    public void setSel2(Point point)
    {
        sel2 = point;
    }

    public void setSelDim(int dimension)
    {
        selDim = dimension;
    }

    // ----------------------------------------------
    // ---------- protection gamemode ---------------
    // ----------------------------------------------

    public Map<String, List<ItemStack>> getInventoryGroups()
    {
        return inventoryGroups;
    }

    public List<ItemStack> getInventoryGroupItems(String name)
    {
        return inventoryGroups.get(name);
    }

    public String getInventoryGroup()
    {
        return activeInventoryGroup;
    }

    public void setInventoryGroup(String name)
    {
        if (!activeInventoryGroup.equals(name))
        {
            // Get the new inventory
            List<ItemStack> newInventory = inventoryGroups.get(name);
            // Create empty inventory if it did not exist yet
            if (newInventory == null)
                newInventory = new ArrayList<>();
            // Swap player inventory and store the old one
            inventoryGroups.put(activeInventoryGroup, FunctionHelper.swapInventory(this.ident.getPlayer(), newInventory));
            // Clear the inventory-group that was assigned to the player (optional)
            inventoryGroups.put(name, null);
            // Save the new active inventory-group
            activeInventoryGroup = name;
        }
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

    // ----------------------------------------------

    public WarpPoint getLastTeleportOrigin()
    {
        return lastTeleportOrigin;
    }

    public void setLastTeleportOrigin(WarpPoint lastTeleportStart)
    {
        this.lastTeleportOrigin = lastTeleportStart;
    }

    public WarpPoint getLastDeathLocation()
    {
        return lastDeathLocation;
    }

    public void setLastDeathLocation(WarpPoint lastDeathLocation)
    {
        this.lastDeathLocation = lastDeathLocation;
    }

    public long getLastTeleportTime()
    {
        return lastTeleportTime;
    }

    public void setLastTeleportTime(long currentTimeMillis)
    {
        this.lastTeleportTime = currentTimeMillis;
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

    // network stuff
    public boolean getHasFEClient()
    {
        return hasFEClient;
    }

    public void setHasFEClient(boolean status)
    {
        this.hasFEClient = status;
    }

    public static Map<UUID, PlayerInfo> getPlayerInfoMap()
    {
        return ImmutableMap.copyOf(playerInfoMap);
    }


}
