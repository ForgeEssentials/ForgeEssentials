package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.google.gson.annotations.Expose;

public class PlayerInfo implements Loadable
{

    private static HashMap<UUID, PlayerInfo> playerInfoMap = new HashMap<UUID, PlayerInfo>();

    /* ------------------------------------------------------------ */
    /* General */

    public final UserIdent ident;

    @Expose(serialize = false)
    private boolean hasFEClient = false;

    /* ------------------------------------------------------------ */
    /* Teleport */

    private WarpPoint home;

    private WarpPoint lastTeleportOrigin;

    private WarpPoint lastDeathLocation;

    @Expose(serialize = false)
    private long lastTeleportTime = 0;

    /* ------------------------------------------------------------ */
    /* Selection */

    private Point sel1;

    private Point sel2;

    private int selDim;

    /* ------------------------------------------------------------ */
    /* Selection wand */

    @Expose(serialize = false)
    private boolean wandEnabled = false;

    @Expose(serialize = false)
    private String wandID;

    @Expose(serialize = false)
    private int wandDmg;

    /* ------------------------------------------------------------ */
    /* Inventory groups */

    private Map<String, List<ItemStack>> inventoryGroups = new HashMap<>();

    private String activeInventoryGroup = "default";

    /* ------------------------------------------------------------ */
    /* Stats / time */

    private long timePlayed = 0;

    @Expose(serialize = false)
    private long timePlayedRef = 0;

    private Date firstLogin = new Date();

    private Date lastLogin = new Date();

    private Date lastLogout;

    @Expose(serialize = false)
    private long lastActivity = System.currentTimeMillis();

    private HashMap<String, Date> namedTimeout = new HashMap<String, Date>();

    /* ------------------------------------------------------------ */

    private PlayerInfo(UUID uuid)
    {
        this.ident = UserIdent.get(uuid);
    }

    @Override
    public void afterLoad()
    {
        if (namedTimeout == null)
            namedTimeout = new HashMap<String, Date>();
        lastActivity = System.currentTimeMillis();
    }

    /**
     * Notifies the PlayerInfo to save itself to the Data store.
     */
    public void save()
    {
        DataManager.getInstance().save(this, ident.getUuid().toString());
    }

    public boolean isLoggedIn()
    {
        return ident.hasPlayer();
    }

    /* ------------------------------------------------------------ */

    public static PlayerInfo get(UUID uuid)
    {
        PlayerInfo info = playerInfoMap.get(uuid);
        if (info == null)
        {
            // Attempt to populate this info with some data from our storage.
            info = DataManager.getInstance().load(PlayerInfo.class, uuid.toString());
            if (info == null)
            {
                EntityPlayerMP player = UserIdent.getPlayerByUuid(uuid);
                if (player != null)
                    APIRegistry.getFEEventBus().post(new NoPlayerInfoEvent(player));
                info = new PlayerInfo(uuid);
            }
            playerInfoMap.put(uuid, info);
        }
        return info;
    }

    public static PlayerInfo get(EntityPlayer player)
    {
        return get(player.getPersistentID());
    }

    public static PlayerInfo get(UserIdent ident)
    {
        if (!ident.hasUuid())
            return null;
        return get(ident.getUuid());
    }

    public static Collection<PlayerInfo> getAll()
    {
        return playerInfoMap.values();
    }

    public static void login(UUID uuid)
    {
        PlayerInfo pi = get(uuid);
        pi.lastActivity = System.currentTimeMillis();
        pi.timePlayedRef = System.currentTimeMillis();
        pi.lastLogin = new Date();
    }

    public static void logout(UUID uuid)
    {
        if (!playerInfoMap.containsKey(uuid))
            return;
        PlayerInfo pi = playerInfoMap.remove(uuid);
        pi.getTimePlayed();
        pi.lastLogout = new Date();
        pi.timePlayedRef = 0;
        pi.save();
    }

