package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.IPermissionsProvider;

import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link IPermissionsHelper} is the primary access-point to the permissions-system.
 * 
 * @author Olee
 */
public interface IPermissionsHelper extends IPermissionsProvider {

	static final String DEFAULT_GROUP = "*";
	static final String OP_GROUP = "_OP_";
	static final String PERMISSION_ASTERIX = "*";
	static final String PERMISSION_FALSE = "false";
	static final String PERMISSION_TRUE = "true";

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player
	 * 
	 * @param player
	 * @param permissionNode
	 * @return
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

	/**
	 * Gets a permission-property for a player as integer
	 * 
	 * @param player
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	Integer getPermissionPropertyInt(EntityPlayer player, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player at a certain position
	 * 
	 * @param player
	 *            null or player
	 * @param targetPoint
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, WorldPoint targetPoint, String permissionNode);

	/**
	 * Gets a permission-property for a player at a certain position
	 * 
	 * @param playernull
	 *            or player
	 * @param targetPoint
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, WorldPoint targetPoint, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player in a certain area
	 * 
	 * @param player
	 *            null or player
	 * @param targetArea
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, WorldArea targetArea, String permissionNode);

	/**
	 * Gets a permission-property for a player in a certain area
	 * 
	 * @param playernull
	 *            or player
	 * @param targetArea
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, WorldArea targetArea, String permissionNode);

	// ---------------------------------------------------------------------------

	/**
	 * Checks a permission for a player in the specified zone
	 * 
	 * @param player
	 *            null or player
	 * @param zone
	 * @param permissionNode
	 * @return
	 */
	boolean checkPermission(EntityPlayer player, Zone zone, String permissionNode);

	/**
	 * Gets a permission-property for a player in the specified zone
	 * 
	 * @param playernull
	 *            or player
	 * @param zone
	 * @param permissionNode
	 * @return property, if it exists, null otherwise
	 */
	String getPermissionProperty(EntityPlayer player, Zone zone, String permissionNode);

	// ---------------------------------------------------------------------------
	
	/**
	 * Sets a player permission
	 * 
	 * @param uuid
	 * @param permissionNode
	 * @param value
	 */
	void setPlayerPermission(UserIdent ident, String permissionNode, boolean value);
	
	/**
	 * Sets a player permission-property
	 * 
	 * @param uuid
	 * @param permissionNode
	 * @param value
	 */
	void setPlayerPermissionProperty(UserIdent ident, String permissionNode, String value);
	
	/**
	 * Sets a group permission
	 * 
	 * @param group
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
	 * Returns the next free zone-id.
	 * NEVER call this method, unless you are really creating a new Zone!
	 * 
	 * @return
	 */
	//int getNextZoneID();

	/**
	 * Get all registered zones
	 * 
	 * @return
	 */
	Collection<Zone> getZones();
	
	/**
	 * Returns the root zone, which has lowest priority and holds the default permissions
	 * 
	 * @return Zone or null
	 */
	Zone getZoneById(int id);

	/**
	 * Returns the root zone, which has lowest priority and holds the default permissions. If id is not a valid integer, null is returned.
	 * 
	 * @return Zone or null
	 */
	Zone getZoneById(String id);

	/**
	 * Returns the root zone, which has lowest priority and holds the default permissions
	 * 
	 * @return
	 */
	//RootZone getRootZone();

	/**
	 * Returns the {@link ServerZone}
	 * 
	 * @return
	 */
	ServerZone getServerZone();

	/**
	 * Returns the {@link WorldZone} for the specified world
	 * 
	 * @param world
	 * @return
	 */
	WorldZone getWorldZone(World world);

	/**
	 * Returns the {@link WorldZone} for the specified world
	 * 
	 * @param world
	 * @return
	 */
	WorldZone getWorldZone(int dimensionId);
	
	// ---------------------------------------------------------------------------

	/**
	 * Get zones that cover the point. Result is ordered by priority.
	 * 
	 * @param worldPoint
	 * @return
	 */
	List<Zone> getZonesAt(WorldPoint worldPoint);

	/**
	 * Get area-zones that cover the point. Result is ordered by priority.
	 * 
	 * @param worldPoint
	 * @return
	 */
	List<AreaZone> getAreaZonesAt(WorldPoint worldPoint);

	/**
	 * Get zones with the highest priority, that covers the point.
	 * 
	 * @param worldPoint
	 * @return
	 */
	Zone getZoneAt(WorldPoint worldPoint);

	/**
	 * Get area-zone with the highest priority, that covers the point.
	 * 
	 * @param worldPoint
	 * @return
	 */
	Zone getAreaZoneAt(WorldPoint worldPoint);

	// ---------------------------------------------------------------------------

	/**
	 * Get a group by it's name
	 * 
	 * @param name
	 * @return
	 */
	Group getGroup(String name);

	/**
	 * Get all registered groups
	 * 
	 * @return
	 */
	Collection<Group> getGroups();
	
	/**
	 * Returns the highest-priority group the the player belongs to.
	 * 
	 * @param player
	 * @return
	 */
	Group getPrimaryGroup(EntityPlayer player);

	/**
	 * Get all groups the player belongs to, ordered by priority.
	 * 
	 * @param player
	 * @return
	 */
	Collection<Group> getPlayerGroups(EntityPlayer player);

	// ---------------------------------------------------------------------------

}
