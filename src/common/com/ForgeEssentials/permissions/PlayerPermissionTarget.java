package com.ForgeEssentials.permissions;

/**
 * Describes a specific player as the target for a permission.
 * 
 * @author MysteriousAges
 * 
 */
public class PlayerPermissionTarget extends PermissionTarget
{
	private String playerName;

	public PlayerPermissionTarget(String name)
	{
		this.playerName = name;
	}

	@Override
	public boolean isPlayerInTarget(String username)
	{
		return this.playerName.equals(username);
	}
}
