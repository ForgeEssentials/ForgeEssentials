package com.forgeessentials.permissions.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.permission.PermissionContext;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.UserIdent.UserIdentInvalidatedEvent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.ServerZone.PermissionDebugger;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

/**
 * Main permission management class
 */
public class ZonedPermissionHelper extends ServerEventHandler implements IPermissionsHelper, PermissionDebugger
{

    public static final String PERMISSIONS_LIST_FILE = "PermissionsList.txt";

    public static final String PERMISSIONS_LIST_ITEMS_FILE = "PermissionList_Items.txt";

    public static final String PERMISSIONS_LIST_BLOCKS_FILE = "PermissionList_Blocks.txt";

    private static final String NEW_LINE = System.getProperty("line.separator");

    protected RootZone rootZone;

    protected ZonePersistenceProvider persistenceProvider;

    protected boolean dirty = true;

    /**
     * First time that permissions have been changed. At least one minute after this time, permissions will definitely
     * be saved.
     */
    protected long firstDirtyTime = 0;

    /**
     * Last time that permissions have been changed. Five seconds after no more permissions have been changed, they will
     * be saved.
     */
    protected long lastDirtyTime = 0;

    protected boolean registeredPermission = true;

    private boolean disableDebug;

    public Set<ICommandSender> permissionDebugUsers = Collections.newSetFromMap(new WeakHashMap<ICommandSender, Boolean>());

    public List<String> permissionDebugFilters = new ArrayList<>();

    public boolean disableAutoSave = false;

    // public boolean verbosePermissionDebug = false;

    // ------------------------------------------------------------

    public ZonedPermissionHelper()
    {
        rootZone = new RootZone(this);
        rootZone.setPermissionDebugger(this);

        ServerZone serverZone = new ServerZone(rootZone);
        APIRegistry.getFEEventBus().post(new PermissionEvent.AfterLoad(serverZone));
        rootZone.setServerZone(serverZone);

        permissionDebugFilters.add("fe.protection.mobspawn");
        permissionDebugFilters.add("fe.protection.gamemode");
        permissionDebugFilters.add("fe.protection.inventory");
        permissionDebugFilters.add("fe.protection.exist");
        permissionDebugFilters.add("fe.protection.pressureplate");
        permissionDebugFilters.add("fe.commands.afk.autotime");
        // permissionDebugFilters.add("fe.economy.cmdprice");
        permissionDebugFilters.add("worldedit.limit.unrestricted");
        permissionDebugFilters.add("fe.worldborder.bypass");
    }

    // ------------------------------------------------------------
    // -- Persistence
    // ------------------------------------------------------------

    public void setPersistenceProvider(ZonePersistenceProvider persistenceProvider)
    {
        this.persistenceProvider = persistenceProvider;
    }

    public void save()
    {
        dirty = false;
        if (persistenceProvider != null)
        {
            LoggingHandler.felog.debug("Saving permissions...");
            APIRegistry.getFEEventBus().post(new PermissionEvent.BeforeSave(rootZone.getServerZone()));
            persistenceProvider.save(rootZone.getServerZone());
            dirty = false;
        }

        if (registeredPermission)
        {
            registeredPermission = false;
            writePermissionlist();
        }
    }

    public boolean load()
    {
        if (persistenceProvider != null)
        {
            ServerZone serverZone = persistenceProvider.load();
            if (serverZone != null)
            {
                // Set new server zone
                rootZone.setServerZone(serverZone);
                serverZone.rebuildZonesMap();
                dirty = false;
                APIRegistry.getFEEventBus().post(new PermissionEvent.AfterLoad(serverZone));
                return true;
            }
        }
        return false;
    }

    public void clear()
    {
        ServerZone serverZone = new ServerZone();
        rootZone.setServerZone(serverZone);
        serverZone.rebuildZonesMap();
        dirty = false;
        APIRegistry.getFEEventBus().post(new PermissionEvent.AfterLoad(serverZone));
    }

    public boolean isDirty()
    {
        return dirty;
    }

