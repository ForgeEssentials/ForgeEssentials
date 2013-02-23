package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.util.AreaSelector.AreaBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EventWCCommand extends PlayerEvent
{
	public AreaBase area;
	public EventWCCommand(EntityPlayer player, AreaBase area)
	{
		super(player);
		this.area = area;
	}
	
}
