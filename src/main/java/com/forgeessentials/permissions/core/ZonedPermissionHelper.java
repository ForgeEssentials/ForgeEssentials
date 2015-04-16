package com.forgeessentials.permissions.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fe.server.CommandHandlerForge;
import net.minecraftforge.permissions.IContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
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
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * 
 * @author Olee
 */
public class ZonedPermissionHelper extends ServerEventHandler implements IPermissionsHelper, PermissionDebugger {

    public static final String PERMISSIONS_LIST_FILE = "PermissionsList.txt";

    protected RootZone rootZone;

    protected ZonePersistenceProvider persistenceProvider;

    protected boolean dirty = true;

    protected long lastDirty = 0;

    protected boolean registeredPermission = true;

    public Set<ICommandSender> permissionDebugUsers = new HashSet<>();

    public List<String> permissionDebugFilters = new ArrayList<>();

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
        permissionDebugFilters.add("worldedit.limit.unrestricted");
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
            OutputHandler.felog.fine("Saving permissions...");
            APIRegistry.getFEEventBus().post(new PermissionEvent.BeforeSave(rootZone.getServerZone()));
            persistenceProvider.save(rootZone.getServerZone());
        }

        if (registeredPermission)
        {
            registeredPermission = false;
            PermissionsListWriter.write(rootZone, new File(ForgeEssentials.getFEDirectory(), PERMISSIONS_LIST_FILE));
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
                serverZone.updatePlayerIdents();
                dirty = false;
                APIRegistry.getFEEventBus().post(new PermissionEvent.AfterLoad(serverZone));
                return true;
            }
        }
        return false;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    @Override
    public void setDirty(boolean registeredPermission)
    {
        this.dirty = true;
        this.lastDirty = System.currentTimeMillis();
        this.registeredPermission |= registeredPermission;
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

    public Set<String> enumRegisteredPermissions()
    {
        Set<String> perms = new TreeSet<String>();
        for (String perm : rootZone.getGroupPermissions(Zone.GROUP_DEFAULT).keySet())
        {
            if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                perm = perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length());
            perms.add(perm);
        }
        return perms;
    }

    public Set<String> enumAllPermissions()
    {
        Set<String> perms = new TreeSet<String>();
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
        Map<Zone, Map<String, String>> result = new HashMap<Zone, Map<String, String>>();
        for (Zone zone : getZones())
        {
            if (zone.getPlayerPermissions(ident) != null)
            {
                Map<String, String> zonePerms = new TreeMap<String, String>();
                zonePerms.putAll(zone.getPlayerPermissions(ident));
                result.put(zone, zonePerms);
            }
        }
        return result;
    }

    public Map<Zone, Map<String, String>> enumGroupPermissions(String group, boolean enumRootPermissions)
    {
        Map<Zone, Map<String, String>> result = new HashMap<Zone, Map<String, String>>();
        for (Zone zone : getZones())
        {
            if (!enumRootPermissions && zone instanceof RootZone)
                continue;
            if (zone.getGroupPermissions(group) != null)
            {
                Map<String, String> zonePerms = new TreeMap<String, String>();
                zonePerms.putAll(zone.getGroupPermissions(group));
                result.put(zone, zonePerms);
            }
        }
        return result;
    }

    @Override
    public void debugPermission(Zone zone, UserIdent ident, String group, String permissionNode, String node, String value)
    {
        for (String filter : permissionDebugFilters)
        {
            if (node.startsWith(filter))
                return;
        }
        String msg1 = String.format("\u00a7b%s\u00a7f = \u00a7%s%s", permissionNode, Zone.PERMISSION_FALSE.equals(value) ? "4" : "2", value);
        String msg2;
        if (zone == null)
            msg2 = "\u00a74  permission not set";
        else if (ident == null)
            msg2 = String.format("\u00a7f  zone [\u00a75%s\u00a7f] group [\u00a75%s\u00a7f]", zone.getName(), group);
        else
            msg2 = String.format("\u00a7f  zone [\u00a75%s\u00a7f] user [\u00a75%s\u00a7f]", zone.getName(), ident.getUsernameOrUUID());
        for (ICommandSender sender : permissionDebugUsers)
        {
            OutputHandler.chatNotification(sender, msg1);
            OutputHandler.chatNotification(sender, msg2);
        }
    }

    // ------------------------------------------------------------
    // -- Events
    // ------------------------------------------------------------

    @SubscribeEvent
    public void permissionAfterLoadEvent(PermissionEvent.AfterLoad e)
    {
        if (!e.serverZone.groupExists(Zone.GROUP_DEFAULT))
        {
            e.serverZone.setGroupPermission(Zone.GROUP_DEFAULT, FEPermissions.GROUP, true);
            e.serverZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, FEPermissions.GROUP_PRIORITY, "0");
        }
        if (!e.serverZone.groupExists(Zone.GROUP_GUESTS))
        {
            e.serverZone.setGroupPermission(Zone.GROUP_GUESTS, FEPermissions.GROUP, true);
            e.serverZone.setGroupPermissionProperty(Zone.GROUP_GUESTS, FEPermissions.GROUP_PRIORITY, "10");
            e.serverZone.setGroupPermissionProperty(Zone.GROUP_GUESTS, FEPermissions.PREFIX, "[GUEST]");
        }
        if (!e.serverZone.groupExists(Zone.GROUP_OPERATORS))
        {
            e.serverZone.setGroupPermission(Zone.GROUP_OPERATORS, FEPermissions.GROUP, true);
            e.serverZone.setGroupPermissionProperty(Zone.GROUP_OPERATORS, FEPermissions.GROUP_PRIORITY, "50");
            e.serverZone.setGroupPermissionProperty(Zone.GROUP_OPERATORS, FEPermissions.PREFIX, "[OPERATOR]");
        }
        if (!e.serverZone.groupExists(Zone.GROUP_FAKEPLAYERS))
        {
            // Configure FakePlayer group
            // It can either use allow-all or inherit the permissions of another (OPs) group
            e.serverZone.setGroupPermission(Zone.GROUP_FAKEPLAYERS, FEPermissions.GROUP, true);
            e.serverZone.setGroupPermissionProperty(Zone.GROUP_FAKEPLAYERS, FEPermissions.GROUP_PRIORITY, "15");
            e.serverZone.setGroupPermission(Zone.GROUP_FAKEPLAYERS, Zone.PERMISSION_ASTERIX, true);
            //e.serverZone.groupParentAdd(Zone.GROUP_FAKEPLAYERS, Zone.GROUP_OPERATORS);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLogin(PlayerLoggedInEvent e)
    {
        // Update permission storage with new players
        for (Zone zone : getZones())
            zone.updatePlayerIdents();

        // Make sure each player has at least one permission
        UserIdent ident = new UserIdent(e.player);
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
        getServerZone().getWorldZone(e.world.provider.dimensionId);
    }
    
    @SubscribeEvent
    public void playerMoveEvent(PlayerMoveEvent e)
    {
        // Abort processing, if the event has already been cancelled
        if (!e.isCanceled())
        {
            Zone before = APIRegistry.perms.getServerZone().getZonesAt(e.before.toWorldPoint()).get(0);
            Zone after = APIRegistry.perms.getServerZone().getZonesAt(e.after.toWorldPoint()).get(0);
            if (!before.equals(after))
            {
                PlayerChangedZone event = new PlayerChangedZone(e.entityPlayer, before, after, e.before, e.after);
                e.setCanceled(MinecraftForge.EVENT_BUS.post(event));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerChangedZoneEvent(PlayerChangedZone event)
    {
        UserIdent ident = new UserIdent(event.entityPlayer);
        String exitMsg = APIRegistry.perms.getUserPermissionProperty(ident, event.beforeZone, FEPermissions.ZONE_EXIT_MESSAGE);
        if (exitMsg != null)
        {
            OutputHandler.sendMessage(event.entityPlayer, FunctionHelper.formatColors(exitMsg));
        }
        String entryMsg = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, FEPermissions.ZONE_ENTRY_MESSAGE);
        if (entryMsg != null)
        {
            OutputHandler.sendMessage(event.entityPlayer, FunctionHelper.formatColors(entryMsg));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkCommandPerm(CommandEvent e)
    {
        if (!(e.command instanceof ForgeEssentialsCommandBase) && e.sender instanceof EntityPlayer && !CommandHandlerForge.canUse(e.command, e.sender))
        {
            e.setCanceled(true);
            permissionDeniedMessage(e.sender);
        }
    }

    public static void permissionDeniedMessage(ICommandSender sender)
    {
        ChatComponentTranslation msg = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
        msg.getChatStyle().setColor(EnumChatFormatting.RED);
        sender.addChatMessage(msg);
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        if (dirty && System.currentTimeMillis() - lastDirty > 1000 * 60)
            save();
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
    public String getPermission(UserIdent ident, WorldPoint point, WorldArea area, Collection<String> groups, String permissionNode, boolean isProperty)
    {
        // Get world zone
        WorldZone worldZone = null;
        if (point != null)
            worldZone = getServerZone().getWorldZone(point.getDimension());
        else if (area != null)
            worldZone = getServerZone().getWorldZone(area.getDimension());

        // Get zones in correct order
        List<Zone> zones = new ArrayList<Zone>();
        if (worldZone != null)
        {
            for (Zone zone : worldZone.getAreaZones())
            {
                if (point != null && zone.isInZone(point) || area != null && zone.isInZone(area))
                {
                    zones.add(zone);
                }
            }
            zones.add(worldZone);
        }
        zones.add(rootZone.getServerZone());
        zones.add(rootZone);

        return getServerZone().getPermission(zones, ident, groups, permissionNode, isProperty);
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
    public void registerPermission(String permissionNode, PermissionsManager.RegisteredPermValue permLevel)
    {
        if (permLevel == RegisteredPermValue.FALSE)
            rootZone.setGroupPermission(Zone.GROUP_DEFAULT, permissionNode, false);
        else if (permLevel == RegisteredPermValue.TRUE)
            rootZone.setGroupPermission(Zone.GROUP_DEFAULT, permissionNode, true);
        else if (permLevel == RegisteredPermValue.OP)
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
    public void registerPermission(String permissionNode, RegisteredPermValue level, String description)
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
    /**
     * Will return the player if set or the commandSender, if it is an instance of {@link EntityPlayer}
     */
    protected static EntityPlayer contextGetPlayerOrCommandPlayer(IContext context)
    {
        return (context.getPlayer() != null) ? context.getPlayer() : (context.getCommandSender() instanceof EntityPlayer ? (EntityPlayer) context
                .getCommandSender() : null);
    }

    protected static boolean contextIsConsole(IContext context)
    {
        return context.getPlayer() == null && context.getCommandSender() != null && !(context.getCommandSender() instanceof EntityPlayer);
    }

    protected static boolean contextIsPlayer(IContext context)
    {
        return (context.getPlayer() != null) || (context.getCommandSender() instanceof EntityPlayer);
    }

    @Override
    public boolean checkPermission(IContext context, String permissionNode)
    {
        if (contextIsConsole(context))
            return true;

        UserIdent ident = null;
        EntityPlayer player = contextGetPlayerOrCommandPlayer(context);
        WorldPoint loc = null;
        WorldArea area = null;
        int dim = 0;

        if (player != null)
        {
            ident = new UserIdent(player);
            // TODO: should be changed to context.getDimension()
            dim = player.dimension;
        }

        if (context.getTargetLocationStart() != null)
        {
            if (context.getTargetLocationEnd() != null)
            {
                area = new WorldArea(dim, new Point(context.getTargetLocationStart()), new Point(context.getTargetLocationEnd()));
            }
            else
            {
                loc = new WorldPoint(dim, context.getTargetLocationStart());
            }
        }
        else if (context.getSourceLocationStart() != null)
        {
            if (context.getSourceLocationEnd() != null)
            {
                area = new WorldArea(dim, new Point(context.getSourceLocationStart()), new Point(context.getSourceLocationEnd()));
            }
            else
            {
                loc = new WorldPoint(dim, context.getSourceLocationStart());
            }
        }
        else
        {
            if (player != null)
            {
                loc = new WorldPoint(player);
            }
        }

        return checkBooleanPermission(getPermission(ident, loc, area, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
        // return checkPermission(player, node);
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
        return getRootZone().getServerZone();
    }

    public Collection<Zone> getGlobalZones()
    {
        List<Zone> zones = new ArrayList<Zone>();
        zones.add(rootZone.getServerZone());
        zones.add(rootZone);
        return zones;
    }

    public Collection<Zone> getGlobalZones(Zone firstZone)
    {
        List<Zone> zones = new ArrayList<Zone>();
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
        return group.equals(Zone.GROUP_DEFAULT) || group.equals(Zone.GROUP_OPERATORS) || group.equals(Zone.GROUP_GUESTS);
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
        return getServerZone().getStoredPlayerGroups(ident);
    }
    
    // --------------------------------------------------------
    // -- Permission checking
    // ------------------------------------------------------------

    protected boolean checkBooleanPermission(String permissionValue)
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
        UserIdent ident = new UserIdent(player);
        return checkBooleanPermission(getPermission(ident, new WorldPoint(player), null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
    }

    @Override
    public String getPermissionProperty(EntityPlayer player, String permissionNode)
    {
        UserIdent ident = new UserIdent(player);
        return getPermission(ident, new WorldPoint(player), null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, String permissionNode)
    {
        return checkBooleanPermission(getPermission(ident, ident != null && ident.hasPlayer() ? new WorldPoint(ident.getPlayer()) : null, null,
                GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, String permissionNode)
    {
        return getPermission(ident, ident.hasPlayer() ? new WorldPoint(ident.getPlayer()) : null, null, GroupEntry.toList(getPlayerGroups(ident)),
                permissionNode, true);
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
        return checkBooleanPermission(getPermission(ident, targetPoint, null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, WorldPoint targetPoint, String permissionNode)
    {
        return getPermission(ident, targetPoint, null, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, WorldArea targetArea, String permissionNode)
    {
        return checkBooleanPermission(getPermission(ident, null, targetArea, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, WorldArea targetArea, String permissionNode)
    {
        return getPermission(ident, null, targetArea, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, Zone zone, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getGlobalZones(zone), ident, GroupEntry.toList(getPlayerGroups(ident)), permissionNode,
                false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, Zone zone, String permissionNode)
    {
        return getServerZone().getPermission(getGlobalZones(zone), ident, GroupEntry.toList(getPlayerGroups(ident)), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public String getGroupPermissionProperty(String group, String permissionNode)
    {
        return getServerZone().getPermission(getGlobalZones(), null, Arrays.asList(group), permissionNode, true);
    }

    @Override
    public String getGroupPermissionProperty(String group, Zone zone, String permissionNode)
    {
        return getServerZone().getPermission(getGlobalZones(zone), null, Arrays.asList(group), permissionNode, true);
    }

    @Override
    public boolean checkGroupPermission(String group, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getGlobalZones(), null, Arrays.asList(group), permissionNode, false));
    }

    @Override
    public boolean checkGroupPermission(String group, Zone zone, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getGlobalZones(zone), null, Arrays.asList(group), permissionNode, false));
    }

    @Override
    public String getGroupPermissionProperty(String group, WorldPoint point, String permissionNode)
    {
        return getServerZone().getPermission(getServerZone().getZonesAt(point), null, Arrays.asList(group), permissionNode, true);
    }

    @Override
    public boolean checkGroupPermission(String group, WorldPoint point, String permissionNode)
    {
        return checkBooleanPermission(getServerZone().getPermission(getServerZone().getZonesAt(point), null, Arrays.asList(group), permissionNode, false));
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
