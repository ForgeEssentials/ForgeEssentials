package com.forgeessentials.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.events.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.events.FEPlayerEvent.InventoryGroupChange;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

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

    // New inventory groups, with support for modded custom inventories
    private Map<String, Map<String, List<ItemStack>>> modInventoryGroups = new HashMap<>();

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

    @Expose(serialize = false)
    private boolean noClip = false;
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
        if (activeInventoryGroup == null || activeInventoryGroup.isEmpty())
            activeInventoryGroup = "default";

        if (modInventoryGroups == null)
            modInventoryGroups = new HashMap<>();

        if (!inventoryGroups.isEmpty())
        {
            // See if we have an inventory to port
            Set<String> groupsToRemove = new HashSet<>();
            for (String name : inventoryGroups.keySet())
            {
                List<ItemStack> portInv = inventoryGroups.get(name);
                if (portInv != null)
                {
                    Map<String, List<ItemStack>> ig = modInventoryGroups.getOrDefault(name, new HashMap<>());
                    if (ig.get("vanilla") == null)
                    {
                        ig.put("vanilla", portInv);
                        groupsToRemove.add(name);
                        modInventoryGroups.put(name, ig);
                    }
                }
            }
            for (String name : groupsToRemove) {

                inventoryGroups.remove(name);
            }
            this.save();
        }

    }

    /**
     * Notifies the PlayerInfo to save itself to the Data store.
     */
    public void save()
    {
        if (ident.hasUuid())
        {
            DataManager.getInstance().save(this, ident.getUuid().toString());
        } else {
            LoggingHandler.felog.fatal("Unable to save player-info for {} because UUID is null!", ident.getUsername());
        }
    }

    public boolean isLoggedIn()
    {
        return ident.hasPlayer();
    }

    /* ------------------------------------------------------------ */
    public static PlayerInfo get(UUID uuid)
    {
        return get(uuid, null);
    }

    public static PlayerInfo get(UUID uuid, String username)
    {
        PlayerInfo info = playerInfoMap.get(uuid);
        if (info != null)
            return info;

        // Attempt to populate this info with some data from our storage
        info = DataManager.getInstance().load(PlayerInfo.class, uuid.toString());
        if (info != null)
        {
            playerInfoMap.put(uuid, info);
            return info;
        }

        // Create new player info data
        EntityPlayerMP player = UserIdent.get(uuid, username).getPlayerMP();
        info = new PlayerInfo(uuid);
        playerInfoMap.put(uuid, info);
        if (player != null)
            APIRegistry.getFEEventBus().post(new NoPlayerInfoEvent(player));
        return info;
    }

    public static PlayerInfo get(EntityPlayer player)
    {
        return get(player.getPersistentID(), player.getName());
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
     * Removes a timeout from the list
     */
    public void removeTimeout(String name)
    {
        namedTimeout.remove(name);
    }

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
     * @param name         Unique name of the timeout
     * @param milliseconds Timeout in milliseconds
     */
    public void startTimeout(String name, long milliseconds)
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

    public Map<String, Map<String, List<ItemStack>>> getModInventoryGroups()
    {
        return modInventoryGroups;
    }

    public List<ItemStack> getInventoryGroupItems(String name, String mod)
    {
        return modInventoryGroups.get(name).get(mod);
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
            Map<String, List<ItemStack>> newInventory = modInventoryGroups.get(name);
            if (newInventory == null)
                newInventory = new HashMap<>();

            // ChatOutputHandler.felog.info(String.format("Changing inventory group for %s from %s to %s",
            // ident.getUsernameOrUUID(), activeInventoryGroup, name));
            /*
             * ChatOutputHandler.felog.info("Items in old inventory:"); for (int i = 0; i <
             * ident.getPlayer().inventory.getSizeInventory(); i++) { ItemStack itemStack =
             * ident.getPlayer().inventory.getStackInSlot(i); if (itemStack != null) ChatOutputHandler.felog.info("  " +
             * itemStack.getDisplayName()); } ChatOutputHandler.felog.info("Items in new inventory:"); for (ItemStack
             * itemStack : newInventory) if (itemStack != null) ChatOutputHandler.felog.info("  " +
             * itemStack.getDisplayName());
             */

            // Swap player inventory and store the old one
            newInventory.put("vanilla", PlayerUtil.swapInventory(this.ident.getPlayerMP(), newInventory.getOrDefault("vanilla", new ArrayList<>())));
            MinecraftForge.EVENT_BUS.post(new InventoryGroupChange(ident.getPlayer(), name, newInventory));
            modInventoryGroups.put(activeInventoryGroup, newInventory);
            // Clear the inventory-group that was assigned to the player (optional)
            modInventoryGroups.put(name, null);
            // Save the new active inventory-group
            activeInventoryGroup = name;
            this.save();
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
        APIRegistry.getFEEventBus().post(new ClientHandshakeEstablished(this.ident.getPlayer()));
    }

    public boolean isNoClip()
    {
        return noClip;
    }

    public void setNoClip(boolean noClip)
    {
        this.noClip = noClip;
    }
}
