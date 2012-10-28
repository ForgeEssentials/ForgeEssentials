package com.ForgeEssentials.permissions;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;

@HasResult
public abstract class Permission extends Event
{
	public Permission()
	{
		super();
		this.setResult(Result.DEFAULT);
	}
}
