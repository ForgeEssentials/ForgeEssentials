package com.forgeessentials.api.permissions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * Zones are used to store permissions in a tree-like hierarchy. Each zone has it's own set of group- and player-permissions. Zones are stored in a tree
 * structure with fixed levels. Priorities for permissions are based on the level of each zone in the tree. The following list shows the structure of the tree:
 * 
 * <pre>
 * {@link RootZone} &gt; {@link ServerZone} &gt; {@link WorldZone} &gt; {@link AreaZone}
 * </pre>
 * 
 * 
 * @author Olee
 */
public abstract class Zone {

	/**
	 * {@link PermissionList} class is used to allow data API retrieving generics attributes.
	 */
	public class PermissionList extends HashMap<String, String> {
	}

	private int id;

	private Map<UserIdent, PermissionList> playerPermissions = new HashMap<UserIdent, PermissionList>();

	private Map<String, PermissionList> groupPermissions = new HashMap<String, PermissionList>();

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

	public abstract ServerZone getServerZone();
	
	// ------------------------------------------------------------
	// -- Player permissions
	// ------------------------------------------------------------

	/**
	 * Get all groups that have permissions in this zone
	 * 
	 * @return
	 */
	public Map<UserIdent, PermissionList> getPlayerPermissions()
	{
		return playerPermissions;
	}

	/**
	 * Gets the player permissions for the specified player, or null if not present.
	 * 
	 * @param ident
	 * @return
	 */
	public PermissionList getPlayerPermissions(UserIdent ident)
	{
		return playerPermissions.get(ident);
	}

	/**
	 * Gets the player permissions for the specified player. If no permission-map is present, a new one is created.
	 * 
	 * @param ident
	 * @return
	 */
	public PermissionList getOrCreatePlayerPermissions(UserIdent ident)
	{
		PermissionList map = playerPermissions.get(ident);
		if (map == null)
		{
			map = new PermissionList();
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
		PermissionList map = getPlayerPermissions(ident);
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
			getServerZone().registerPlayer(ident);
			PermissionList map = getOrCreatePlayerPermissions(ident);
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
	 * Clears a player permission
	 * 
	 * @param group
	 * @param permissionNode
	 */
	public void clearPlayerPermission(UserIdent ident, String permissionNode)
	{
		if (ident != null)
		{
			PermissionList map = getPlayerPermissions(ident);
			if (map != null)
			{
				map.remove(permissionNode);
			}
		}
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
			Map.Entry<UserIdent, PermissionList> entry = (Map.Entry<UserIdent, PermissionList>) iterator.next();
			if (!entry.getKey().wasValidUUID())
			{
				if (entry.getKey().hasUUID())
				{
					iterator.remove();
					playerPermissions.put(entry.getKey(), entry.getValue());
				}
			}
			else
			{
				entry.getKey().updateUsername();
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
	public Map<String, PermissionList> getGroupPermissions()
	{
		return groupPermissions;
	}

	/**
	 * Gets the group permissions for the specified group, or null if not present.
	 * 
	 * @param group
	 * @return
	 */
	public PermissionList getGroupPermissions(String group)
	{
		return groupPermissions.get(group);
	}

	/**
	 * Gets the group permissions for the specified group. If no permission-map is present, a new one is created.
	 * 
	 * @param group
	 * @return
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
			PermissionList map = getOrCreateGroupPermissions(group);
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

	/**
	 * Clears a player permission
	 * 
	 * @param group
	 * @param permissionNode
	 */
	public void clearGroupPermission(String group, String permissionNode)
	{
		if (group != null)
		{
			PermissionList map = getGroupPermissions(group);
			if (map != null)
			{
				map.remove(permissionNode);
			}
		}
	}

}