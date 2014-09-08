package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.IPermissionsProvider;

import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.WorldPoint;

public interface IPermissionsHelper extends IPermissionsProvider {

	static final String DEFAULT_GROUP = "*";
	static final String PERMISSION_ASTERIX = "*";
	static final String PERMISSION_FALSE = "false";
	static final String PERMISSION_TRUE = "true";
	
	/**
	 * Checks a permission for a player
	 * @param player
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, String permissionNode);

	/**
	 * Gets a permission-property for a player
	 * @param player
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, String permissionNode);

	/**
	 * Gets a permission-property for a player as integer
	 * @param player
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	Integer getPermissionPropertyInt(EntityPlayer player, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player at a certain position
	 * @param player null or player
	 * @param targetPoint
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, WorldPoint targetPoint, String permissionNode);

	/**
	 * Gets a permission-property for a player at a certain position
	 * @param playernull or player
	 * @param targetPoint
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, WorldPoint targetPoint, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player in a certain area
	 * @param player null or player
	 * @param targetArea
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, AreaBase targetArea, String permissionNode);

	/**
	 * Gets a permission-property for a player in a certain area
	 * @param playernull or player
	 * @param targetArea
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, AreaBase targetArea, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player in the specified zone
	 * @param player null or player
	 * @param zone
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, Zone zone, String permissionNode);

	/**
	 * Gets a permission-property for a player in the specified zone
	 * @param playernull or player
	 * @param zone
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, Zone zone, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Get zones that cover the point. Result is ordered by priority.
	 * @param worldPoint
	 * @return
	 */
	List<Zone> getZonesAt(WorldPoint worldPoint);

	/**
	 * Get area-zones that cover the point. Result is ordered by priority.
	 * @param worldPoint
	 * @return
	 */
	List<AreaZone> getAreaZonesAt(WorldPoint worldPoint);
	
	// ---------------------------------------------------------------------------

	/**
	 * Registers a permission property
	 * @param permissionNode
	 * @param value
	 */
	void registerPermissionProperty(String permissionNode, String defaultValue);
	
	// ---------------------------------------------------------------------------

	/**
	 * Returns the global zone
	 * @return
	 */
	GlobalZone getGlobalZone();

	/**
	 * Returns the world-zone for the specified world
	 * @param world
	 * @return
	 */
	WorldZone getWorldZone(World world);

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------
	
    /**
     * Create a group within a zone
     *
     * @param groupName Name of the group
     * @param prefix    Chat prefix
     * @param suffix    Chat suffix
     * @param parent    Parent group
     * @param priority  Priority that the group should be checked in
     * @return Group created
     */
    Group createGroup(String groupName, String prefix, String suffix, String parent, int priority);

    /**
     * Set a permissions for a group
     *
     * @param group   The group name
     * @param permission The permissions node name
     * @param allow      Is the permissions allowed or denied
     * @param zoneID     The zone in which the permissions takes effect
     * @return null on success, error message otherwise
     */
    String setGroupPermission(String group, String permission, boolean allow, String zoneID);

    /**
     * Set a permissions prop for a player
     *
     * @param username   The player's username
     * @param permission The permissions node name
     * @param value      Value of the permissions prop
     * @param zoneID     The zone in which the permissions takes effect
     * @return null on success, error message otherwise
     */
    String setPlayerPermissionProp(UUID username, String permission, String value, String zoneID);

    /**
     * Set a permissions for a player
     *
     * @param permission The permissions node name
     * @param value      Value of the permissions prop
     * @param zoneID     The zone in which the permissions takes effect
     * @return null on success, error message otherwise
     */
    String setGroupPermissionProp(String group, String permission, String value, String zoneID);

    ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults);

    ArrayList<Group> getApplicableGroups(UUID player, boolean includeDefaults, String zoneID);

    // moved to forge, use APIRegistry.perms.getGroupForName if you need the group
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

    Group getDefaultGroup();

    String getEPPrefix();

    void setEPPrefix(String ePPrefix);

    String getEPSuffix();

    void setEPSuffix(String ePSuffix);

    UUID getEntryPlayer();

}
