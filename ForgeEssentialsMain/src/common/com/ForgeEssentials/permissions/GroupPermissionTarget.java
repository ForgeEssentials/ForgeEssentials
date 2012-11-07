package com.ForgeEssentials.permissions;

import java.util.ArrayList;

/**
 * Describes a group of players.
 * @author MysteriousAges
 *
 */
public class GroupPermissionTarget extends PermissionTarget
{
	private ArrayList<String> groupUsers;
	
	public GroupPermissionTarget(String[] users)
	{
		this.groupUsers = new ArrayList<String>(users.length);
		for (int i = 0; i < users.length; ++i)
		{
			this.groupUsers.add(users[i]);
		}
	}
	
	@Override
	public boolean isPlayerInTarget(String playerName)
	{
		return this.groupUsers.contains(playerName);
	}

	public void addUsername(String name)
	{
		this.groupUsers.add(name);
	}
	
	public void removeUsername(String name)
	{
		this.groupUsers.remove(name);
	}
}
