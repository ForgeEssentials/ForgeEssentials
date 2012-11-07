package com.ForgeEssentials.permissions;

import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.permissions.allowance.BaseAllowance;

/**
 * 
 * Determines whether the Allowance is allowed or denied for a certain target.
 * 
 * @author MysteriousAges
 *
 */
public class PermissionManager
{
	private BaseAllowance allowance;
	private boolean isPermitted;
	
	// Target applied to
	private PermissionTarget target;
	
	public PermissionManager(BaseAllowance allowObject, boolean permit, PermissionTarget playerGroup)
	{
		this.allowance = allowObject;
		this.isPermitted = permit;
		this.target = playerGroup;
	}
	
	/**
	 * Determines whether the allowance applies to a specific player or not.
	 * @param playerName The player's username
	 * @return True, if the permission applies to this username, false otherwise.
	 */
	public boolean doesPermissionApplyTo(String playerName)
	{
		boolean flag = false;
		if (this.target.isPlayerInTarget(playerName))
		{
			flag = true;
		}
		return flag;
	}
	
	/**
	 * Allows an allowance to determine whether or not it should deny the action.
	 * @param query 
	 */
	public void checkForPermission(PlayerInteractPermissionQuery query)
	{
		this.allowance.processPermission(query, this.isPermitted);
	}
	
	public boolean isAllowancePermitted()
	{
		return this.isPermitted;
	}
	
	public BaseAllowance getAllowance()
	{
		return this.allowance;
	}
	
	// TODO: save this somehow. 
	
}