    @Override
    public void setDirty(boolean registeredPermission)
    {
        // if (verbosePermissionDebug)
        // {
        // ChatOutputHandler.felog.fine("PERMISSIONS SET DIRTY");
        // StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // for (int i = 2; i < stackTrace.length && i < 10; i++)
        // ChatOutputHandler.felog.fine(" " + stackTrace[i].toString());
        // }
        dirty = true;
        lastDirtyTime = System.currentTimeMillis();
        if (firstDirtyTime <= 0)
            firstDirtyTime = lastDirtyTime;
        this.registeredPermission |= registeredPermission;
    }

    public static boolean isItemPermission(String perm)
    {
        return perm.startsWith(ModuleProtection.PERM_INVENTORY) || //
                perm.startsWith(ModuleProtection.PERM_EXIST) || //
                perm.startsWith(ModuleProtection.PERM_USE) || //
                perm.startsWith(ModuleProtection.PERM_CRAFT);
    }

    public static boolean isBlockPermission(String perm)
    {
        return perm.startsWith(ModuleProtection.PERM_PLACE) || //
                perm.startsWith(ModuleProtection.PERM_BREAK) || //
                perm.startsWith(ModuleProtection.PERM_TRAMPLE) || //
                perm.startsWith(ModuleProtection.PERM_EXPLODE) || //
                perm.startsWith(ModuleProtection.PERM_INTERACT);
    }

