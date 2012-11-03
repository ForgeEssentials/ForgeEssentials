package com.ForgeEssentials.permissions;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;

/**
 * 
 * Not sure how useful this class will be, as it has no spatial coords, or... anything really.
 * 
 * @author AbrarSyed
 */
@HasResult
@Cancelable
public abstract class GenericPermission extends Event
{
	public GenericPermission()
	{
		super();
	}
}
