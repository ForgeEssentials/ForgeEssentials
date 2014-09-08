package com.forgeessentials.api.permissions;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class Zone {

	private int id;
	
	private Map<String, Map<String, String>> playerPermissions = new HashMap<String, Map<String, String>>();

	private Map<String, Map<String, String>> groupPermissions = new HashMap<String, Map<String, String>>();

	/**
	 * Gets the unique zone-ID
	 * 
	 * @return
	 */
	public int getId()
	{
		return id;
	}
	
	public Zone(int id) {
		this.id = id;
	}

	/**
	 * Checks, whether the player is in the zone.
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerInZone(EntityPlayer player) {
		return isPointInZone(new WorldPoint(player));
	}

	/**
	 * Checks, whether the player is in the zone.
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean isPointInZone(WorldPoint point);

	/**
	 * Checks, whether the area is entirely contained within the zone.
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean isAreaInZone(WorldArea point);

	/**
	 * Checks, whether a part of the area is in the zone.
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean isPartOfAreaInZone(WorldArea point);

	/**
	 * Returns the name of the zone
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Get the parent zone
	 * 
	 * @return
	 */
	public abstract Zone getParent();

	// ------------------------------------------------------------
	// -- Player permissions
	// ------------------------------------------------------------

	/**
	 * Gets the player permissions for the specified player, or null if not present.
	 * 
	 * @param uuid
	 * @return
	 */
	public Map<String, String> getPlayerPermissions(String uuid)
	{
		return playerPermissions.get(uuid);
	}

	/**
	 * Gets the player permissions for the specified player. If no permission-map is present, a new one is created.
	 * 
	 * @param uuid
	 * @return
	 */
	public Map<String, String> getOrCreatePlayerPermissions(String uuid)
	{
		Map<String, String> map = playerPermissions.get(uuid);
		if (map == null)
		{
			map = new HashMap<String, String>();
			playerPermissions.put(uuid, map);
		}
		return playerPermissions.get(uuid);
	}

	/**
	 * Returns the value of a player permission, or null if empty.
	 * 
	 * @param uuid
	 * @param permissionNode
	 * @return
	 */
	public String getPlayerPermission(String uuid, String permissionNode)
	{
		Map<String, String> map = getPlayerPermissions(uuid);
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
		return getPlayerPermission(player.getPersistentID().toString(), permissionNode);
	}

	/**
	 * Checks, if a player permission is true, false or empty.
	 * 
	 * @param uuid
	 * @param permissionNode
	 * @return true / false or null, if not set
	 */
	public Boolean checkPlayerPermission(String uuid, String permissionNode)
	{
		Map<String, String> map = getPlayerPermissions(uuid);
		if (map != null)
		{
			return !map.get(permissionNode).equals(IPermissionsHelper.PERMISSION_FALSE);
		}
		return null;
	}

	/**
	 * Set a player permission-property
	 * 
	 * @param uuid
	 * @param permissionNode
	 * @param value
	 */
	public void setPlayerPermissionProperty(String uuid, String permissionNode, String value)
	{
		if (uuid != null) {
			Map<String, String> map = getOrCreatePlayerPermissions(uuid);
			map.put(permissionNode, value);
		}
	}

	/**
	 * Set a player permission
	 * 
	 * @param group
	 * @param permissionNode
	 * @param value
	 */
	public void setPlayerPermission(String uuid, String permissionNode, boolean value)
	{
		setPlayerPermissionProperty(uuid, permissionNode, value ? IPermissionsHelper.PERMISSION_TRUE : IPermissionsHelper.PERMISSION_FALSE);
	}

	// ------------------------------------------------------------
	// -- Group permissions
	// ------------------------------------------------------------

	/**
	 * Gets the group permissions for the specified group, or null if not present.
	 * 
	 * @param group
	 * @return
	 */
	public Map<String, String> getGroupPermissions(String group)
	{
		return groupPermissions.get(group);
	}

	/**
	 * Gets the group permissions for the specified group. If no permission-map is present, a new one is created.
	 * 
	 * @param group
	 * @return
	 */
	public Map<String, String> getOrCreateGroupPermissions(String group)
	{
		Map<String, String> map = groupPermissions.get(group);
		if (map == null)
		{
			map = new HashMap<String, String>();
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
		Map<String, String> map = getGroupPermissions(group);
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
		Map<String, String> map = getGroupPermissions(group);
		if (map != null)
		{
			return !map.get(permissionNode).equals(IPermissionsHelper.PERMISSION_FALSE);
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
	public void setGroupPermissionProperty(String group, String permissionNode, String value)
	{
		if (group != null) {
			Map<String, String> map = getOrCreateGroupPermissions(group);
			map.put(permissionNode, value);
		}
	}

	/**
	 * Set a group permission
	 * 
	 * @param group
	 * @param permissionNode
	 * @param value
	 */
	public void setGroupPermission(String group, String permissionNode, boolean value)
	{
		setGroupPermissionProperty(group, permissionNode, value ? IPermissionsHelper.PERMISSION_TRUE : IPermissionsHelper.PERMISSION_FALSE);
	}


}