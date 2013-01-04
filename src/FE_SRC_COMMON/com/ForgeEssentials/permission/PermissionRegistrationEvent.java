package com.ForgeEssentials.permission;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.event.Event;

public class PermissionRegistrationEvent extends Event
{
	/**
	 * Parents are not automatically registered
	 * @param permName. Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow. True if the permission is allowed by default
	 */
	@Deprecated
	public void registerPermissionDefault(String permName, boolean allow)
	{
	}

	/**
	 * This is to define the level the permission should be used for by defualt..
	 * see @see com.ForgeEssentials.permissions.PermissionsAPI for the default groups
	 * If you want.. you can also set specific group permissions with this.. though they may or may not exist...
	 * @param username player to apply the permission to.
	 * @param permission Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow
	 */
	public void registerGlobalGroupPermissions(String group, String permission, boolean allow)
	{
		if (!group.equals(PermissionsAPI.GROUP_OWNERS) &&
				!group.equals(PermissionsAPI.GROUP_ZONE_ADMINS) &&
				!group.equals(PermissionsAPI.GROUP_GUESTS) &&
				!group.equals(PermissionsAPI.GROUP_MEMBERS)
				)
			throw new IllegalArgumentException("You can't register a permission for \""+group+"\"! use the PermissionsAPI!");

//		Permission perm = new Permission(permission, allow);
//		Set<Permission> perms = ZoneManager.GLOBAL.groupOverrides.get(group);
//
//		if (perms == null)
//		{
//			perms = Collections.newSetFromMap(new ConcurrentHashMap<Permission, Boolean>());
//			perms.add(perm);
//			ZoneManager.GLOBAL.groupOverrides.put(group, perms);
//		}
//		else
//		{
//			PermissionChecker checker = new PermissionChecker(permission);
//			if (perms.contains(checker))
//				perms.remove(checker);
//			perms.add(perm);
//		}
		
		// store defaults... for later...
	}
}
