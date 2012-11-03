package com.ForgeEssentials.permissions;

/**
 * Describes the "target" of a permission - either a group or player.
 * 
 * @author MysteriousAges
 *
 */
public abstract class PermissionTarget
{
	/**
	 * 
	 * @param playerName The name of the player
	 * @return True, if the player is targeted.
	 */
	public abstract boolean isPlayerInTarget(String playerName);
}
