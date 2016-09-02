package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.UserIdent.UserIdentInvalidatedEvent;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;

/**
 * Zones are used to store permissions in a tree-like hierarchy. Each zone has it's own set of group- and
 * player-permissions. Zones are stored in a tree structure with fixed levels. Priorities for permissions are based on
 * the level of each zone in the tree. The following list shows the structure of the tree:
 * 
 * <pre>
 * {@link RootZone} &gt; {@link ServerZone} &gt; {@link WorldZone} &gt; {@link AreaZone}
 * </pre>
 */
public abstract class Zone
{

    public static final String GROUP_DEFAULT = "_ALL_";
    public static final String GROUP_GUESTS = "_GUESTS_";
    public static final String GROUP_PLAYERS = "_PLAYERS_";
    public static final String GROUP_NPC = "_NPC_";
    public static final String GROUP_OPERATORS = "_OPS_";
    public static final String GROUP_FAKEPLAYERS = "_FAKEPLAYERS_";
    public static final String GROUP_CREATIVE = "_CREATIVE_";
    public static final String GROUP_ADVENTURE = "_ADVENTURE_";

    public static final String PERMISSION_ASTERIX = "*";
    public static final String PERMISSION_FALSE = "false";
    public static final String PERMISSION_TRUE = "true";
    public static final String ALL_PERMS = '.' + PERMISSION_ASTERIX;

    public static class PermissionList extends HashMap<String, String>
    {
        private static final long serialVersionUID = 1L;

        public List<String> toList()
        {
            List<String> list = new ArrayList<>();
            for (Map.Entry<String, String> perm : this.entrySet())
            {
                if (perm.getValue() == null)
                    continue;
                if (perm.getValue().equals(PERMISSION_TRUE))
                {
                    list.add(perm.getKey());
                }
                else if (perm.getValue().equals(PERMISSION_FALSE))
                {
                    list.add("-" + perm.getKey());
                }
                else
                {
                    list.add(perm.getKey() + "=" + perm.getValue());
                }
            }
            Collections.sort(list);
            return list;
        }

        public static PermissionList fromList(List<String> fromList)
        {
            PermissionList list = new PermissionList();
            for (String permission : fromList)
            {
                String[] permParts = permission.split("=");
                if (permParts.length == 2)
                    list.put(permParts[0], permParts[1]);
                else if (permParts.length == 1)
                {
                    if (permission.startsWith("-"))
                        list.put(permission.substring(1, permission.length()), PERMISSION_FALSE);
                    else
                        list.put(permission, PERMISSION_TRUE);
                }
            }
            return list;
        }

        public PermissionList()
        {
        }

        public PermissionList(Map<? extends String, ? extends String> clone)
        {
            super(clone);
        }
    }

