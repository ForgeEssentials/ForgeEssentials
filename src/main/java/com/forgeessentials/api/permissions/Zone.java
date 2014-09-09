package com.forgeessentials.api.permissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Zones are used to store permissions in a tree-like hierarchy. Each zone has it's own set of group- and player-permissions. Zones are stored in a tree
 * structure with fixed levels. Priorities for permissions are based on the level of each zone in the tree. The following list shows the structure of the tree:
 * 
 * <pre>
 * {@link RootZone} &gt; {@link ServerZone} &gt; {@link WorldZone} &gt; {@link AreaZone}
 * </pre>
 * 
 * 
 * @author Bjoern Zeutzheim
 */
public abstract class Zone {

	private int id;

	private Map<UserIdent, Map<String, String>> playerPermissions = new HashMap<UserIdent, Map<String, String>>();

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

	public Zone(int id)
	{
		this.id = id;
	}

	/**
	 * Checks, whether the player is in the zone.
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerInZone(EntityPlayer player)
	{
		return isInZone(new WorldPoint(player));
	}

	/**
	 * Checks, whether the player is in the zone.
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean isInZone(WorldPoint point);

	/**
	 * Checks, whether the area is entirely contained within the zone.
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean isInZone(WorldArea point);

	/**
	 * Checks, whether a part of the area is in the zone.
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean isPartOfZone(WorldArea point);

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
	 * Get all groups that have permissions in this zone
	 * 
	 * @return
	 */
	public Collection<Map<String, String>> getPlayers()
	{
		return playerPermissions.values();
	}

	/**
	 * Gets the player permissions for the specified player, or null if not present.
	 * 
	 * @param ident
	 * @return
	 */
	public Map<String, String> getPlayerPermissions(UserIdent ident)
	{
		return playerPermissions.get(ident);
	}

	/**
	 * Gets the player permissions for the specified player. If no permission-map is present, a new one is created.
	 * 
	 * @param ident
	 * @return
	 */
	public Map<String, String> getOrCreatePlayerPermissions(UserIdent ident)
	{
		Map<String, String> map = playerPermissions.get(ident);
		if (map == null)
		{
			map = new HashMap<String, String>();
			playerPermissions.put(ident, map);
		}
		return playerPermissions.get(ident);
	}

	/**
	 * Returns the value of a player permission, or null if empty.
	 * 
	 * @param ident
	 * @param permissionNode
	 * @return
	 */
	public String getPlayerPermission(UserIdent ident, String permissionNode)
	{
		Map<String, String> map = getPlayerPermissions(ident);
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
		return getPlayerPermission(new UserIdent(player), permissionNode);
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
		Map<String, String> map = getPlayerPermissions(ident);
		if (map != null)
		{
			return !map.get(permissionNode).equals(IPermissionsHelper.PERMISSION_FALSE);
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
	public void setPlayerPermissionProperty(UserIdent ident, String permissionNode, String value)
	{
		if (ident != null)
		{
			Map<String, String> map = getOrCreatePlayerPermissions(ident);
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
	public void setPlayerPermission(UserIdent ident, String permissionNode, boolean value)
	{
		setPlayerPermissionProperty(ident, permissionNode, value ? IPermissionsHelper.PERMISSION_TRUE : IPermissionsHelper.PERMISSION_FALSE);
	}

	/**
	 * Revalidates all UserIdent fields in playerPermissions to replace those which were hashed based on their playername. This function should always be called
	 * as soon as a player connects to the server.
	 */
	public void updatePlayerIdents()
	{
		// TODO: TEST updatePlayerIdents !!!
		// To do so add a permission by playername of user who is not connected
		// When he joins an event needs to be fired that triggers this function
		// It should update the map entry then
		for (Iterator iterator = playerPermissions.entrySet().iterator(); iterator.hasNext();)
		{
			Map.Entry<UserIdent, Map<String, String>> entry = (Map.Entry<UserIdent, Map<String, String>>) iterator.next();
			if (!entry.getKey().wasValidUUID())
			{
				if (entry.getKey().isValidUUID())
				{
					iterator.remove();
					playerPermissions.put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	// ------------------------------------------------------------
	// -- Group permissions
	// ------------------------------------------------------------

	/**
	 * Get all groups that have permissions in this zone
	 * 
	 * @return
	 */
	public Collection<Map<String, String>> getGroups()
	{
		return groupPermissions.values();
	}

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
		if (group != null)
		{
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