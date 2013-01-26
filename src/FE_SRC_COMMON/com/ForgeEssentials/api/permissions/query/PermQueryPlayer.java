package com.ForgeEssentials.api.permissions.query;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event.HasResult;

import com.ForgeEssentials.permission.PermissionChecker;

/**
 * Reuslts are: default, allow, deny.
 * 
 * @author AbrarSyed
 * 
 */
@HasResult
public class PermQueryPlayer extends PermQuery
{
	public EntityPlayer doer;
	public boolean dOverride;

	/**
	 * Assumes the Players position as the "doneTo" point.
	 * 
	 * @param player
	 * @param permission
	 */
	public PermQueryPlayer(EntityPlayer player, String permission)
	{
		doer = player;
		checker = new PermissionChecker(permission);
		checkForward = false;
	}

	/**
	 * Assumes the Players position as the "doneTo" point.
	 * 
	 * @param player
	 * @param permission
	 * @param checkForward
	 *            Specifies to only return allow if all the children of the permission are allowed.
	 */
	public PermQueryPlayer(EntityPlayer player, String permission, boolean checkForward)
	{
		doer = player;
		checker = new PermissionChecker(permission);
		this.checkForward = checkForward;
	}
}
