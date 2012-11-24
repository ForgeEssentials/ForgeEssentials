package com.ForgeEssentials.permissions;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.Result;

public class ForgeEssentialsPermissionRegistrationEvent extends Event
{
	
	//public void registerPermissionDefault()
	// TODO: make commands to register stuff for groups.
	
	/**
	 * Parent permissions need not be registered.
	 * @param permName. Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow. True if the permission is allowed by default
	 */
	public void registerGlobalPermission(String permName, boolean allow)
	{
		Permission perm = new Permission(permName, allow);
		Permission.addDefaultPermission(perm);
	}
}
