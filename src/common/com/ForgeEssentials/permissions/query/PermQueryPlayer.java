package com.ForgeEssentials.permissions.query;

import com.ForgeEssentials.permissions.PermissionChecker;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.*;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 *
 */
@HasResult
public class PermQueryPlayer extends PermQuery
{
	public EntityPlayer doer;
	public PermissionChecker checker;
	
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
	
	public Point getDoerPoint()
	{
		return new Point((int)Math.round(doer.posX), (int)Math.round(doer.posY), (int)Math.round(doer.posZ));
	}
}

