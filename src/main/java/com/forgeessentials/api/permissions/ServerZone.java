package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.UserIdent;

/**
 * {@link ServerZone} contains every player on the whole server. Has second lowest priority with next being
 * {@link RootZone}.
 * 
 * @author Olee
 */
public class ServerZone extends Zone {

    private RootZone rootZone;

    private Map<Integer, Zone> zones = new HashMap<Integer, Zone>();

    private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

    private int maxZoneID;

    private Map<UserIdent, Set<String>> playerGroups = new HashMap<UserIdent, Set<String>>();

    private Set<UserIdent> knownPlayers = new HashSet<UserIdent>();

    // ------------------------------------------------------------

    public ServerZone()
    {
        super(1);
        APIRegistry.getFEEventBus().post(new PermissionEvent.Initialize(this));
        addZone(this);
    }

    public ServerZone(RootZone rootZone)
    {
        this();
        this.maxZoneID = 1;
        this.rootZone = rootZone;
        this.rootZone.setServerZone(this);
        addZone(this.rootZone);
    }

    // ------------------------------------------------------------

    @Override
    public boolean isInZone(WorldPoint point)
    {
        return true;
    }

    @Override
    public boolean isInZone(WorldArea point)
    {
        return true;
    }

    @Override
    public boolean isPartOfZone(WorldArea point)
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "_SERVER_";
    }

    @Override
    public Zone getParent()
    {
        return rootZone;
    }

    @Override
    public ServerZone getServerZone()
    {
        return this;
    }

    void setRootZone(RootZone rootZone)
    {
        this.rootZone = rootZone;
        addZone(this.rootZone);
    }

    public RootZone getRootZone()
    {
        return rootZone;
    }

    public int getMaxZoneID()
    {
        return maxZoneID;
    }

    public int nextZoneID()
    {
        return ++maxZoneID;
    }

    public void setMaxZoneId(int maxId)
    {
        this.maxZoneID = maxId;
    }

    // ------------------------------------------------------------

    public Map<Integer, WorldZone> getWorldZones()
    {
        return worldZones;
    }

    public void addWorldZone(WorldZone zone)
    {
        worldZones.put(zone.getDimensionID(), zone);
        addZone(zone);
        setDirty();
    }

    public WorldZone getWorldZone(int dimensionId)
    {
        WorldZone zone = getWorldZones().get(dimensionId);
        if (zone == null)
        {
            zone = new WorldZone(getServerZone(), dimensionId);
        }
        return zone;
    }

    public WorldZone getWorldZone(World world)
    {
        return getWorldZone(world.provider.dimensionId);
    }

    // ------------------------------------------------------------

    public Set<String> getGroups()
    {
        return getGroupPermissions().keySet();
    }

    public boolean groupExists(String name)
    {
        return getGroupPermissions().containsKey(name);
    }

    public boolean createGroup(String name)
    {
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.Group.Create(this, name)))
            return false;
        setGroupPermission(name, FEPermissions.GROUP, true);
        setGroupPermissionProperty(name, FEPermissions.GROUP_PRIORITY, Integer.toString(FEPermissions.GROUP_PRIORITY_DEFAULT));
        setDirty();
        return true;
    }

    // ------------------------------------------------------------

    public Set<String> getIncludedGroups(String group)
    {
        Set<String> result = new HashSet<>();
        String groupsStr = getGroupPermission(group, FEPermissions.GROUP_INCLUDES);
        if (groupsStr != null && !groupsStr.isEmpty())
            for (String g : groupsStr.replaceAll(" ", "").split(","))
                if (!g.isEmpty())
                    result.add(g);
        return result;
    }

    public void groupIncludeAdd(String group, String otherGroup)
    {
        Set<String> groups = getIncludedGroups(group);
        groups.add(otherGroup);
        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_INCLUDES, StringUtils.join(groups, ","));
    }

    public void groupIncludeRemove(String group, String otherGroup)
    {
        Set<String> groups = getIncludedGroups(group);
        groups.remove(otherGroup);
        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_INCLUDES, StringUtils.join(groups, ","));
    }

    // ------------------------------------------------------------

    public Set<String> getParentedGroups(String group)
    {
        Set<String> result = new HashSet<>();
        String groupsStr = getGroupPermission(group, FEPermissions.GROUP_PARENTS);
        if (groupsStr != null && !groupsStr.isEmpty())
            for (String g : groupsStr.replaceAll(" ", "").split(","))
                if (!g.isEmpty())
                    result.add(g);
        return result;
    }

    public void groupParentAdd(String group, String otherGroup)
    {
        Set<String> groups = getIncludedGroups(group);
        groups.add(otherGroup);
        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_PARENTS, StringUtils.join(groups, ","));
    }

    public void groupParentRemove(String group, String otherGroup)
    {
        Set<String> groups = getIncludedGroups(group);
        groups.remove(otherGroup);
        APIRegistry.perms.setGroupPermissionProperty(group, FEPermissions.GROUP_PARENTS, StringUtils.join(groups, ","));
    }

    // ------------------------------------------------------------

    @Override
    public boolean addPlayerToGroup(UserIdent ident, String group)
    {
        registerPlayer(ident);
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(this, ident, PermissionEvent.User.ModifyGroups.Action.ADD, group)))
            return false;
        Set<String> groupSet = playerGroups.get(ident);
        if (groupSet == null)
        {
            groupSet = new TreeSet<String>();
            playerGroups.put(ident, groupSet);
        }
        groupSet.add(group);
        setDirty();
        return true;
    }

    @Override
    public boolean removePlayerFromGroup(UserIdent ident, String group)
    {
        registerPlayer(ident);
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(this, ident, PermissionEvent.User.ModifyGroups.Action.REMOVE, group)))
            return false;
        Set<String> groupSet = playerGroups.get(ident);
        if (groupSet != null)
            groupSet.remove(group);
        setDirty();
        return true;
    }

    // ------------------------------------------------------------

    public Map<UserIdent, Set<String>> getPlayerGroups()
    {
        return playerGroups;
    }

    @Override
    public SortedSet<GroupEntry> getStoredPlayerGroups(UserIdent ident)
    {
        registerPlayer(ident);
        Set<String> pgs = playerGroups.get(ident);
        SortedSet<GroupEntry> result = new TreeSet<GroupEntry>();
        if (pgs != null)
            for (String group : pgs)
                result.add(new GroupEntry(this, group));
        return result;
    }

    public SortedSet<GroupEntry> getAdditionalPlayerGroups(UserIdent ident)
    {
        SortedSet<GroupEntry> result = getStoredPlayerGroups(ident);
        if (ident != null)
        {
            if (ident.hasGameProfile() && !ident.isFakePlayer() && MinecraftServer.getServer().getConfigurationManager().func_152596_g(ident.getGameProfile()))
            {
                result.add(new GroupEntry(this, GROUP_OPERATORS));
            }
            if (result.isEmpty())
            {
                result.add(new GroupEntry(this, GROUP_GUESTS));
            }
        }
        result.add(new GroupEntry(GROUP_DEFAULT, 0, 0));
        return result;
    }

    public SortedSet<GroupEntry> includeGroups(SortedSet<GroupEntry> groups)
    {
        // Get included groups
        Set<String> checkedGroups = new HashSet<>();
        boolean addedGroup;
        do
        {
            addedGroup = false;
            for (GroupEntry existingGroup : new ArrayList<GroupEntry>(groups))
            {
                // Check if group was already checked for inclusion
                if (!checkedGroups.add(existingGroup.getGroup()))
                    continue;
                String p = getGroupPermission(existingGroup.getGroup(), FEPermissions.GROUP_INCLUDES);
                if (p != null)
                {
                    for (String group : p.replaceAll(" ", "").split(","))
                        if (!group.isEmpty())
                            addedGroup |= groups.add(new GroupEntry(this, group));
                }

                p = getGroupPermission(existingGroup.getGroup(), FEPermissions.GROUP_PARENTS);
                if (p != null)
                {
                    for (String group : p.replaceAll(" ", "").split(","))
                        if (!group.isEmpty())
                            addedGroup |= groups.add(new GroupEntry(this, group, existingGroup.getPriority()));
                }
            }
        }
        while (addedGroup);

        return groups;
    }

    public SortedSet<GroupEntry> getPlayerGroups(UserIdent ident)
    {
        SortedSet<GroupEntry> result = getAdditionalPlayerGroups(ident);
        // Check groups added through zones
        if (ident != null)
            for (Zone z : getZonesAt(ident))
                if (!(z instanceof ServerZone))
                    result.addAll(z.getStoredPlayerGroups(ident));
        return includeGroups(result);
    }

    public String getPrimaryPlayerGroup(UserIdent ident)
    {
        Iterator<GroupEntry> it = getPlayerGroups(ident).iterator();
        if (it.hasNext())
            return it.next().getGroup();
        else
            return null;
    }

    // ------------------------------------------------------------

    public void addZone(Zone zone)
    {
        zones.put(zone.getId(), zone);
    }

    public boolean removeZone(Zone zone)
    {
        return zones.remove(zone.getId()) != null;
    }

    public void rebuildZonesMap()
    {
        zones.clear();
        addZone(getRootZone());
        addZone(this);
        for (WorldZone worldZone : worldZones.values())
        {
            addZone(worldZone);
            for (AreaZone areaZone : worldZone.getAreaZones())
            {
                addZone(areaZone);
            }
        }
    }

    public Map<Integer, Zone> getZoneMap()
    {
        return zones;
    }

    public Collection<Zone> getZones()
    {
        return zones.values();
    }

    public List<Zone> getZonesAt(WorldPoint worldPoint)
    {
        WorldZone w = getWorldZone(worldPoint.getDimension());
        List<Zone> result = new ArrayList<Zone>();
        for (AreaZone zone : w.getAreaZones())
            if (zone.isInZone(worldPoint))
                result.add(zone);
        result.add(w);
        result.add(this);
        return result;
    }

    public List<Zone> getZonesAt(UserIdent ident)
    {
        if (ident == null)
        {
            return new ArrayList<>();
        }
        else if (ident.hasPlayer())
        {
            return getZonesAt(new WorldPoint(ident.getPlayer()));
        }
        else
        {
            ArrayList<Zone> result = new ArrayList<>();
            result.add(this);
            return result;
        }
    }

    public Zone getZoneAt(WorldPoint worldPoint)
    {
        List<Zone> zones = getZonesAt(worldPoint);
        return zones.isEmpty() ? null : zones.get(0);
    }

    public List<AreaZone> getAreaZonesAt(WorldPoint worldPoint)
    {
        WorldZone w = getWorldZone(worldPoint.getDimension());
        List<AreaZone> result = new ArrayList<AreaZone>();
        for (AreaZone zone : w.getAreaZones())
            if (zone.isInZone(worldPoint))
                result.add(zone);
        return result;
    }

    public AreaZone getAreaZoneAt(WorldPoint worldPoint)
    {
        List<AreaZone> zones = getAreaZonesAt(worldPoint);
        return zones.isEmpty() ? null : zones.get(0);
    }

    // ------------------------------------------------------------

    public void registerPlayer(UserIdent ident)
    {
        if (ident != null)
            knownPlayers.add(ident);
    }

    public Set<UserIdent> getKnownPlayers()
    {
        return knownPlayers;
    }

    // ------------------------------------------------------------

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
                        if (rootZone.permissionDebugger != null)
                            rootZone.permissionDebugger.debugPermission(zone, ident, null, permissionNode, node, result);
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
                            if (rootZone.permissionDebugger != null)
                                rootZone.permissionDebugger.debugPermission(zone, null, group, permissionNode, node, result);
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
                    if (rootZone.permissionDebugger != null)
                        rootZone.permissionDebugger.debugPermission(zone, null, GROUP_DEFAULT, permissionNode, node, result);
                    return result;
                }
            }
        }

        if (rootZone.permissionDebugger != null)
            rootZone.permissionDebugger.debugPermission(null, null, GROUP_DEFAULT, permissionNode, permissionNode, PERMISSION_TRUE);
        return null;
    }

    public static interface PermissionDebugger {

        void debugPermission(Zone zone, UserIdent ident, String group, String permissionNode, String node, String value);

    }

}
