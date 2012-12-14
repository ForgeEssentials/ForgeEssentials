package com.ForgeEssentials.permission.query;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event.HasResult;

import com.ForgeEssentials.permission.PermissionChecker;
import com.ForgeEssentials.util.AreaSelector.Point;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 *
 */
@HasResult
public class PermQueryPlayer extends PermQuery
{
	public EntityPlayer doer;
	
	/**
	 * Assumes the Players position as the "doneTo" point.
	 * @param player
	 * @param permission
	 */
	public PermQueryPlayer(EntityPlayer player, String permission)
	{
		this.doer = player;
		checker = new PermissionChecker(permission);
	}
}

