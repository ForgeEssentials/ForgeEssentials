package com.forgeessentials.api.permissions;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.permissions.IPermissionsProvider;

import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link IPermissionsHelper} is the primary access-point to the permissions-system.
 * 
 * @author Olee
 */
public interface IPermissionsHelper extends IPermissionsProvider {

	static final String GROUP_DEFAULT = "_ALL_";
	static final String GROUP_GUESTS = "_GUESTS_";
	static final String GROUP_OPERATORS = "_OPS_";

	static final String PERMISSION_ASTERIX = "*";
	static final String PERMISSION_FALSE = "false";
	static final String PERMISSION_TRUE = "true";

	// ---------------------------------------------------------------------------
	// -- Persistence
	// ---------------------------------------------------------------------------

	/**
	 * Marks the permission storage as dirty, so it will be persisted as soon as possible.
	 */
	void setDirty();

	// ---------------------------------------------------------------------------
	// -- Permissions
	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player
	 * 
	 * @param player
	 * @param permissionNode
	 */
	boolean checkPermission(EntityPlayer player, String permissionNode);

	/**
	 * Gets a permission-property for a player
	 * 
	 * @param player
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player
	 * 
	 * @param ident
	 * @param permissionNode
	 */
	boolean checkPermission(UserIdent ident, String permissionNode);

	/**
	 * Gets a permission-property for a player
	 * 
	 * @param ident
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(UserIdent ident, String permissionNode);

	/**
	 * Gets a permission-property for a player as integer
	 * 
	 * @param ident
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	Integer getPermissionPropertyInt(UserIdent ident, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player at a certain position
	 * 
	 * @param ident
	 * @param targetPoint
	 * @param permissionNode
	 */
	boolean checkPermission(UserIdent ident, WorldPoint targetPoint, String permissionNode);

	/**
	 * Gets a permission-property for a player at a certain position
	 * 
	 * @param ident
	 * @param targetPoint
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(UserIdent ident, WorldPoint targetPoint, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player in a certain area
	 * 
	 * @param ident
	 * @param targetArea
	 * @param permissionNode
	 */
	boolean checkPermission(UserIdent ident, WorldArea targetArea, String permissionNode);

	/**
	 * Gets a permission-property for a player in a certain area
	 * 
	 * @param ident
	 * @param targetArea
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(UserIdent ident, WorldArea targetArea, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player in the specified zone
	 * 
	 * @param ident
	 * @param zone
	 * @param permissionNode
	 */
	boolean checkPermission(UserIdent ident, Zone zone, String permissionNode);

	/**
	 * Gets a permission-property for a player in the specified zone
	 * 
	 * @param ident
	 * @param zone
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(UserIdent ident, Zone zone, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Sets a player permission
	 * 
	 * @param ident
	 * @param permissionNode
	 * @param value
	 */
	void setPlayerPermission(UserIdent ident, String permissionNode, boolean value);

	/**
	 * Sets a player permission-property
	 * 
	 * @param ident
	 * @param permissionNode
	 * @param value
	 */
	void setPlayerPermissionProperty(UserIdent ident, String permissionNode, String value);

	/**
	 * Sets a group permission
	 * 
	 * @param group Group name
	 * @param permissionNode
	 * @param value
	 */
	void setGroupPermission(String group, String permissionNode, boolean value);

	/**
	 * Sets a group permission-property
	 * 
	 * @param group
	 * @param permissionNode
	 * @param value
	 */
	void setGroupPermissionProperty(String group, String permissionNode, String value);

	/**
	 * Registers a permission property
	 * 
	 * @param permissionNode
	 * @param defaultValue
	 */
	void registerPermissionProperty(String permissionNode, String defaultValue);

	// ---------------------------------------------------------------------------

	/**
	 * Get all registered zones
	 */
	Collection<Zone> getZones();

	/**
	 * Returns a zone by it's ID
	 * 
	 * @return Zone or null
	 */
	Zone getZoneById(int id);

	/**
	 * Returns a zone by it's ID as string. It the string is no valid integer, it returns null.
	 * 
	 * @return Zone or null
	 */
	Zone getZoneById(String id);

	/**
	 * Returns the {@link ServerZone}
	 */
	ServerZone getServerZone();

	/**
	 * Returns the {@link WorldZone} for the specified world
	 * 
	 * @param world
	 */
	WorldZone getWorldZone(World world);

	/**
	 * Returns the {@link WorldZone} for the specified world
	 * 
	 * @param dimensionId
	 */
	WorldZone getWorldZone(int dimensionId);

	// ---------------------------------------------------------------------------

	/**
	 * Get zones that cover the point. Result is ordered by priority.
	 * 
	 * @param worldPoint
	 */
	List<Zone> getZonesAt(WorldPoint worldPoint);

	/**
	 * Get area-zones that cover the point. Result is ordered by priority.
	 * 
	 * @param worldPoint
	 */
	List<AreaZone> getAreaZonesAt(WorldPoint worldPoint);

	/**
	 * Get zones with the highest priority, that covers the point.
	 * 
	 * @param worldPoint
	 */
	Zone getZoneAt(WorldPoint worldPoint);

	/**
	 * Get area-zone with the highest priority, that covers the point.
	 * 
	 * @param worldPoint
	 */
	Zone getAreaZoneAt(WorldPoint worldPoint);

	// ---------------------------------------------------------------------------

	/**
	 * Checks, if a group exists
	 * 
	 * @param groupName
	 * @return true, if the group exists
	 */
	boolean groupExists(String groupName);
	
	/**
	 * Create a group
	 * 
	 * @param groupName
	 */
	void createGroup(String groupName);

	/**
	 * Add a player to a group
	 * 
	 * @param ident
	 * @param group
	 */
	void addPlayerToGroup(UserIdent ident, String group);

	/**
	 * Remove a player from a group
	 * 
	 * @param ident
	 * @param group
	 */
	void removePlayerFromGroup(UserIdent ident, String group);

	/**
	 * Returns the highest-priority group the the player belongs to.
	 * 
	 * @param ident
	 */
	String getPrimaryGroup(UserIdent ident);

	/**
	 * Get all groups the player belongs to, ordered by priority.
	 * 
	 * @param ident
	 */
	SortedSet<String> getPlayerGroups(UserIdent ident);

	// ---------------------------------------------------------------------------

}
