package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import com.forgeessentials.util.selections.ISelectionProvider;
import cpw.mods.fml.common.Mod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.network.S1PacketSelectionUpdate;
import com.forgeessentials.data.v2.DataManager;
import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@SaveableObject
public class PlayerInfo {

    private static HashMap<UUID, PlayerInfo> playerInfoMap = new HashMap<UUID, PlayerInfo>();

    public static boolean persistSelections;

    @SaveableField()
    private UserIdent ident;

    @UniqueLoadingKey
    private final String uuid_string;

    // wand stuff
    private String wandID;

    private int wandDmg;

    private boolean wandEnabled = false;

    @SaveableField()
    private WarpPoint home;

    @SaveableField()
    private WarpPoint lastTeleportOrigin;

    @SaveableField()
    private WarpPoint lastDeathLocation;

    // 0: Normal 1: World spawn 2: Bed 3: Home
    @SaveableField()
    private int spawnType;

    private long lastTeleportTime = 0;

    private HashMap<String, Integer> kitCooldown = new HashMap<String, Integer>();

    // selection stuff
    @SaveableField()
    protected Point sel1;

    @SaveableField()
    protected Point sel2;

    @SaveableField()
    private int timePlayed = 0;

    private long loginTime = System.currentTimeMillis();

    @SaveableField()
    private long firstJoin = System.currentTimeMillis();

    // undo and redo stuff
    private Stack<BackupArea> undos = new Stack<BackupArea>();

    private Stack<BackupArea> redos = new Stack<BackupArea>();

    @SaveableField
    private Map<String, List<ItemStack>> inventoryGroups = new HashMap<>();

    @SaveableField
    private String activeInventoryGroup = "default";

    private boolean hasFEClient = false;

    protected PlayerInfo()
    {
        uuid_string = null;
    }

    protected PlayerInfo(UUID uuid)
    {
        this.ident = new UserIdent(uuid);
        this.uuid_string = uuid.toString();
    }

    @Reconstructor()
    public static PlayerInfo reconstruct(IReconstructData tag)
    {
        UUID uuid = UUID.fromString(tag.getUniqueKey());
        PlayerInfo info = new PlayerInfo(uuid);

        if (persistSelections)
        {
            info.sel1 = ((Point) tag.getFieldValue("sel1"));
            info.sel2 = ((Point) tag.getFieldValue("sel2"));
        }

        info.home = (WarpPoint) tag.getFieldValue("home");
        info.lastTeleportOrigin = (WarpPoint) tag.getFieldValue("lastTeleportOrigin");
        info.lastDeathLocation = (WarpPoint) tag.getFieldValue("lastDeathLocation");

        info.spawnType = (Integer) tag.getFieldValue("spawnType");
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
        DataManager.getInstance().save(this, uuid_string);
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
                info = new PlayerInfo(playerID);
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

    public Point getSel1()
    {
        return sel1;
    }

    public Point getSel2()
    {
        return sel2;
    }

    public void setSel1(Point point)
    {
        sel1 = point;
    }

    public void setSel2(Point point)
    {
        sel2 = point;
    }

    public void clearSelection()
    {
        if (!ModuleLauncher.getModuleList().contains("WEIntegration"))
        {
            sel1 = null;
            sel2 = null;
            FunctionHelper.netHandler.sendTo(new S1PacketSelectionUpdate(ident.getPlayer()), ident.getPlayer());
        }
    }

    public void sendSelectionUpdate()
    {
        FunctionHelper.netHandler.sendTo(new S1PacketSelectionUpdate(ident.getPlayer()), ident.getPlayer());
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
