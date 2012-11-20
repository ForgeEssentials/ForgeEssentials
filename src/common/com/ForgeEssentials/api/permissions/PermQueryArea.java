package com.ForgeEssentials.api.permissions;

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
public class PermQueryArea extends PermQueryPlayer
{
	public final String permission;
	
	public PermQueryArea(EntityPlayer player, String permission, AreaBase doneTo)
	{
		super(player, permission);
		this.permission = permission;
	}
	
	public PermQueryArea(EntityPlayer player, String permission, Point doneTo)
	{
		this(player, permission, new Selection(doneTo, doneTo));
	}
	
	@Override
    public Result getResult()
    {
		if (super.getResult().equals(Result.DEFAULT))
			return Permission.getPermissionDefault(permission);
        return super.getResult();
    }
}
