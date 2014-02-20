package com.forgeessentials.util.events;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.permission.PermissionProp;

@Cancelable
/**
 * @author AbrarSyed
 * This is thrown after the permissions are checked but before the permission is actually sent.
 */
public class PermissionPropSetEvent extends Event
{
	public PermissionProp	perm;
	public Zone				zone;
	public String			entity; // p:PlayerUsername or g:GroupName. the prefixes will be there.
	private String			reason;

	public PermissionPropSetEvent(PermissionProp perm, Zone zone, String entity)
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
			return "";
		else
			return reason;
	}
}
