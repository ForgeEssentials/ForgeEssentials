package com.ForgeEssentials.permission;

/**
 * RegistrationGroups
 * @author AbrarSyed
 *
 */
public enum RegGroup
{
	/**
	 * This is automatically assigned to the server owner when they make a world available to the LAN.
	 * This is also best kept for players that have direct access to the server's console and filesystem.
	 */
	OWNERS("Owners"),
	
	/**
	 * This is usually for players that are admins or owners of a given zone
	 * They will most likely have WorldEdit access, as well as the power to edit permissions in the zone.
	 */
	ZONE_ADMINS("ZoneAdmins"),
	
	/**
	 * This is usually for players that are actually members of the server.
	 * They will most likely be able to use basic commands as well as break blocks and stuff in the world.
	 */
	MEMBERS("Members"),
	
	/**
	 * This is the group, b default, that all players are assigned to when they first log in.
	 * The players in this group are usually denied commands and breaking blocks before they are promoted to members.
	 */
	GUESTS("Guest"),
	
	/**
	 * Used for blankets permissions tied to no particular player or group in a zone.
	 * All players are part of this group 
	 * This group is guaranteed existence
	 */
	ZONE("_DEFAULT_");
	
	
	
	private RegGroup(String name)
	{
		this.name= name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	private String name;
}
