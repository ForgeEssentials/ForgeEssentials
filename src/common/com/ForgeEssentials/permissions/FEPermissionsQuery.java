package com.ForgeEssentials.permissions;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.*;

/**
 * Reuslts are: default, allow, deny.
 * @author AbrarSyed
 *
 */
@HasResult
public class FEPermissionsQuery extends Event
{
	public final EntityPlayer doer;
	public final AreaBase doneTo;
	public final String permission;
	
	public FEPermissionsQuery(EntityPlayer player, String permission, AreaBase doneTo)
	{
		doer = player;
		this.doneTo = doneTo;
		this.permission = permission;
	}
	
	public FEPermissionsQuery(EntityPlayer player, String permission, Point doneTo)
	{
		this(player, permission, new Selection(doneTo, doneTo));
	}
	
	/**
	 * Assumes the Players position as the "doneTo" point.
	 * @param player
	 * @param permission
	 */
	public FEPermissionsQuery(EntityPlayer player, String permission)
	{
		this(player, permission, getAreaofPlayer(player));
	}
	
	@Override
    public Result getResult()
    {
		if (super.getResult().equals(Result.DEFAULT))
			return Permission.getPermissionDefault(permission);
        return super.getResult();
    }
	
	protected static Point getAreaofPlayer(EntityPlayer player)
	{
		return new Point((int)Math.round(player.posX), (int)Math.round(player.posY), (int)Math.round(player.posZ));
	}
}
