package com.ForgeEssentials.util.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.ForgeEssentials.api.permissions.Zone;

@Cancelable
public class PlayerChangedZone extends PlayerEvent
{
	public final Zone	before;
	public final Zone	after;

	public PlayerChangedZone(EntityPlayer player, Zone before, Zone after)
	{
		super(player);
		this.before = before;
		this.after = after;
	}
}
