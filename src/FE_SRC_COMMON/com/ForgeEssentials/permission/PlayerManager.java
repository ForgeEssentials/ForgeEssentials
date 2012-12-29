package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ForgeEssentials.permission.query.PermQuery.PermResult;

public class PlayerManager
{
	protected static ConcurrentHashMap<String, ConcurrentHashMap<String, PlayerPermData>>	playerDats		= new ConcurrentHashMap<String, ConcurrentHashMap<String, PlayerPermData>>();

	protected static ConcurrentHashMap<String, Set<Permission>>				playerSupers	= new ConcurrentHashMap<String, Set<Permission>>();

	public ConfigPlayer													config;

	public PlayerManager()
	{
		config = new ConfigPlayer();
	}

	/**
	 * @param username of the player
	 * @return NULL if the player has nothing specified
	 */
	public static PlayerPermData getPlayerData(String zoneID, String username)
	{
		ConcurrentHashMap<String, PlayerPermData> map = playerDats.get(zoneID);
		if (map == null)
		{
			map = new ConcurrentHashMap<String, PlayerPermData>();
			playerDats.put(zoneID, map);
		}
		
		PlayerPermData data = map.get(username); 
		
		if (data == null)
		{
			data = new PlayerPermData(username, zoneID);
			putPlayerData(data);
		}
		
		return data;
	}

	public static void putPlayerData(PlayerPermData data)
	{
		ConcurrentHashMap<String, PlayerPermData> map = playerDats.get(data.zoneID);

		if (map == null)
		{
			map = new ConcurrentHashMap<String, PlayerPermData>();
			map.put(data.username, data);
			playerDats.put(data.zoneID, map);
		}
		else
			map.put(data.username, data);
	}

	/**
	 * Gets all the PlayerDatas that were explicitly created in the given zone. these datas will only apply
	 * to the given Zone and all of its children.
	 * @param zoneID zone to check.
	 * @return List of PlayerDatas. may be an empty list, but never null.
	 */
	public static ArrayList<PlayerPermData> getAllPlayerDatasInZone(String zoneID)
	{
		ArrayList<PlayerPermData> ps = new ArrayList<PlayerPermData>();
		for (PlayerPermData g : playerDats.get(zoneID).values())
			if (g.zoneID.equals(zoneID))
				ps.add(g);

		return ps;
	}

	/**
	 * @return UNKNOWN if the permission does not exist here.
	 */
	public static PermResult getSuperPermission(String username, String permission)
	{
		PermissionChecker checker = new PermissionChecker(permission);
		Set<Permission> perms = playerSupers.get(username);

		if (perms == null)
			return PermResult.UNKNOWN;

		Permission smallest = null;
		for (Permission perm : perms)
			if (checker.equals(perm))
				return perm.allowed;
			else if (checker.matches(perm))
				if (smallest == null)
					smallest = perm;
				else if (smallest.isChildOf(perm))
					smallest = perm;
		if (smallest != null)
			return smallest.allowed;
		else
			return PermResult.UNKNOWN;
	}

	public static void setSuperPermission(String username, String permission, boolean allowed)
	{
		PermissionChecker checker = new PermissionChecker(permission);
		Permission newPerm = new Permission(permission, allowed);

		Set<Permission> perms = playerSupers.get(username);
		
		if (perms == null)
		{
			perms = Collections.newSetFromMap(new ConcurrentHashMap<Permission, Boolean>());
			playerSupers.put(username, perms);
		}
		
		if (perms.contains(checker) && !perms.contains(newPerm))
			perms.remove(checker);

		perms.add(newPerm);
	}
}
