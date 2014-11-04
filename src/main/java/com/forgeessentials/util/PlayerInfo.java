package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.network.S1PacketSelectionUpdate;
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
public class PlayerInfo
{

    private static HashMap<UUID, PlayerInfo> playerInfoMap = new HashMap<UUID, PlayerInfo>();

    // -------------------------------------------------------------------------------------------

    public static ISelectionProvider selectionProvider = new FESelectionProvider();

    public static boolean persistSelections;

    public static class FESelectionProvider implements ISelectionProvider
    {

        @Override
        public Point getPoint1(EntityPlayerMP player)
        {
            PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
            return pi.sel1;
        }

        @Override
        public Point getPoint2(EntityPlayerMP player)
        {
            PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
            return pi.sel2;
        }

        @Override
        public Selection getSelection(EntityPlayerMP player)
        {
            PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
            if (pi.sel1 == null || pi.sel2 == null)
            {
                return null;
            }
            return new Selection(pi.sel1, pi.sel2);
        }

        @Override
        public void setPoint1(EntityPlayerMP player, Point sel1)
        {
            PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
            pi.sel1 = sel1;
            pi.sendSelectionUpdate();
        }

        @Override
        public void setPoint2(EntityPlayerMP player, Point sel2)
        {
            PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
            pi.sel2 = sel2;
            pi.sendSelectionUpdate();
        }

    }

    // -------------------------------------------------------------------------------------------

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
    private int timePlayed;

    private long loginTime;

    @SaveableField()
    private long firstJoin;

    // undo and redo stuff
    private Stack<BackupArea> undos;

    private Stack<BackupArea> redos;

    @SaveableField
    private List<ItemStack> gamemodeInventory;
    
    @SaveableField
    private GameType gamemodeInventoryType;

    private boolean hasFEClient;

    private PlayerInfo(UUID uuid)
    {
        this.ident = new UserIdent(uuid);
        this.uuid_string = uuid.toString();

        sel1 = null;
        sel2 = null;

        undos = new Stack<BackupArea>();
        redos = new Stack<BackupArea>();

        firstJoin = System.currentTimeMillis();
        loginTime = System.currentTimeMillis();

        timePlayed = 0;

        gamemodeInventory = new ArrayList<ItemStack>();
        gamemodeInventoryType = GameType.NOT_SET;
        hasFEClient = false;
    }

    @SuppressWarnings("unchecked")
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
        
        info.gamemodeInventory = (List<ItemStack>) tag.getFieldValue("gamemodeInventory");
        info.gamemodeInventoryType = (GameType) tag.getFieldValue("gamemodeInventoryType");
        
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

    public static PlayerInfo getPlayerInfo(EntityPlayer player)
    {
        return getPlayerInfo(player.getPersistentID());
    }

    public static PlayerInfo getPlayerInfo(UserIdent ident)
    {
        if (!ident.hasUUID())
        {
            return null;
        }
        return getPlayerInfo(ident.getUuid());
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

    public Point getPoint1()
    {
        return selectionProvider.getPoint1(ident.getPlayer());
    }

    public Point getPoint2()
    {
        return selectionProvider.getPoint2(ident.getPlayer());
    }

    public Selection getSelection()
    {
        return selectionProvider.getSelection(ident.getPlayer());
    }

    public void clearSelection()
    {
        if (!ForgeEssentials.worldEditCompatilityPresent)
        {
            sel1 = null;
            sel2 = null;
            FunctionHelper.netHandler.sendTo(new S1PacketSelectionUpdate(this), ident.getPlayer());
        }
    }

    public void sendSelectionUpdate()
    {
        FunctionHelper.netHandler.sendTo(new S1PacketSelectionUpdate(this), ident.getPlayer());
    }
    
    // ----------------------------------------------
    // ---------- protection gamemode ---------------
    // ----------------------------------------------

    public List<ItemStack> getGamemodeInventory()
    {
        return gamemodeInventory;
    }

    public void setGamemodeInventory(List<ItemStack> stacks)
    {
        gamemodeInventory = stacks;
    }

    public GameType getGamemodeInventoryType()
    {
        return gamemodeInventoryType;
    }

    public void setGamemodeInventoryType(GameType gamemodeInventoryType)
    {
        this.gamemodeInventoryType = gamemodeInventoryType;
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

}
