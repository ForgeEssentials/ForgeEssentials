package com.ForgeEssentials.permissions;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;

@HasResult
public abstract class PermQueryBase extends Event
{
	public final EntityPlayer doer;
	public final PermissionChecker permission;
	
	public PermQueryBase(EntityPlayer player, String permission)
	{
		doer = player;
		this.permission = new PermissionChecker(permission);
	}
}
