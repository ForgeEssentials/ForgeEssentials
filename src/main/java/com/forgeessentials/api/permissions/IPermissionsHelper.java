package com.forgeessentials.api.permissions;

import com.forgeessentials.api.permissions.query.PermQuery;
import com.forgeessentials.api.permissions.query.PermQuery.PermResult;
import com.forgeessentials.api.permissions.query.PropQuery;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public interface IPermissionsHelper {

    /**
     * Check if a permissions is allowed
     *
     * @param query A PermQuery object
     * @return true if allowed, false if not.
     */

    boolean checkPermAllowed(PermQuery query);
    
    // moved to forge API 
    // boolean checkPermAllowed(EntityPlayer player, String node);

    PermResult checkPermResult(PermQuery query);

    /**
     * populates the given PropQuery with a value.
     */
    void getPermissionProp(PropQuery query);

    /**
     * Create a group within a zone
     *
     * @param groupName Name of the group
     * @param zoneName  Name of the zone the group is under
     * @param prefix    Chat prefix
     * @param suffix    Chat suffix
     * @param parent    Parent group
     * @param priority  Priority that the group should be checked in
     * @return Group created
     */
    Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority);

    /**
     * Set a permissions for a player
     *
     * @param username   The player's username
     * @param permission The permissions node name
     * @param allow      Is the permissions allowed or denied
     * @param zoneID     The zone in which the permissions takes effect
     * @return
     */
    String setPlayerPermission(UUID username, String permission, boolean allow, String zoneID);

    /**
     * Set a permissions for a group
     *
     * @param group   The group name
     * @param permission The permissions node name
     * @param allow      Is the permissions allowed or denied
     * @param zoneID     The zone in which the permissions takes effect
     * @return
     */
    String setGroupPermission(String group, String permission, boolean allow, String zoneID);

    /**
     * Set a permissions prop for a player
     *
     * @param username   The player's username
     * @param permission The permissions node name
     * @param value      Value of the permissions prop
     * @param zoneID     The zone in which the permissions takes effect
     * @return
     */
    String setPlayerPermissionProp(UUID username, String permission, String value, String zoneID);

    /**
     * Set a permissions for a player
     *
     * @param permission The permissions node name
     * @param value      Value of the permissions prop
     * @param zoneID     The zone in which the permissions takes effect
     * @return
     */
    String setGroupPermissionProp(String group, String permission, String value, String zoneID);

    ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults);

    ArrayList<Group> getApplicableGroups(UUID player, boolean includeDefaults, String zoneID);

    // moved to forge, use APIRegistry.getAsFEGroup if you need the group
    // Group getGroupForName(String name);

    Group getHighestGroup(EntityPlayer player);

    /**
     * These methods are zone aware - if you don't care about the zone, use the methods provided in the ForgePerms API
     */

    ArrayList<String> getPlayersInGroup(String group, String zone);

    String setPlayerGroup(String group, UUID player, String zone);

    String addPlayerToGroup(String group, UUID player, String zone);

    String clearPlayerGroup(String group, UUID player, String zone);

    String clearPlayerPermission(UUID player, String node, String zone);

    String clearPlayerPermissionProp(UUID player, String node, String zone);

    void deleteGroupInZone(String group, String zone);

    boolean updateGroup(Group group);

    String clearGroupPermission(String name, String node, String zone);

    String clearGroupPermissionProp(String name, String node, String zone);

    ArrayList<Group> getGroupsInZone(String zoneName);

    String getPermissionForGroup(String target, String zone, String perm);

    String getPermissionPropForGroup(String target, String zone, String perm);

    String getPermissionForPlayer(UUID target, String zone, String perm);

    String getPermissionPropForPlayer(UUID target, String zone, String perm);

    ArrayList getPlayerPermissions(UUID target, String zone);

    ArrayList getPlayerPermissionProps(UUID target, String zone);

    ArrayList getGroupPermissions(String target, String zone);

    ArrayList getGroupPermissionProps(String target, String zone);

    Group getDEFAULT();
}
