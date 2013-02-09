package com.ForgeEssentials.core.customEvents;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class PlayerBlockPlace extends Event
{
	public final World			world;
	public final int			blockX;
	public final int			blockY;
	public final int			blockZ;
	public final EntityPlayer	player;
	public final int			side;
	public final float			hitx;
	public final float			hity;
	public final float			hitz;

	public PlayerBlockPlace(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz)
	{
		super();
		this.world = world;
		blockX = x;
		blockY = y;
		blockZ = z;
		this.player = player;
		this.side = side;
		this.hitx = hitx;
		this.hity = hity;
		this.hitz = hitz;
	}
}