    public void writePermissionlist()
    {
        PermissionList defaultPerms = rootZone.getGroupPermissions(Zone.GROUP_DEFAULT);
        PermissionList opPerms = rootZone.getGroupPermissions(Zone.GROUP_OPERATORS);
        opPerms.remove(null);
        defaultPerms.remove(null);

        File file = new File(ForgeEssentials.getFEDirectory(), PERMISSIONS_LIST_FILE);
        File fileItems = new File(ForgeEssentials.getFEDirectory(), PERMISSIONS_LIST_ITEMS_FILE);
        File fileBlocks = new File(ForgeEssentials.getFEDirectory(), PERMISSIONS_LIST_BLOCKS_FILE);

        TreeMap<String, String> permissions = new TreeMap<>(opPerms);
        permissions.putAll(defaultPerms);

        int permCount = 0;
        int permNameLength = 0;
        for (String perm : permissions.keySet())
            if (!perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
            {
                permCount++;
                permNameLength = Math.max(permNameLength, perm.length());
            }
        permNameLength += 2;

        try
        {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    BufferedWriter writerItems = new BufferedWriter(new FileWriter(fileItems));
                    BufferedWriter writerBlocks = new BufferedWriter(new FileWriter(fileBlocks)))
            {
                writer.write("#// ------------ PERMISSIONS LIST ---------- \\\\#");
                writer.newLine();
                writer.write("#// --------------- " + FEConfig.FORMAT_DATE_TIME.format(new Date()) + " ------------ \\\\#");
                writer.newLine();
                writer.write("#// ----------- Total amount: " + permCount + " --------- \\\\#");
                writer.newLine();
                writer.write("#// ---------------------------------------- \\\\#");
                writer.newLine();

                writerItems.write("#// --------- PERMISSIONS LIST ITEMS ------- \\\\#");
                writerItems.newLine();
                writerItems.write("#// --------------- " + FEConfig.FORMAT_DATE_TIME.format(new Date()) + " ------------ \\\\#");
                writerItems.newLine();
                writerItems.write("#// ----------- Total amount: " + permCount + " --------- \\\\#");
                writerItems.newLine();
                writerItems.write("#// ---------------------------------------- \\\\#");
                writerItems.newLine();

                writerBlocks.write("#// -------- PERMISSIONS LIST BLOCKS ------- \\\\#");
                writerBlocks.newLine();
                writerBlocks.write("#// --------------- " + FEConfig.FORMAT_DATE_TIME.format(new Date()) + " ------------ \\\\#");
                writerBlocks.newLine();
                writerBlocks.write("#// ----------- Total amount: " + permCount + " --------- \\\\#");
                writerBlocks.newLine();
                writerBlocks.write("#// ---------------------------------------- \\\\#");
                writerBlocks.newLine();

                for (Entry<String, String> permission : permissions.entrySet())
                {
                    String perm = permission.getKey();
                    if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                    {
                        perm = perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length());
                        String value = permissions.get(perm);
                        if (value == null)
                        {
                            StringBuffer sb = new StringBuffer();
                            sb.append(perm);
                            for (int i = perm.length(); i <= permNameLength; i++)
                                sb.append(' ');
                            sb.append("# ");
                            sb.append(permission.getValue());
                            sb.append(NEW_LINE);
                            if (isItemPermission(perm))
                                writerItems.write(sb.toString());
                            else if (isBlockPermission(perm))
                                writerBlocks.write(sb.toString());
                            else
                                writer.write(sb.toString());
                        }
                    }
                    else
                    {
                        String description = permissions.get(perm + FEPermissions.DESCRIPTION_PROPERTY);
                        String value = permission.getValue();
                        String opValue = opPerms.get(perm);
                        StringBuffer sb = new StringBuffer();
                        sb.append(perm);
                        for (int i = perm.length(); i <= permNameLength; i++)
                            sb.append(' ');
                        sb.append("# ");
                        if (opValue != null)
                            sb.append("(OP only: " + opValue + ") ");
                        else
                            sb.append("(default: " + value + ") ");
                        if (description != null)
                            sb.append(description);
                        sb.append(NEW_LINE);
                        if (isItemPermission(perm))
                            writerItems.write(sb.toString());
                        else if (isBlockPermission(perm))
                            writerBlocks.write(sb.toString());
                        else
                            writer.write(sb.toString());
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // -- Utilities
    // ------------------------------------------------------------

    public PermissionList getRegisteredPermissions()
    {
        PermissionList perms = (PermissionList) rootZone.getGroupPermissions(Zone.GROUP_DEFAULT).clone();
        perms.putAll(rootZone.getGroupPermissions(Zone.GROUP_OPERATORS));
        return perms;
    }

    public Set<String> enumAllPermissions()
    {
        Set<String> perms = new TreeSet<>();
        for (Zone zone : getZones())
        {
            for (Map<String, String> groupPerms : zone.getGroupPermissions().values())
            {
                for (String perm : groupPerms.keySet())
                {
                    if (!perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                        perms.add(perm);
                }
            }
            for (Map<String, String> playerPerms : zone.getPlayerPermissions().values())
            {
                for (String perm : playerPerms.keySet())
                {
                    if (!perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                        perms.add(perm);
                }
            }
        }
        return perms;
    }

    public Map<Zone, Map<String, String>> enumUserPermissions(UserIdent ident)
    {
        Map<Zone, Map<String, String>> result = new HashMap<>();
        for (Zone zone : getZones())
        {
            if (zone.getPlayerPermissions(ident) != null)
            {
                Map<String, String> zonePerms = new TreeMap<>();
                zonePerms.putAll(zone.getPlayerPermissions(ident));
                result.put(zone, zonePerms);
            }
        }
        return result;
    }

    public Map<Zone, Map<String, String>> enumGroupPermissions(String group, boolean enumRootPermissions)
    {
        Map<Zone, Map<String, String>> result = new HashMap<>();
        for (Zone zone : getZones())
        {
            if (!enumRootPermissions && zone instanceof RootZone)
                continue;
            if (zone.getGroupPermissions(group) != null)
            {
                Map<String, String> zonePerms = new TreeMap<>();
                zonePerms.putAll(zone.getGroupPermissions(group));
                result.put(zone, zonePerms);
            }
        }
        return result;
    }

    @Override
    public void debugPermission(Zone zone, UserIdent ident, String group, String permissionNode, String node, String value, WorldPoint point,
            boolean isGroupPermission)
    {
        if (disableDebug || permissionDebugUsers.isEmpty())
            return;
        for (String filter : permissionDebugFilters)
        {
            if (permissionNode.startsWith(filter))
                return;
        }

        IChatComponent msg1 = new ChatComponentText(String.format("%s = %s", permissionNode, value));
        msg1.getChatStyle().setColor(Zone.PERMISSION_FALSE.equals(value) ? EnumChatFormatting.RED : EnumChatFormatting.DARK_GREEN);

        IChatComponent msg2;
        if (zone == null)
        {
            msg2 = new ChatComponentText("  permission not set");
            msg2.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        }
        else
        {
            IChatComponent msgZone = new ChatComponentText(zone.getName());
            msgZone.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);

            IChatComponent msgUser = new ChatComponentText(ident == null ? APIRegistry.IDENT_SERVER.getUsername() : ident.getUsernameOrUuid());
            msgUser.getChatStyle().setColor(EnumChatFormatting.GOLD);

            if (isGroupPermission)
            {
                // String groupName = getServerZone().getGroupPermission(group, FEPermissions.GROUP_NAME);
                // if (groupName == null)
                // groupName = group;
                IChatComponent msgGroup = new ChatComponentText(group);
                msgGroup.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);

                msg2 = new ChatComponentTranslation("  zone %s group %s for user %s", msgZone, msgGroup, msgUser);
            }
            else
            {
                msg2 = new ChatComponentTranslation("  zone %s user %s", msgZone, msgUser);
            }
        }
        for (ICommandSender sender : permissionDebugUsers)
        {
            if (point != null && sender instanceof Entity && new WorldPoint((Entity) sender).distance(point) > 32)
                continue;
            ChatOutputHandler.sendMessage(sender, msg1);
            ChatOutputHandler.sendMessage(sender, msg2);
        }
    }

    public void disableDebugMode(boolean disable)
    {
        disableDebug = disable;
    }

    // ------------------------------------------------------------
    // -- Events
    // ------------------------------------------------------------

    @SubscribeEvent
    public void permissionAfterLoadEvent(PermissionEvent.AfterLoad event)
    {
        if (!event.serverZone.groupExists(Zone.GROUP_DEFAULT))
        {
            event.serverZone.setGroupPermission(Zone.GROUP_DEFAULT, FEPermissions.GROUP, true);
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, FEPermissions.GROUP_PRIORITY, "0");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, FEPermissions.GROUP_NAME, "global");
        }
        if (!event.serverZone.groupExists(Zone.GROUP_GUESTS))
        {
            event.serverZone.setGroupPermission(Zone.GROUP_GUESTS, FEPermissions.GROUP, true);
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_GUESTS, FEPermissions.GROUP_PRIORITY, "10");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_GUESTS, FEPermissions.PREFIX, "[GUEST]");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, FEPermissions.GROUP_NAME, "guests");
        }
        if (!event.serverZone.groupExists(Zone.GROUP_OPERATORS))
        {
            event.serverZone.setGroupPermission(Zone.GROUP_OPERATORS, FEPermissions.GROUP, true);
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_OPERATORS, FEPermissions.GROUP_PRIORITY, "50");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_OPERATORS, FEPermissions.PREFIX, "[&cOP&f]");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, FEPermissions.GROUP_NAME, "OPs");
        }
        if (!event.serverZone.groupExists(Zone.GROUP_PLAYERS))
        {
            event.serverZone.setGroupPermission(Zone.GROUP_PLAYERS, FEPermissions.GROUP, true);
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_PLAYERS, FEPermissions.GROUP_PRIORITY, "1");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, FEPermissions.GROUP_NAME, "players");
        }
        if (!event.serverZone.groupExists(Zone.GROUP_NPC))
        {
            event.serverZone.setGroupPermission(Zone.GROUP_NPC, FEPermissions.GROUP, true);
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_NPC, FEPermissions.GROUP_PRIORITY, "1");
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_NPC, FEPermissions.GROUP_NAME, "NPCs");
        }
        if (!event.serverZone.groupExists(Zone.GROUP_FAKEPLAYERS))
        {
            // Configure FakePlayer group
            // It can either use allow-all or inherit the permissions of another (OPs) group
            event.serverZone.setGroupPermission(Zone.GROUP_FAKEPLAYERS, FEPermissions.GROUP, true);
            event.serverZone.setGroupPermissionProperty(Zone.GROUP_FAKEPLAYERS, FEPermissions.GROUP_PRIORITY, "15");
            event.serverZone.setGroupPermission(Zone.GROUP_FAKEPLAYERS, ModuleProtection.BASE_PERM + ".*", true);
            // e.serverZone.groupParentAdd(Zone.GROUP_FAKEPLAYERS, Zone.GROUP_OPERATORS);
        }

        event.serverZone.setGroupPermission(Zone.GROUP_CREATIVE, FEPermissions.GROUP, true);
        event.serverZone.setGroupPermission(Zone.GROUP_ADVENTURE, FEPermissions.GROUP, true);

        event.serverZone.setPlayerPermission(APIRegistry.IDENT_SERVER, "*", true);
        event.serverZone.setPlayerPermission(APIRegistry.IDENT_CMDBLOCK, "*", true);
        event.serverZone.setPlayerPermission(APIRegistry.IDENT_RCON, "*", true);
    }

    @SubscribeEvent
    public void userIdentInvalidatedEvent(UserIdentInvalidatedEvent event)
    {
        for (Zone zone : getServerZone().getZones())
            zone.userIdentInvalidated(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLogin(PlayerLoggedInEvent e)
    {
        // Make sure each player has at least one permission
        UserIdent ident = UserIdent.get(e.player);
        if (getServerZone().getPlayerPermissions(ident) == null || getServerZone().getPlayerPermissions(ident).size() == 0)
            getServerZone().setPlayerPermission(ident, FEPermissions.PLAYER_KNOWN, true);
        else
            getServerZone().clearPlayerPermission(ident, FEPermissions.PLAYER_KNOWN);

        // Fire first zone-changed event
        WarpPoint point = new WarpPoint(e.player);
        Zone zone = APIRegistry.perms.getServerZone().getZonesAt(point.toWorldPoint()).get(0);
        PlayerChangedZone event = new PlayerChangedZone(e.player, zone, zone, point, point);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerLoggedOutEvent e)
    {
        permissionDebugUsers.remove(e.player);
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load e)
    {
        getServerZone().getWorldZone(e.world.provider.getDimensionId());
    }

    @SubscribeEvent
    public void playerMoveEvent(PlayerMoveEvent e)
    {
        Zone before = APIRegistry.perms.getServerZone().getZonesAt(e.before.toWorldPoint()).get(0);
        Zone after = APIRegistry.perms.getServerZone().getZonesAt(e.after.toWorldPoint()).get(0);
        if (!before.equals(after))
        {
            PlayerChangedZone event = new PlayerChangedZone(e.entityPlayer, before, after, e.before, e.after);
            e.setCanceled(MinecraftForge.EVENT_BUS.post(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerChangedZoneEvent(PlayerChangedZone event)
    {
        UserIdent ident = UserIdent.get(event.entityPlayer);
        String exitMsg = APIRegistry.perms.getUserPermissionProperty(ident, event.beforeZone, FEPermissions.ZONE_EXIT_MESSAGE);
        if (exitMsg != null)
        {
            ChatOutputHandler.sendMessage(event.entityPlayer, ChatOutputHandler.formatColors(exitMsg));
        }
        String entryMsg = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, FEPermissions.ZONE_ENTRY_MESSAGE);
        if (entryMsg != null)
        {
            ChatOutputHandler.sendMessage(event.entityPlayer, ChatOutputHandler.formatColors(entryMsg));
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        if (!disableAutoSave && dirty && (//
        System.currentTimeMillis() - lastDirtyTime > 1000 * 5 || //
                System.currentTimeMillis() - firstDirtyTime > 1000 * 60))
        {
            firstDirtyTime = 0;
            save();
        }
        // TODO: Detect manual changes to persistence backend
    }

    // ------------------------------------------------------------
    // -- Core permission handling
    // ------------------------------------------------------------

    /**
     * Main function for permission retrieval. This method should not be used directly. Use the helper methods instead.
     *
     * @param point
     * @param groups
     * @param permissionNode
     * @param isProperty
     * @return
     */
    @Override
    public String getPermission(UserIdent ident, WorldPoint point, WorldArea area, List<String> groups, String permissionNode, boolean isProperty)
    {
        // Get world zone
        WorldZone worldZone = null;
        if (point != null)
            worldZone = getServerZone().getWorldZone(point.getDimension());
        else if (area != null)
            worldZone = getServerZone().getWorldZone(area.getDimension());

        // Get zones in correct order
        List<Zone> zones = new ArrayList<>();
        if (worldZone != null)
        {
            for (Zone zone : worldZone.getAreaZones())
            {
                // TODO (2) It should be possible in some way to change zone inclusion to isPartOfZone instead of
                // isInZone
                // This is necessary for inverse allowing permissions (like explosions e.g.)
                if (point != null && zone.isInZone(point) || area != null && zone.isInZone(area))
                {
                    zones.add(zone);
                }
            }
            zones.add(worldZone);
        }
        zones.add(rootZone.getServerZone());
        zones.add(rootZone);

        if (isProperty)
            return getServerZone().getPermissionProperty(zones, ident, groups, permissionNode, point);
        else
            return getServerZone().getPermission(zones, ident, groups, permissionNode, point);
    }

    // ------------------------------------------------------------

    @Override
    public void registerPermissionProperty(String permissionNode, String defaultValue)
    {
        rootZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, permissionNode, defaultValue);
    }

    @Override
    public void registerPermissionPropertyOp(String permissionNode, String defaultValue)
    {
        rootZone.setGroupPermissionProperty(Zone.GROUP_OPERATORS, permissionNode, defaultValue);
    }

    @Override
    public void registerPermission(String permissionNode, PermissionLevel permLevel)
    {
        if (permLevel == PermissionLevel.FALSE)
            rootZone.setGroupPermission(Zone.GROUP_DEFAULT, permissionNode, false);
        else if (permLevel == PermissionLevel.TRUE)
            rootZone.setGroupPermission(Zone.GROUP_DEFAULT, permissionNode, true);
        else
        {
            rootZone.setGroupPermission(Zone.GROUP_DEFAULT, permissionNode, false);
            rootZone.setGroupPermission(Zone.GROUP_OPERATORS, permissionNode, true);
        }
    }

    @Override
    public void registerPermissionDescription(String permissionNode, String description)
    {
        registerPermissionProperty(permissionNode + FEPermissions.DESCRIPTION_PROPERTY, description);
    }

    @Override
    public void registerPermission(String permissionNode, PermissionLevel level, String description)
    {
        registerPermission(permissionNode, level);
        registerPermissionDescription(permissionNode, description);
    }

    @Override
    public void registerPermissionProperty(String permissionNode, String defaultValue, String description)
    {
        registerPermissionProperty(permissionNode, defaultValue);
        registerPermissionDescription(permissionNode, description);
    }

    @Override
    public void registerPermissionPropertyOp(String permissionNode, String defaultValue, String description)
    {
        registerPermissionPropertyOp(permissionNode, defaultValue);
        registerPermissionDescription(permissionNode, description);
    }

    @Override
    public void setPlayerPermission(UserIdent ident, String permissionNode, boolean value)
    {
        getServerZone().setPlayerPermission(ident, permissionNode, value);
    }

    @Override
    public void setPlayerPermissionProperty(UserIdent ident, String permissionNode, String value)
    {
        getServerZone().setPlayerPermissionProperty(ident, permissionNode, value);
    }

    @Override
    public void setGroupPermission(String group, String permissionNode, boolean value)
    {
        getServerZone().setGroupPermission(group, permissionNode, value);
    }

    @Override
    public void setGroupPermissionProperty(String group, String permissionNode, String value)
    {
        getServerZone().setGroupPermissionProperty(group, permissionNode, value);
    }

    @Override
    public String getPermissionDescription(String permissionNode)
    {
        return rootZone.getGroupPermission(Zone.GROUP_DEFAULT, permissionNode + FEPermissions.DESCRIPTION_PROPERTY);
    }

    // ------------------------------------------------------------
    // -- IPermissionProvider
    // ------------------------------------------------------------

    @Override
    public boolean checkPermission(PermissionContext context, String permissionNode)
    {
        if (context.isConsole())
            return true;

        EntityPlayer player = context.getPlayer();
        UserIdent ident = null;
        int dim = context.getDimension();
        WorldPoint loc = null;
        WorldArea area = null;

        if (context.getSender() instanceof DoAsCommandSender)
            ident = ((DoAsCommandSender) context.getSender()).getUserIdent();
        else if (context.getSender() instanceof CommandBlockLogic)
            ident = APIRegistry.IDENT_CMDBLOCK;
        else if (context.getSender() instanceof RConConsoleSource)
            ident = APIRegistry.IDENT_RCON;
        else if (player != null)
            ident = UserIdent.get(player);

        if (context.getTargetLocationStart() != null)
        {
            if (context.getTargetLocationEnd() != null)
                area = new WorldArea(dim, new Point(context.getTargetLocationStart()), new Point(context.getTargetLocationEnd()));
            else
                loc = new WorldPoint(dim, context.getTargetLocationStart());
        }
        else if (context.getSourceLocationStart() != null)
        {
            if (context.getSourceLocationEnd() != null)
                area = new WorldArea(dim, new Point(context.getSourceLocationStart()), new Point(context.getSourceLocationEnd()));
            else
                loc = new WorldPoint(dim, context.getSourceLocationStart());
        }

        SortedSet<GroupEntry> groups = getPlayerGroups(ident);
        return checkBooleanPermission(getPermission(ident, loc, area, GroupEntry.toList(groups), permissionNode, false));
    }

    // ------------------------------------------------------------
    // -- Zones
    // ------------------------------------------------------------

    @Override
    public Collection<Zone> getZones()
    {
        return getServerZone().getZones();
    }

    @Override
    public Zone getZoneById(int id)
    {
        return getServerZone().getZoneMap().get(id);
    }

    @Override
    public Zone getZoneById(String id)
    {
        try
        {
            return getServerZone().getZoneMap().get(Integer.parseInt(id));
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public RootZone getRootZone()
    {
        return rootZone;
    }

    @Override
    public ServerZone getServerZone()
    {
        return rootZone.getServerZone();
    }

    public Collection<Zone> getGlobalZones()
    {
        List<Zone> zones = new ArrayList<>();
        zones.add(rootZone.getServerZone());
        zones.add(rootZone);
        return zones;
    }

    public Collection<Zone> getGlobalZones(Zone firstZone)
    {
        List<Zone> zones = new ArrayList<>();
        if (firstZone != null)
            zones.add(firstZone);
        zones.add(rootZone.getServerZone());
        zones.add(rootZone);
        return zones;
    }

    // ------------------------------------------------------------
    // -- Groups
    // ------------------------------------------------------------

    @Override
    public boolean isSystemGroup(String group)
    {
        return Zone.GROUP_DEFAULT.equals(group) || //
                Zone.GROUP_GUESTS.equals(group) || //
                Zone.GROUP_OPERATORS.equals(group) || //
                Zone.GROUP_PLAYERS.equals(group) || //
                Zone.GROUP_FAKEPLAYERS.equals(group);
    }

    @Override
    public boolean groupExists(String name)
    {
        return getServerZone().getGroupPermissions().containsKey(name);
    }

    @Override
    public boolean createGroup(String name)
    {
        return getServerZone().createGroup(name);
    }

    @Override
    public void addPlayerToGroup(UserIdent ident, String group)
    {
        getServerZone().addPlayerToGroup(ident, group);
    }

    @Override
    public void removePlayerFromGroup(UserIdent ident, String group)
    {
        getServerZone().removePlayerFromGroup(ident, group);
    }

    @Override
    public String getPrimaryGroup(UserIdent ident)
    {
        return getServerZone().getPrimaryPlayerGroup(ident);
    }

    @Override
    public SortedSet<GroupEntry> getPlayerGroups(UserIdent ident)
    {
        return getServerZone().getPlayerGroups(ident);
    }

    @Override
    public SortedSet<GroupEntry> getStoredPlayerGroups(UserIdent ident)
    {
        return getServerZone().getStoredPlayerGroupEntries(ident);
    }

    // --------------------------------------------------------
    // -- Permission checking
    // ------------------------------------------------------------

    @Override
    public boolean checkBooleanPermission(String permissionValue)
    {
        if (permissionValue == null)
        {
            return true;
        }
        else
        {
            return !permissionValue.equals(Zone.PERMISSION_FALSE);
        }
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkPermission(EntityPlayer player, String permissionNode)
    {
        UserIdent ident = UserIdent.get(player);
        return checkBooleanPermission(getPermission(ident, new WorldPoint(player), null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
    }

    @Override
    public String getPermissionProperty(EntityPlayer player, String permissionNode)
    {
        UserIdent ident = UserIdent.get(player);
        return getPermission(ident, new WorldPoint(player), null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, String permissionNode)
    {
        WorldPoint point = null;
        if (ident != null && ident.hasPlayer())
            point = new WorldPoint(ident.getPlayer());
        return checkBooleanPermission(getPermission(ident, point, null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, String permissionNode)
    {
        WorldPoint point = null;
        if (ident != null && ident.hasPlayer())
            point = new WorldPoint(ident.getPlayer());
        return getPermission(ident, point, null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, true);
    }

    @Override
    public Integer getUserPermissionPropertyInt(UserIdent ident, String permissionNode)
    {
        String value = getUserPermissionProperty(ident, permissionNode);
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, WorldPoint targetPoint, String permissionNode)
    {
        return checkBooleanPermission(
                getPermission(ident, targetPoint, null, GroupEntry.toList(getServerZone().getPlayerGroups(ident, targetPoint)), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, WorldPoint targetPoint, String permissionNode)
    {
        return getPermission(ident, targetPoint, null, GroupEntry.toList(getServerZone().getPlayerGroups(ident, targetPoint)), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, WorldArea targetArea, String permissionNode)
    {
        return checkBooleanPermission(getPermission(ident, null, targetArea, GroupEntry.toList(getServerZone().getPlayerGroups(ident, targetArea.getCenter())),
                permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, WorldArea targetArea, String permissionNode)
    {
        return getPermission(ident, null, targetArea, GroupEntry.toList(getServerZone().getPlayerGroups(ident, targetArea.getCenter())), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, Zone zone, String permissionNode)
    {
        return checkBooleanPermission(
                getServerZone().getPermission(getGlobalZones(zone), ident, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, null));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, Zone zone, String permissionNode)
    {
        return getServerZone().getPermissionProperty(getGlobalZones(zone), ident, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, null);
    }

    // ------------------------------------------------------------

    @Override
    public String getGroupPermissionProperty(String group, String permissionNode)
    {
        return getServerZone().getPermissionProperty(getGlobalZones(), null, Arrays.asList(group), permissionNode, null);
    }

    @Override
    public String getGroupPermissionProperty(String group, Zone zone, String permissionNode)
    {
        return getServerZone().getPermissionProperty(getGlobalZones(zone), null, Arrays.asList(group), permissionNode, null);
    }

    @Override
    public boolean checkGroupPermission(String group, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getGlobalZones(), null, Arrays.asList(group), permissionNode, null));
    }

    @Override
    public boolean checkGroupPermission(String group, Zone zone, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getGlobalZones(zone), null, Arrays.asList(group), permissionNode, null));
    }

    @Override
    public String getGroupPermissionProperty(String group, WorldPoint point, String permissionNode)
    {
        return getServerZone().getPermissionProperty(getServerZone().getZonesAt(point), null, Arrays.asList(group), permissionNode, point);
    }

    @Override
    public boolean checkGroupPermission(String group, WorldPoint point, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getServerZone().getZonesAt(point), null, Arrays.asList(group), permissionNode, point));
    }

    // ------------------------------------------------------------

    @Override
    public String getGlobalPermissionProperty(String permissionNode)
    {
        return getGroupPermissionProperty(Zone.GROUP_DEFAULT, permissionNode);
    }

    @Override
    public String getGlobalPermissionProperty(Zone zone, String permissionNode)
    {
        return getGroupPermissionProperty(Zone.GROUP_DEFAULT, zone, permissionNode);
    }

    @Override
    public boolean checkGlobalPermission(String permissionNode)
    {
        return checkGroupPermission(Zone.GROUP_DEFAULT, permissionNode);
    }

    @Override
    public boolean checkGlobalPermission(Zone zone, String permissionNode)
    {
        return checkGroupPermission(Zone.GROUP_DEFAULT, zone, permissionNode);
    }

    // ------------------------------------------------------------

}
