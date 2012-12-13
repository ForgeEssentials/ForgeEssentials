package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.ForgeEssentials.permission.query.PermQuery.PermResult;

public class PlayerManager
{
	protected static HashMap<String, HashMap<String, PlayerPermData>>	playerDats		= new HashMap<String, HashMap<String, PlayerPermData>>();

	protected static HashMap<String, HashSet<Permission>>				playerSupers	= new HashMap<String, HashSet<Permission>>();

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
		return playerDats.get(zoneID).get(username);
	}

	public static void putPlayerData(PlayerPermData data)
	{
		HashMap<String, PlayerPermData> map = playerDats.get(data.zoneID);

		if (map == null)
		{
			map = new HashMap<String, PlayerPermData>();
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
		HashSet<Permission> perms = playerSupers.get(username);

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

		HashSet<Permission> perms = playerSupers.get(username);
		if (perms.contains(checker) && !perms.contains(newPerm))
			perms.remove(checker);

		perms.add(newPerm);
	}
}
