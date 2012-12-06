package com.ForgeEssentials.permission;

import java.util.HashSet;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.Result;

public class ForgeEssentialsPermissionRegistrationEvent extends Event
{
	/**
	 * Parents are not automatically registered
	 * @param permName. Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow. True if the permission is allowed by default
	 */
	public void registerPermissionDefault(String permName, boolean allow)
	{
		Permission perm = new Permission(permName, allow);
		Permission.addDefaultPermission(perm);
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
		if (!GroupManager.groups.containsKey(group))
			return;
		
		Permission perm = new Permission(permission, allow);
		HashSet<Permission> perms = ZoneManager.GLOBAL.groupOverrides.get(group);
		
		if (perms == null)
		{
			perms = new HashSet<Permission>();
			perms.add(perm);
			ZoneManager.GLOBAL.groupOverrides.put(group, perms);
		}
		else
		{
			PermissionChecker checker = new PermissionChecker(permission);
			if (perms.contains(checker))
				perms.remove(checker);
			perms.add(perm);
		}
	}
}
