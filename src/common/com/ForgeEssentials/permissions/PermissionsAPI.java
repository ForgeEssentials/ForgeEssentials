package com.ForgeEssentials.permissions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;


public class PermissionsAPI
{
	/**
	 * Used for blankets permissions tied to no particular layer or group in a zone.
	 * This is the also the group all players are assigned to if they are members of no other groups.
	 * This includes new players when they first log in.
	 * The players in this group are usually denied commands and breaking blocks before they are promoted to members.
	 * This group is guaranteed existence
	 */
	public static final String GROUP_DEFAULT = "_DEFAULT_";
	
	/**
	 * This is automatically assigned to the server owner when they make a world available to the LAN.
	 * This is also best kep for layers that have direct access to the server console.
	 * **CAUTION! MAY OR MAYNOT EXIST**
	 */
	public static final String GROUP_OWNERS = "Owners";
	
	/**
	 * This is usually for players that are actually members of the server.
	 * They will most likely be able to use basic commands as well as break blocks and stuff in the world.
	 * **CAUTION MAY OR MAY NOT EXIST**
	 */
	public static final String GROUP_MEMBERS = "Members";
	
	/**
	 * Use this to check AllOrNothing Area queries, Player Queries, or Point Queries.
	 * @param query
	 * @return TRUE if the permission is allowed. FALSE if the permission is denied or partially allowed.
	 */
	public static boolean checkPermAllowed(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult().equals(Result.ALLOW);
	}

	/**
	 * Use this with Area Queries, so you can know if the Permission is partially allowed.
	 * @param query
	 * @return the Result of the query
	 */
	public static Result checkPermResult(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult();
	}
}
