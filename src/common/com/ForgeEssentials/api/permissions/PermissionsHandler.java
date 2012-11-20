package com.ForgeEssentials.api.permissions;

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
		
		Zone zone = ZoneManager.getWhichZoneIn(event.getDoerPoint(), event.doer.worldObj);
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
			
			if (result.equals(Result.DEFAULT)) // or group blankets
				result = tempZone.getGroupOverride(group, perm);
			
			if (result.equals(Result.DEFAULT))
			{
				if (tempZone == ZoneManager.GLOBAL)
					result = Permission.getPermissionDefault(perm.name);
				else
					tempZone = tempZone.parent;
			}
		}
		return result;
	}
	
	public static boolean checkPermAllowed(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult().equals(Result.ALLOW);
	}
	
	public static Result checkPermResult(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult();
	}
	
	/**
	 * Parent permissions need not be registerred.
	 * @param permName Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow True if the permission is allowed by default
	 */
	public static void registerPermission(String permName, boolean allow)
	{
		Permission perm = new Permission(permName, allow);
		Permission.addDefaultPermission(perm);
	}
	
	/**
	 * This does NOT automatically register parents.
	 * @param perm Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 */
	public static void registerPermission(Permission perm)
	{
		assert !perm.allowed.equals(Result.DEFAULT) : new IllegalArgumentException("You cannot register a permission with a default value of DEFAULT!");
		Permission.addDefaultPermission(perm);
	}
}
