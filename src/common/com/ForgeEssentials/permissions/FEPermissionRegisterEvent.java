package com.ForgeEssentials.permissions;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;

import net.minecraftforge.event.Event.Result;

public class FEPermissionRegisterEvent
{
	private boolean isRegisterring;
	
	public FEPermissionRegisterEvent()
	{
		isRegisterring = true;
	}
	
	/**
	 * NOBODY TOUCH THIS
	 * this used to stop permission registrations.
	 * After this is called, the permissions will be saved to the Permissions file. 
	 */
	public void endPermissionRegistration(IFEModule authorization)
	{
		if (authorization == ForgeEssentials.instance.mdlaunch.permission)
			isRegisterring = false;
		else
			throw new IllegalArgumentException("Illegal stoppage of permissions registrations process");
	}
	
	//public void registerPermissionDefault()
	// TODO: make commands to register stuff for groups.
	
	/**
	 * Parent permissions need not be registered.
	 * @param permName. Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow. True if the permission is allowed by default
	 */
	public void registerGlobalPermission(String permName, boolean allow)
	{
		if (!isRegisterring)
			throw new RuntimeException("Its too late to register permissions");
		
		Permission perm = new Permission(permName, allow);
		Permission.addDefaultPermission(perm);
	}
	
	public boolean isRegisterring()
	{
		return isRegisterring();
	}
}
