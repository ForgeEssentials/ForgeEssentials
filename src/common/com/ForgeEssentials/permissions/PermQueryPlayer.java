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
public class PermQueryPlayer extends Event
{
	public final EntityPlayer doer;
	public final Permission permission;
	
	/**
	 * Assumes the Players position as the "doneTo" point.
	 * @param player
	 * @param permission
	 */
	public PermQueryPlayer(EntityPlayer player, String permission)
	{
		doer = player;
		this.permission = new Permission(permission);
	}
	
	@Override
    public Result getResult()
    {
		if (super.getResult().equals(Result.DEFAULT))
			return Permission.getPermissionDefault(permission.name);
        return super.getResult();
    }
	
	public Point getDoerPoint()
	{
		return new Point((int)Math.round(doer.posX), (int)Math.round(doer.posY), (int)Math.round(doer.posZ));
	}
}

