package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.Loadable;
import com.google.gson.annotations.Expose;

/**
 * {@link ServerZone} contains every player on the whole server. Has second lowest priority with next being {@link RootZone}.
 */
public class ServerZone extends Zone implements Loadable
{

    @Expose(serialize = false)
    private RootZone rootZone;

    @Expose(serialize = false)
    private Map<Integer, Zone> zones = new HashMap<Integer, Zone>();

    private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

    @Expose(serialize = false)
    private int maxZoneID;

    private Map<UserIdent, Set<String>> playerGroups = new HashMap<UserIdent, Set<String>>();

    @Expose(serialize = false)
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

    @Override
    public void afterLoad()
    {
        for (WorldZone zone : worldZones.values())
        {
            zone.serverZone = this;
            zone.afterLoad();
        }
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
        return getWorldZone(world.provider.getDimensionId());
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
        Set<String> groupSet = playerGroups.get(ident);
        if (groupSet == null)
        {
            groupSet = new HashSet<String>();
            playerGroups.put(ident, groupSet);
        }
        if (!groupSet.contains(group))
        {
            if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(this, ident, PermissionEvent.User.ModifyGroups.Action.ADD, group)))
                return false;
            groupSet.add(group);
        }
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

    public Map<String, Set<UserIdent>> getGroupPlayers()
    {
        Map<String, Set<UserIdent>> groupPlayers = new HashMap<String, Set<UserIdent>>();
        for (Entry<UserIdent, Set<String>> player : playerGroups.entrySet())
        {
            for (String group : player.getValue())
            {
                Set<UserIdent> players = groupPlayers.get(group);
                if (players == null)
                {
                    players = new HashSet<UserIdent>();
                    groupPlayers.put(group, players);
                }
                players.add(player.getKey());
            }
        }
        return groupPlayers;
    }

    @Override
    public SortedSet<GroupEntry> getStoredPlayerGroupEntries(UserIdent ident)
    {
        registerPlayer(ident);
        Set<String> pgs = playerGroups.get(ident);
        SortedSet<GroupEntry> result = new TreeSet<GroupEntry>();
        if (pgs != null)
            for (String group : pgs)
                result.add(new GroupEntry(this, group));
        return result;
    }

    public SortedSet<GroupEntry> getAdditionalPlayerGroups(UserIdent ident, WorldPoint point)
    {
        SortedSet<GroupEntry> result = getStoredPlayerGroupEntries(ident);
        if (ident != null)
        {
            // Include special groups
            if (MinecraftServer.getServer().getConfigurationManager().canSendCommands(ident.getGameProfile()))
            {
                result.add(new GroupEntry(this, GROUP_OPERATORS));
            }
            if (ident.isFakePlayer())
            {
                result.add(new GroupEntry(this, GROUP_FAKEPLAYERS));
            }
            if (result.isEmpty() && ident.isPlayer())
                result.add(new GroupEntry(this, GROUP_GUESTS));
            if (!ident.isFakePlayer())
                result.add(new GroupEntry(GROUP_PLAYERS, 1, 1));
            if (ident.isNpc())
                result.add(new GroupEntry(GROUP_NPC, 1, 1));

            EntityPlayerMP player = ident.getPlayerMP();
            if (player != null && player.theItemInWorldManager != null)
                switch (player.theItemInWorldManager.getGameType())
                {
                case ADVENTURE:
                    result.add(new GroupEntry(this, GROUP_ADVENTURE));
                    break;
                case CREATIVE:
                    result.add(new GroupEntry(this, GROUP_CREATIVE));
                    break;
                default:
                    break;
                }
        }
        // Check groups added through zones
        if (point == null && ident != null && ident.hasPlayer())
            point = new WorldPoint(ident.getPlayer());
        if (ident != null && point != null)
            for (Zone z : getZonesAt(point))
                if (!(z instanceof ServerZone))
                    result.addAll(z.getStoredPlayerGroupEntries(ident));
        result.add(new GroupEntry(GROUP_DEFAULT, -1, -1));
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

    public SortedSet<GroupEntry> getPlayerGroups(UserIdent ident, WorldPoint point)
    {
        return includeGroups(getAdditionalPlayerGroups(ident, point));
    }

    public SortedSet<GroupEntry> getPlayerGroups(UserIdent ident)
    {
        return getPlayerGroups(ident, null);
    }

    public String getPrimaryPlayerGroup(UserIdent ident, WorldPoint point)
    {
        Iterator<GroupEntry> it = getPlayerGroups(ident, point).iterator();
        if (it.hasNext())
            return it.next().getGroup();
        else
            return null;
    }

    public String getPrimaryPlayerGroup(UserIdent ident)
    {
        return getPrimaryPlayerGroup(ident, null);
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
        result.add(rootZone);
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
        if (ident == null || knownPlayers.contains(ident))
            return;
        knownPlayers.add(ident);
        PermissionList map = getOrCreatePlayerPermissions(ident);
        if (map.isEmpty())
            map.put(FEPermissions.PLAYER_KNOWN, PERMISSION_TRUE);
    }

    public Set<UserIdent> getKnownPlayers()
    {
        return knownPlayers;
    }

    // ------------------------------------------------------------

    public String getPermission(Collection<Zone> zones, UserIdent ident, List<String> groups, String permissionNode, WorldPoint point)
    {
        // Build node list
        List<String> nodes = new ArrayList<String>();
        nodes.add(permissionNode);
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

        PermissionCheckEvent event = postPermissionCheckEvent(zones, ident, groups, nodes, false);
        if (event.result != null)
            return event.result;

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
                            rootZone.permissionDebugger.debugPermission(zone, ident, null, permissionNode, node, result, point, false);
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
                                rootZone.permissionDebugger.debugPermission(zone, ident, group, permissionNode, node, result, point, true);
                            return result;
                        }
                    }
                }
            }
        }
        if (rootZone.permissionDebugger != null)
            rootZone.permissionDebugger.debugPermission(null, ident, GROUP_DEFAULT, permissionNode, permissionNode, PERMISSION_TRUE, point, true);
        return null;
    }

    public String getPermissionProperty(Collection<Zone> zones, UserIdent ident, List<String> groups, String node, WorldPoint point)
    {
        PermissionCheckEvent event = postPermissionCheckEvent(zones, ident, groups, Arrays.asList(node), true);
        if (event.result != null)
            return event.result;

        // Check player permissions
        if (ident != null)
        {
            for (Zone zone : zones)
            {
                String result = zone.getPlayerPermission(ident, node);
                if (result != null)
                {
                    if (rootZone.permissionDebugger != null)
                        rootZone.permissionDebugger.debugPermission(zone, ident, null, node, node, result, point, false);
                    return result;
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
                    String result = zone.getGroupPermission(group, node);
                    if (result != null)
                    {
                        if (rootZone.permissionDebugger != null)
                            rootZone.permissionDebugger.debugPermission(zone, ident, group, node, node, result, point, true);
                        return result;
                    }
                }
            }
        }
        if (rootZone.permissionDebugger != null)
            rootZone.permissionDebugger.debugPermission(null, null, GROUP_DEFAULT, node, node, "null", point, true);
        return null;
    }

    public static PermissionCheckEvent postPermissionCheckEvent(Collection<Zone> zones, UserIdent ident, List<String> groups, List<String> nodes,
            boolean isProperty)
    {
        PermissionCheckEvent event = new PermissionCheckEvent(ident, zones, groups, nodes, isProperty);
        APIRegistry.FE_EVENTBUS.post(event);
        return event;
    }

    public static interface PermissionDebugger
    {

        void debugPermission(Zone zone, UserIdent ident, String group, String permissionNode, String node, String value, WorldPoint point,
                boolean isGroupPermission);

    }

}
