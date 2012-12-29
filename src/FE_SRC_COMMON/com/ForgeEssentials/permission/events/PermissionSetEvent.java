package com.ForgeEssentials.permission.events;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

import com.ForgeEssentials.permission.Permission;
import com.ForgeEssentials.permission.Zone;

@Cancelable
/**
 * @author AbrarSyed
 * This is thrown after the permissions are checked but before the permission is actually sent.
 */
public class PermissionSetEvent extends Event
{
	public Permission	perm;
	public Zone			zone;
	public String		entity; // p:PlayerUsername  or g:GroupName. the prefixes will be there.
	
	private String reason;
	
	public PermissionSetEvent(Permission perm, Zone zone, String entity)
	{
		this.perm = perm;
		this.zone = zone;
		this.entity = entity;
	}
	
	public void setCanceled(boolean cancel, String reason)
	{
		if (cancel)
			this.reason = reason;
		
		super.setCanceled(cancel);
	}
	
	@Deprecated
	/**
	 * @see com.ForgeEssentials.permissions.event.PermissionSetEvent.setCancelled(boolean, reason)
	 */
	public void setCanceled(boolean cancel)
	{
		if (cancel)
			this.reason = "unspecified reason";
		
		super.setCanceled(cancel);
	}
	
	public String getCancelReason()
	{
		if (!this.isCanceled())
			return "";
		else
			return reason;
	}
}
