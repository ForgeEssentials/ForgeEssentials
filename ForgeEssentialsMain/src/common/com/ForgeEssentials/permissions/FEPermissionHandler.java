package com.ForgeEssentials.permissions;

import com.ForgeEssentials.AreaSelector.Point;

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
public final class FEPermissionHandler
{	
	@ForgeSubscribe
	public void handleGenericPlayerInteractPermission(PlayerInteractPermissionQuery perm)
	{
		Point interactionPoint = new Point(perm.x, perm.y, perm.z);
		
		// if we have a permission area that contains this point:
		//		if we can not perform this action in this area:
		//			-> Deny the action
		
	}
}