    public static final Comparator<Object> permissionComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2)
        {
            if (!(o1 instanceof String && o2 instanceof String))
                return 0;
            String s1 = (String) o1;
            String s2 = (String) o2;

            if (s1.startsWith(FEPermissions.PLAYER))
            {
                if (s2.startsWith(FEPermissions.PLAYER))
                    return s1.compareTo(s2);
                else
                    return -1;
            }
            else
            {
                if (s2.startsWith(FEPermissions.PLAYER))
                    return 1;
            }

            if (s1.startsWith(FEPermissions.GROUP))
            {
                if (s2.startsWith(FEPermissions.GROUP))
                    return s1.compareTo(s2);
                else
                    return -1;
            }
            else
            {
                if (s2.startsWith(FEPermissions.GROUP))
                    return 1;
            }

            if (s1.startsWith(FEPermissions.FE_INTERNAL))
            {
                if (s2.startsWith(FEPermissions.FE_INTERNAL))
                    return s1.compareTo(s2);
                else
                    return -1;
            }
            else
            {
                if (s2.startsWith(FEPermissions.FE_INTERNAL))
                    return 1;
                else
                    return s1.compareTo(s2);
            }
        }
    };

    private int id;

    protected Map<UserIdent, PermissionList> playerPermissions = new HashMap<>();

    protected Map<String, PermissionList> groupPermissions = new HashMap<>();

    public Zone(int id)
    {
        this.id = id;
    }

    /**
     * Gets the unique zone-ID
     */
    public int getId()
    {
        return id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    /**
     * Checks, whether the player is in the zone.
     * 
     * @param player
     */
    public boolean isPlayerInZone(EntityPlayer player)
    {
        return isInZone(new WorldPoint(player));
    }

    /**
     * Checks, whether the player is in the zone.
     * 
     * @param point
     */
    public abstract boolean isInZone(WorldPoint point);

    /**
     * Checks, whether the area is entirely contained within the zone.
     * 
     * @param point
     */
    public abstract boolean isInZone(WorldArea point);

    /**
     * Checks, whether a part of the area is in the zone.
     * 
     * @param point
     */
    public abstract boolean isPartOfZone(WorldArea point);

    /**
     * Returns the name of the zone
     */
    public abstract String getName();

    /**
     * Returns the name of the zone
     */
    @Override
    public String toString()
    {
        return getName();
    }

    /**
     * Get the parent zone
     */
    public abstract Zone getParent();

    public abstract ServerZone getServerZone();

    public void setDirty()
    {
        if (getServerZone() != null && getServerZone().getRootZone() != null)
            getServerZone().getRootZone().getPermissionHelper().setDirty(false);
    }

    /**
     * Checks, if the zone is hidden. Returns false for all zones except {@link AreaZone}s
     */
    public boolean isHidden()
    {
        return false;
    }

    // ------------------------------------------------------------
    // -- Player permissions
    // ------------------------------------------------------------

    /**
     * Get all player permissions as a map
     */
    public Map<UserIdent, PermissionList> getPlayerPermissions()
    {
        return playerPermissions;
    }

    /**
     * Gets the player permissions for the specified player, or null if not present.
     * 
     * @param ident
     */
    public PermissionList getPlayerPermissions(UserIdent ident)
    {
        return playerPermissions.get(ident);
    }

    /**
     * Gets the player permissions for the specified player. If no permission-map is present, a new one is created.
     * 
     * @param ident
     */
    public PermissionList getOrCreatePlayerPermissions(UserIdent ident)
    {
        PermissionList map = playerPermissions.get(ident);
        if (map == null)
        {
            map = new PermissionList();
            playerPermissions.put(ident, map);
            return map;
        }
        return playerPermissions.get(ident);
    }

    /**
     * Returns the value of a player permission, or null if empty.
     * 
     * @param ident
     * @param permissionNode
     */
    public String getPlayerPermission(UserIdent ident, String permissionNode)
    {
        PermissionList map = getPlayerPermissions(ident);
        if (map != null)
        {
            return map.get(permissionNode);
        }
        return null;
    }

    /**
     * Returns the value of a player permission, or null if empty.
     * 
     * @param player
     * @param permissionNode
     * @return permission value or null, if not set
     */
    public String getPlayerPermission(EntityPlayer player, String permissionNode)
    {
        return getPlayerPermission(UserIdent.get(player), permissionNode);
    }

    /**
     * Checks, if a player permission is true, false or empty.
     * 
     * @param ident
     * @param permissionNode
     * @return true / false or null, if not set
     */
    public Boolean checkPlayerPermission(UserIdent ident, String permissionNode)
    {
        PermissionList map = getPlayerPermissions(ident);
        if (map != null)
        {
            String permValue = map.get(permissionNode);
            return !PERMISSION_FALSE.equalsIgnoreCase(permValue);
        }
        return null;
    }

    /**
     * Set a player permission-property
     * 
     * @param ident
     * @param permissionNode
     * @param value
     */
    public boolean setPlayerPermissionProperty(UserIdent ident, String permissionNode, String value)
    {
        if (ident != null && !APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyPermission(getServerZone(), ident, this, permissionNode, value)))
        {
            getServerZone().registerPlayer(ident);
            PermissionList map = getOrCreatePlayerPermissions(ident);
            if (value == null)
                map.remove(permissionNode);
            else
                map.put(permissionNode, value);
            setDirty();
            return true;
        }
        return false;
    }

    /**
     * Set a player permission
     * 
     * @param ident
     * @param permissionNode
     * @param value
     */
    public boolean setPlayerPermission(UserIdent ident, String permissionNode, boolean value)
    {
        return setPlayerPermissionProperty(ident, permissionNode, value ? PERMISSION_TRUE : PERMISSION_FALSE);
    }

    /**
     * Clears a player permission
     * 
     * @param ident
     * @param permissionNode
     */
    public boolean clearPlayerPermission(UserIdent ident, String permissionNode)
    {
        if (ident != null)
        {
            PermissionList map = getPlayerPermissions(ident);
            if (map != null && !APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyPermission(getServerZone(), ident, this, permissionNode, null)))
            {
                map.remove(permissionNode);
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------

    private Set<String> getPlayerGroups(UserIdent ident)
    {
        Set<String> result = new HashSet<>();
        String groupsStr = getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        if (groupsStr != null && !groupsStr.isEmpty())
            for (String g : groupsStr.replaceAll(" ", "").split(","))
                if (!g.isEmpty())
                    result.add(g);
        return result;
    }

    public boolean addPlayerToGroup(UserIdent ident, String group)
    {
        if (APIRegistry.getFEEventBus()
                .post(new PermissionEvent.User.ModifyGroups(getServerZone(), ident, PermissionEvent.User.ModifyGroups.Action.ADD, group)))
            return false;
        Set<String> groups = getPlayerGroups(ident);
        groups.add(group);
        setPlayerPermissionProperty(ident, FEPermissions.PLAYER_GROUPS, StringUtils.join(groups, ","));
        return true;
    }

    public boolean removePlayerFromGroup(UserIdent ident, String group)
    {
        if (APIRegistry.getFEEventBus().post(
                new PermissionEvent.User.ModifyGroups(getServerZone(), ident, PermissionEvent.User.ModifyGroups.Action.REMOVE, group)))
            return false;
        Set<String> groups = getPlayerGroups(ident);
        groups.remove(group);
        if (!groups.isEmpty())
            setPlayerPermissionProperty(ident, FEPermissions.PLAYER_GROUPS, StringUtils.join(groups, ","));
        else
            clearPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        return true;
    }

    /**
     * Return a list of the user's groups in this zone
     */
    public Set<String> getStoredPlayerGroups(UserIdent ident)
    {
        Set<String> result = new HashSet<>();
        String groupsStr = getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        if (groupsStr != null && !groupsStr.isEmpty())
            for (String group : groupsStr.replace(" ", "").split(","))
                result.add(group);
        return result;
    }

    /**
     * Return a list of the user's groups in this zone
     */
    public SortedSet<GroupEntry> getStoredPlayerGroupEntries(UserIdent ident)
    {
        SortedSet<GroupEntry> result = new TreeSet<>();
        String groupsStr = getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        if (groupsStr != null && !groupsStr.isEmpty())
            for (String group : groupsStr.replace(" ", "").split(","))
                result.add(new GroupEntry(getServerZone(), group));
        return result;
    }

    // ------------------------------------------------------------
    // -- Group permissions
    // ------------------------------------------------------------

    /**
     * Get all group permissions as a map
     */
    public Map<String, PermissionList> getGroupPermissions()
    {
        return groupPermissions;
    }

    /**
     * Gets the group permissions for the specified group, or null if not present.
     * 
     * @param group Group
     */
    public PermissionList getGroupPermissions(String group)
    {
        return groupPermissions.get(group);
    }

    /**
     * Gets the group permissions for the specified group. If no permission-map is present, a new one is created.
     * 
     * @param group Group
     */
    public PermissionList getOrCreateGroupPermissions(String group)
    {
        PermissionList map = groupPermissions.get(group);
        if (map == null)
        {
            map = new PermissionList();
            groupPermissions.put(group, map);
        }
        return groupPermissions.get(group);
    }

    /**
     * Returns the value of a group permission, or null if empty.
     * 
     * @param group
     * @param permissionNode
     * @return permission value or null, if not set
     */
    public String getGroupPermission(String group, String permissionNode)
    {
        PermissionList map = getGroupPermissions(group);
        if (map != null)
        {
            return map.get(permissionNode);
        }
        return null;
    }

    /**
     * Checks, if a group permission is true, false or empty.
     * 
     * @param group
     * @param permissionNode
     * @return true / false or null, if not set
     */
    public Boolean checkGroupPermission(String group, String permissionNode)
    {
        PermissionList map = getGroupPermissions(group);
        if (map != null)
        {
            String permValue = map.get(permissionNode);
            return !PERMISSION_FALSE.equalsIgnoreCase(permValue);
        }
        return null;
    }

    /**
     * Set a group permission-property
     * 
     * @param group
     * @param permissionNode
     * @param value
     */
    public boolean setGroupPermissionProperty(String group, String permissionNode, String value)
    {
        if (group != null && !APIRegistry.getFEEventBus().post(new PermissionEvent.Group.ModifyPermission(getServerZone(), group, this, permissionNode, value)))
        {
            PermissionList map = getOrCreateGroupPermissions(group);
            if (value == null)
                map.remove(permissionNode);
            else
                map.put(permissionNode, value);
            setDirty();
            return true;
        }
        return false;
    }

    /**
     * Set a group permission
     * 
     * @param group
     * @param permissionNode
     * @param value
     */
    public boolean setGroupPermission(String group, String permissionNode, boolean value)
    {
        return setGroupPermissionProperty(group, permissionNode, value ? PERMISSION_TRUE : PERMISSION_FALSE);
    }

    /**
     * Clears a player permission
     * 
     * @param group
     * @param permissionNode
     */
    public boolean clearGroupPermission(String group, String permissionNode)
    {
        if (group != null)
        {
            PermissionList map = getGroupPermissions(group);
            if (map != null
                    && !APIRegistry.getFEEventBus().post(new PermissionEvent.Group.ModifyPermission(getServerZone(), group, this, permissionNode, null)))
            {
                map.remove(permissionNode);
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------
    // -- Other
    // ------------------------------------------------------------

    public void userIdentInvalidated(UserIdentInvalidatedEvent event)
    {
        PermissionList oldPerms = playerPermissions.remove(event.oldValue);
        if (oldPerms == null)
            return;
        setDirty();

        PermissionList newPerms = playerPermissions.get(event.newValue);
        if (newPerms == null)
            playerPermissions.put(event.newValue, oldPerms);
        else
            newPerms.putAll(oldPerms);
    }

    /**
     * Swaps the permissions of one zone with another one
     */
    public void swapPermissions(Zone zone)
    {
        Map<String, PermissionList> swapGroupPerms = zone.groupPermissions;
        zone.groupPermissions = groupPermissions;
        groupPermissions = swapGroupPerms;

        Map<UserIdent, PermissionList> swapPlayerPermissions = zone.playerPermissions;
        zone.playerPermissions = playerPermissions;
        playerPermissions = swapPlayerPermissions;
    }

    /**
     * List all permission nodes that have any kind of configuration in this zone
     */
    public Set<String> enumRegisteredPermissions()
    {
        Set<String> perms = new TreeSet<>();
        for (Entry<UserIdent, PermissionList> permList : playerPermissions.entrySet())
            for (String perm : permList.getValue().keySet())
            {
                if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                    perm = perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length());
                perms.add(perm);
            }
        for (Entry<String, PermissionList> permList : groupPermissions.entrySet())
            for (String perm : permList.getValue().keySet())
            {
                if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                    perm = perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length());
                perms.add(perm);
            }
        return perms;
    }

}