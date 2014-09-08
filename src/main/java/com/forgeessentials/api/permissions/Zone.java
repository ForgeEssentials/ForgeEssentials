package com.forgeessentials.api.permissions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class Zone {

	public static final String DEFAULT_GROUP = "*";
	public static final String PERMISSION_ASTERIX = "*";
	public static final String PERMISSION_FALSE = "false";
	public static final String PERMISSION_TRUE = "true";

	private Map<String, Map<String, String>> playerPermissions = new HashMap<String, Map<String, String>>();

	private Map<String, Map<String, String>> groupPermissions = new HashMap<String, Map<String, String>>();

	public abstract boolean isPlayerInZone(EntityPlayer player);

	public abstract String getName();

	public String getPlayerPermission(String uuid, String permissionNode)
	{
		Map<String, String> map = getPlayerPermissions(uuid);
		if (map != null)
		{
			return map.get(permissionNode);
		}
		return null;
	}
	
	public String getPlayerPermission(EntityPlayer player, String permissionNode)
	{
		return getPlayerPermission(player.getPersistentID().toString(), permissionNode);
	}

	public String getGroupPermission(String group, String permissionNode)
	{
		Map<String, String> map = getGroupPermissions(group);
		if (map != null)
		{
			return map.get(permissionNode);
		}
		return null;
	}

	public Boolean checkPlayerPermission(String uuid, String permissionNode)
	{
		Map<String, String> map = getPlayerPermissions(uuid);
		if (map != null)
		{
			return !map.get(permissionNode).equals(Zone.PERMISSION_FALSE);
		}
		return null;
	}

	public Boolean checkGroupPermission(String group, String permissionNode)
	{
		Map<String, String> map = getGroupPermissions(group);
		if (map != null)
		{
			return !map.get(permissionNode).equals(Zone.PERMISSION_FALSE);
		}
		return null;
	}

	public Map<String, String> getPlayerPermissions(String uuid)
	{
		return playerPermissions.get(uuid);
	}

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

	public void setPlayerPermission(String uuid, String permissionNode, String value)
	{
		Map<String, String> map = getOrCreatePlayerPermissions(uuid);
		map.put(permissionNode, value);
	}

	public Map<String, String> getGroupPermissions(String group)
	{
		return groupPermissions.get(group);
	}

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

	public void setGroupPermission(String group, String permissionNode, String value)
	{
		Map<String, String> map = getOrCreateGroupPermissions(group);
		map.put(permissionNode, value);
	}

	public void setGroupPermission(String group, String permissionNode, boolean allow)
	{
		setGroupPermission(group, permissionNode, allow ? Zone.PERMISSION_TRUE : Zone.PERMISSION_FALSE);
	}


}