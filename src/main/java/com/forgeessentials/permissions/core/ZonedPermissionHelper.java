package com.forgeessentials.permissions.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.permissions.IContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * 
 * @author Olee
 */
public class ZonedPermissionHelper implements IPermissionsHelper {

    protected RootZone rootZone;

    protected ZonePersistenceProvider persistenceProvider;

    protected Timer persistenceTimer;

    protected boolean dirty;

    // ------------------------------------------------------------

    public ZonedPermissionHelper()
    {
        FMLCommonHandler.instance().bus().register(this);
        rootZone = new RootZone(this);
        rootZone.setServerZone(new ServerZone(rootZone));
    }

    // ------------------------------------------------------------
    // -- Persistence
    // ------------------------------------------------------------

    class PersistenceTask extends TimerTask {

        @Override
        public void run()
        {
            if (persistenceProvider == null)
                return;
            if (dirty)
            {
                save();
            }
            else
            {
                // TODO: Detect manual changes to persistence backend
            }
        }

    }

    public void setPersistenceProvider(ZonePersistenceProvider persistenceProvider)
    {
        this.persistenceProvider = persistenceProvider;
    }

    public void save()
    {
        if (persistenceProvider != null)
        {
            OutputHandler.felog.info("Saving permissions...");
            persistenceProvider.save(rootZone.getServerZone());
        }
        dirty = false;
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
    public void setDirty()
    {
        this.dirty = true;
        if (persistenceTimer != null)
            persistenceTimer.cancel();
        persistenceTimer = new Timer("permission persistence timer", true);
        persistenceTimer.schedule(new PersistenceTask(), 2000);
    }

    // ------------------------------------------------------------
    // -- Utilities
    // ------------------------------------------------------------

    public PermissionList getRegisteredPermissions()
    {
        PermissionList perms = (PermissionList) rootZone.getGroupPermissions(IPermissionsHelper.GROUP_DEFAULT).clone();
        for (Entry<String, String> perm : rootZone.getGroupPermissions(IPermissionsHelper.GROUP_OPERATORS).entrySet())
            perms.put(perm.getKey(), perm.getValue());
        return perms;
    }

    public Set<String> enumRegisteredPermissions()
    {
        Set<String> perms = new TreeSet<String>();
        for (String perm : rootZone.getGroupPermissions(IPermissionsHelper.GROUP_DEFAULT).keySet())
        {
            if (!perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
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

    // ------------------------------------------------------------
    // -- Events
    // ------------------------------------------------------------

    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent e)
    {
        for (Zone zone : getZones())
        {
            zone.updatePlayerIdents();
        }
    }

    // ------------------------------------------------------------
    // -- Core permission handling
    // ------------------------------------------------------------

    /**
     * Main function for permission retrieval. This method should not be used directly. Use the helper methods instead.
     * 
     * @param playerId
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
            worldZone = getWorldZone(point.getDimension());
        else if (area != null)
            worldZone = getWorldZone(area.getDimension());

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

        return getPermission(zones, ident, groups, permissionNode, isProperty);
    }

    public String getPermission(Collection<Zone> zones, UserIdent ident, Collection<String> groups, String permissionNode, boolean isProperty)
    {
        // Build node list
        List<String> nodes = new ArrayList<String>();
        nodes.add(permissionNode);
        if (!isProperty)
        {
            String[] nodeParts = permissionNode.split("\\.");
            for (int i = nodeParts.length; i > 0; i--)
            {
                String node = "";
                for (int j = 0; j < i; j++)
                {
                    node += nodeParts[j] + ".";
                }
                nodes.add(node + PERMISSION_ASTERIX);
            }
            nodes.add(PERMISSION_ASTERIX);
        }

        // Check player permissions
        if (ident != null)
        {
            for (Zone zone : zones)
            {
                for (String node : nodes)
                {
                    String result = zone.getPlayerPermission(ident, node);
                    if (result != null)
                    {
                        return result;
                    }
                }
            }
        }

        // Check group permissions
        // Add default group
        if (groups != null)
        {
            // Lowest order: group hierarchy
            // (e.g. ADMIN, MEMBER, _OPS_, _ALL_)
            for (String group : groups)
            {
                // Second order: zones
                // (e.g. area, world, server, root)
                for (Zone zone : zones)
                {
                    // First order: nodes
                    // (e.g. fe.commands.time, fe.commands.time.*, fe.commands.*, fe.*, *)
                    for (String node : nodes)
                    {
                        String result = zone.getGroupPermission(group, node);
                        if (result != null)
                        {
                            return result;
                        }
                    }
                }
            }
        }

        // Check default permissions
        for (String node : nodes)
        {
            // Check group permissions
            for (Zone zone : zones)
            {
                String result = zone.getGroupPermission(GROUP_DEFAULT, node);
                if (result != null)
                {
                    return result;
                }
            }
        }

        return null;
    }

    // ------------------------------------------------------------

    @Override
    public void registerPermissionProperty(String permissionNode, String defaultValue)
    {
        rootZone.setGroupPermissionProperty(GROUP_DEFAULT, permissionNode, defaultValue);
    }

    @Override
    public void registerPermissionPropertyOp(String permissionNode, String defaultValue)
    {
        rootZone.setGroupPermissionProperty(GROUP_OPERATORS, permissionNode, defaultValue);
    }

    @Override
    public void registerPermission(String permissionNode, PermissionsManager.RegisteredPermValue permLevel)
    {
        if (permLevel == RegisteredPermValue.FALSE)
            rootZone.setGroupPermission(GROUP_DEFAULT, permissionNode, false);
        else if (permLevel == RegisteredPermValue.TRUE)
            rootZone.setGroupPermission(GROUP_DEFAULT, permissionNode, true);
        else if (permLevel == RegisteredPermValue.OP)
        {
            rootZone.setGroupPermission(GROUP_DEFAULT, permissionNode, false);
            rootZone.setGroupPermission(GROUP_OPERATORS, permissionNode, true);
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
        return rootZone.getGroupPermission(GROUP_DEFAULT, permissionNode + FEPermissions.DESCRIPTION_PROPERTY);
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

        return checkBooleanPermission(getPermission(ident, loc, area, getPlayerGroups(ident), permissionNode, false));
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

    @Override
    public WorldZone getWorldZone(int dimensionId)
    {
        WorldZone zone = rootZone.getServerZone().getWorldZones().get(dimensionId);
        if (zone == null)
        {
            zone = new WorldZone(getServerZone(), dimensionId);
        }
        return zone;
    }

    @Override
    public WorldZone getWorldZone(World world)
    {
        return getWorldZone(world.provider.dimensionId);
    }

    @Override
    public List<Zone> getZonesAt(WorldPoint worldPoint)
    {
        WorldZone w = getWorldZone(worldPoint.getDimension());
        List<Zone> result = new ArrayList<Zone>();
        for (AreaZone zone : w.getAreaZones())
            if (zone.isInZone(worldPoint))
                result.add(zone);
        result.add(w);
        result.add(w.getParent());
        return result;
    }

    @Override
    public List<AreaZone> getAreaZonesAt(WorldPoint worldPoint)
    {
        WorldZone w = getWorldZone(worldPoint.getDimension());
        List<AreaZone> result = new ArrayList<AreaZone>();
        for (AreaZone zone : w.getAreaZones())
            if (zone.isInZone(worldPoint))
                result.add(zone);
        return result;
    }

    @Override
    public Zone getZoneAt(WorldPoint worldPoint)
    {
        List<Zone> zones = getZonesAt(worldPoint);
        return zones.isEmpty() ? null : zones.get(0);
    }

    @Override
    public AreaZone getAreaZoneAt(WorldPoint worldPoint)
    {
        List<AreaZone> zones = getAreaZonesAt(worldPoint);
        return zones.isEmpty() ? null : zones.get(0);
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
        return group.equals(IPermissionsHelper.GROUP_DEFAULT) || group.equals(IPermissionsHelper.GROUP_OPERATORS)
                || group.equals(IPermissionsHelper.GROUP_GUESTS);
    }

    @Override
    public boolean groupExists(String name)
    {
        return getServerZone().getGroupPermissions().containsKey(name);
    }

    @Override
    public void createGroup(String name)
    {
        getServerZone().createGroup(name);
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
    public SortedSet<String> getPlayerGroups(UserIdent ident)
    {
        return getServerZone().getPlayerGroups(ident);
    }

    @Override
    public SortedSet<String> getStoredPlayerGroups(UserIdent ident)
    {
        return getServerZone().getStoredPlayerGroups(ident);
    }

    // ------------------------------------------------------------
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
            return !permissionValue.equals(PERMISSION_FALSE);
        }
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkPermission(EntityPlayer player, String permissionNode)
    {
        UserIdent ident = new UserIdent(player);
        return checkBooleanPermission(getPermission(ident, new WorldPoint(player), null, getPlayerGroups(ident), permissionNode, false));
    }

    @Override
    public String getPermissionProperty(EntityPlayer player, String permissionNode)
    {
        UserIdent ident = new UserIdent(player);
        return getPermission(ident, new WorldPoint(player), null, getPlayerGroups(ident), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, String permissionNode)
    {
        return checkBooleanPermission(getPermission(ident, ident.hasPlayer() ? new WorldPoint(ident.getPlayer()) : null, null, getPlayerGroups(ident),
                permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, String permissionNode)
    {
        return getPermission(ident, ident.hasPlayer() ? new WorldPoint(ident.getPlayer()) : null, null, getPlayerGroups(ident), permissionNode, true);
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
        return checkBooleanPermission(getPermission(ident, targetPoint, null, getPlayerGroups(ident), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, WorldPoint targetPoint, String permissionNode)
    {
        return getPermission(ident, targetPoint, null, getPlayerGroups(ident), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, WorldArea targetArea, String permissionNode)
    {
        return checkBooleanPermission(getPermission(ident, null, targetArea, getPlayerGroups(ident), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, WorldArea targetArea, String permissionNode)
    {
        return getPermission(ident, null, targetArea, getPlayerGroups(ident), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public boolean checkUserPermission(UserIdent ident, Zone zone, String permissionNode)
    {
        return checkBooleanPermission(getPermission(getGlobalZones(zone), ident, getPlayerGroups(ident), permissionNode, false));
    }

    @Override
    public String getUserPermissionProperty(UserIdent ident, Zone zone, String permissionNode)
    {
        return getPermission(getGlobalZones(zone), ident, getPlayerGroups(ident), permissionNode, true);
    }

    // ------------------------------------------------------------

    @Override
    public String getGroupPermissionProperty(String group, String permissionNode)
    {
        return getPermission(getGlobalZones(), null, Arrays.asList(group), permissionNode, true);
    }

    @Override
    public String getGroupPermissionProperty(String group, Zone zone, String permissionNode)
    {
        return getPermission(getGlobalZones(zone), null, Arrays.asList(group), permissionNode, true);
    }

    @Override
    public boolean checkGroupPermission(String group, String permissionNode)
    {
        return checkBooleanPermission(getPermission(getGlobalZones(), null, Arrays.asList(group), permissionNode, false));
    }

    @Override
    public boolean checkGroupPermission(String group, Zone zone, String permissionNode)
    {
        return checkBooleanPermission(getPermission(getGlobalZones(zone), null, Arrays.asList(group), permissionNode, false));
    }

    @Override
    public String getGroupPermissionProperty(String group, WorldPoint point, String permissionNode)
    {
        return getPermission(getZonesAt(point), null, Arrays.asList(group), permissionNode, true);
    }

    @Override
    public boolean checkGroupPermission(String group, WorldPoint point, String permissionNode)
    {
        return checkBooleanPermission(getPermission(getZonesAt(point), null, Arrays.asList(group), permissionNode, false));
    }

    // ------------------------------------------------------------

    @Override
    public String getGlobalPermissionProperty(String permissionNode)
    {
        return getGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, permissionNode);
    }

    @Override
    public String getGlobalPermissionProperty(Zone zone, String permissionNode)
    {
        return getGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, zone, permissionNode);
    }

    @Override
    public boolean checkGlobalPermission(String permissionNode)
    {
        return checkGroupPermission(IPermissionsHelper.GROUP_DEFAULT, permissionNode);
    }

    @Override
    public boolean checkGlobalPermission(Zone zone, String permissionNode)
    {
        return checkGroupPermission(IPermissionsHelper.GROUP_DEFAULT, zone, permissionNode);
    }

    // ------------------------------------------------------------

}