    public static boolean exists(UUID uuid)
    {
        if (playerInfoMap.containsKey(uuid))
            return true;
        if (DataManager.getInstance().exists(PlayerInfo.class, uuid.toString()))
            return true;
        return false;
    }

    /**
     * Unload PlayerInfo and save to disk
     */
    public static void discard(UUID uuid)
    {
        PlayerInfo info = playerInfoMap.remove(uuid);
        if (info != null)
            info.save();
    }

    /**
     * Discard all PlayerInfo
     */
    public static void discardAll()
    {
        for (PlayerInfo info : playerInfoMap.values())
            info.save();
        playerInfoMap.clear();
    }

    /* ------------------------------------------------------------ */

    public Date getFirstLogin()
    {
        return firstLogin;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public Date getLastLogout()
    {
        return lastLogout;
    }

    public long getTimePlayed()
    {
        if (isLoggedIn() && timePlayedRef != 0)
        {
            timePlayed += System.currentTimeMillis() - timePlayedRef;
            timePlayedRef = System.currentTimeMillis();
        }
        return timePlayed;
    }

    public void setActive()
    {
        lastActivity = System.currentTimeMillis();
    }

    public void setActive(long delta)
    {
        lastActivity = System.currentTimeMillis() - delta;
    }

    public long getInactiveTime()
    {
        return System.currentTimeMillis() - lastActivity;
    }

    /* ------------------------------------------------------------ */
    /* Timeouts */

    /**
     * Check, if a timeout passed
     * 
     * @param name
     * @return true, if the timeout passed
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
     * Start a named timeout. Use {@link #checkTimeout(String)} to check if the timeout has passed.
     * 
     * @param name
     *            Unique name of the timeout
     * @param milliseconds
     *            Timeout in milliseconds
     */
    public void startTimeout(String name, int milliseconds)
    {
        Date date = new Date();
        date.setTime(date.getTime() + milliseconds);
        namedTimeout.put(name, date);
    }

    /* ------------------------------------------------------------ */
    /* Wand */

    public boolean isWandEnabled()
    {
        return wandEnabled;
    }

    public void setWandEnabled(boolean wandEnabled)
    {
        this.wandEnabled = wandEnabled;
    }

    public String getWandID()
    {
        return wandID;
    }

    public void setWandID(String wandID)
    {
        this.wandID = wandID;
    }

    public int getWandDmg()
    {
        return wandDmg;
    }

    public void setWandDmg(int wandDmg)
    {
        this.wandDmg = wandDmg;
    }

    /* ------------------------------------------------------------ */
    /* Selection */

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

    /* ------------------------------------------------------------ */
    /* Inventory groups */

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

            // OutputHandler.felog.info(String.format("Changing inventory group for %s from %s to %s",
            // ident.getUsernameOrUUID(), activeInventoryGroup, name));
            /*
             * OutputHandler.felog.info("Items in old inventory:"); for (int i = 0; i <
             * ident.getPlayer().inventory.getSizeInventory(); i++) { ItemStack itemStack =
             * ident.getPlayer().inventory.getStackInSlot(i); if (itemStack != null) OutputHandler.felog.info("  " +
             * itemStack.getDisplayName()); } OutputHandler.felog.info("Items in new inventory:"); for (ItemStack
             * itemStack : newInventory) if (itemStack != null) OutputHandler.felog.info("  " +
             * itemStack.getDisplayName());
             */

            // Swap player inventory and store the old one
            inventoryGroups.put(activeInventoryGroup, PlayerUtil.swapInventory(this.ident.getPlayerMP(), newInventory));
            // Clear the inventory-group that was assigned to the player (optional)
            inventoryGroups.put(name, null);
            // Save the new active inventory-group
            activeInventoryGroup = name;
        }
    }

    /* ------------------------------------------------------------ */
    /* Teleportation */

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

    /* ------------------------------------------------------------ */
    /* Other */

    public boolean getHasFEClient()
    {
        return hasFEClient;
    }

    public void setHasFEClient(boolean status)
    {
        this.hasFEClient = status;
    }

}
