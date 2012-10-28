package com.ForgeEssentials.permissions;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;

@HasResult
public class Permission extends Event
{
	public Permission(EntityPlayer player)
	{
		super();
	}
}
