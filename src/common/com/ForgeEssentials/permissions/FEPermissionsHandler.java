package com.ForgeEssentials.permissions;

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
public final class FEPermissionsHandler
{	
	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void handlerQuery(FEPermissionsQuery event)
	{
		//actually handle stuff here.
		
		// if we have a permission area that contains this point:
		//		if we can not perform this action in this area:
		//			-> Deny the action
	}
	
	public static boolean checkPermAllowed(FEPermissionsQuery query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult().equals(Result.ALLOW);
	}
	
	public static Result checkPermResult(FEPermissionsQuery query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult();
	}
}
