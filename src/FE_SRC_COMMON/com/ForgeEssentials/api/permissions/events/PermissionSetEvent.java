package com.ForgeEssentials.api.permissions.events;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.permission.Permission;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
/**
 * @author AbrarSyed
 * This is thrown after the permissions are checked but before the permission is actually sent.
 */
public class PermissionSetEvent extends Event
{
	public Permission perm;
	public Zone zone;
	public String entity; // p:PlayerUsername or g:GroupName. the prefixes will
							// be there.

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
		{
			this.reason = reason;
		}

		super.setCanceled(cancel);
	}

	@Override
	@Deprecated
	/**
	 * @see com.ForgeEssentials.permissions.event.PermissionSetEvent.setCancelled(boolean, reason)
	 */
	public void setCanceled(boolean cancel)
	{
		if (cancel)
		{
			reason = "unspecified reason";
		}

		super.setCanceled(cancel);
	}

	public String getCancelReason()
	{
		if (!isCanceled())
		{
			return "";
		}
		else
		{
			return reason;
		}
	}
}
