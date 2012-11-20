package com.ForgeEssentials.permissions;

import com.ForgeEssentials.core.PlayerInfo;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * 
 * This is the default catcher of all the ForgeEssentials Permission checks.
 * Mods can inherit from any of the ForgeEssentials Permissions and specify more specific
 * catchers to get first crack at handling them.
 * 
 * The handling performed here is limited to basic area permission checks, and is not aware
 * of anything else other mods add to the system.
 * 
 * @author AbrarSyed
 *
 */
public final class PermissionsHandler
{	
	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void handlerQuery(PermQueryPlayer event)
	{
		//actually handle stuff here.
		
		// if we have a permission area that contains this point:
		//		if we can not perform this action in this area:
		//			-> Deny the action
		
		Zone zone = null; //Zone.getWhichZoneIn(event.getDoerPoint(), event.doer.worldObj);
		Result result = getResultFromZone(zone, event.permission, event.doer);
		event.setResult(result);
	}
	
	private Result getResultFromZone(Zone zone, PermissionChecker perm, EntityPlayer player)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		Result result = Result.DEFAULT;
		Zone tempZone = zone;
		while (result.equals(Result.DEFAULT))
		{
			String group = info.getGroupForZone(tempZone);
			result = tempZone.getPlayerOverride(player, perm);
			
			if (result.equals(Result.DEFAULT))
				result = tempZone.getGroupOverride(group, perm);
			
			if (result.equals(Result.DEFAULT))
			{
				if (tempZone == ZoneManager.GLOBAL)
					result = Permission.getPermissionDefault(perm.name);
				else
					tempZone = tempZone;//.getParentZone();
			}
		}
		return result;
	}
	
	public static boolean checkPermAllowed(PermQueryPlayer permQueryPlayer)
	{
		MinecraftForge.EVENT_BUS.post(permQueryPlayer);
		return permQueryPlayer.getResult().equals(Result.ALLOW);
	}
	
	public static Result checkPermResult(PermQueryArea query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult();
	}
	
	/**
	 * This does NOT automatically register parents.
	 * @param permName Permission to be added
	 * @param allow True if the permission is allowed by default
	 */
	public static void registerPermission(String permName, boolean allow)
	{
		Permission perm = new Permission(permName, allow);
		Permission.addDefaultPermission(perm);
	}
	
	/**
	 * This does NOT automatically register parents.
	 * @param perm Permission to be added
	 */
	public static void registerPermission(Permission perm)
	{
		assert !perm.allowed.equals(Result.DEFAULT) : new IllegalArgumentException("You cannot register a permission with a default value of DEFAULT!");
		Permission.addDefaultPermission(perm);
	}
}
