package com.ForgeEssentials.permissions.allowance;

import com.ForgeEssentials.permissions.PlayerInteractPermissionQuery;

/**
 * Minimum requirements for building an allowance for ForgeEssentials.
 * @author MysteriousAges
 *
 */
public abstract class BaseAllowance
{
	/**
	 * Processes the Permission Query to determine if the allowance applies; if so, the
	 *  derrived allowance can cancel or deny the event as appropriate
	 * @param query The Event that can be cancelled
	 * @param allowanceGranted Whether the permission is granting or denying an allowance.
	 */
	public abstract void processPermission(PlayerInteractPermissionQuery query, boolean allowanceGranted);
}
