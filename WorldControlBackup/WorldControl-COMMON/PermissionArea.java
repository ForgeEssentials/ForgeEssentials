package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.permissions.Permission;
import com.ForgeEssentials.permissions.PlayerInteractPermissionQuery;

/**
 * 
 * Describes an area that has been locked down by some permission (however locked that may be...)
 * 
 * @author MysteriousAges
 *
 */
public class PermissionArea extends AreaBase {

	public Permission permission; 
	
	public PermissionArea(Point start, Point end, Permission permission) {
		super(start, end);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isPlayerInteractionPermitted(PlayerInteractPermissionQuery query) {
		// TODO: Check the permission in the area.
		return true;
	}

}
