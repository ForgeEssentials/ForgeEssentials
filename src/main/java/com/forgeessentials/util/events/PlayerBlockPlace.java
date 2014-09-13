package com.forgeessentials.util.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class PlayerBlockPlace extends Event {

	private final World world;
	private final int blockX;
	private final int blockY;
	private final int blockZ;
	private final EntityPlayer player;
	private final int side;
	private final float hitx;
	private final float hity;
	private final float hitz;

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

	public World getWorld()
	{
		return world;
	}

	public int getBlockX()
	{
		return blockX;
	}

	public int getBlockY()
	{
		return blockY;
	}

	public int getBlockZ()
	{
		return blockZ;
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}

	public int getSide()
	{
		return side;
	}

	public float getHitx()
	{
		return hitx;
	}

	public float getHity()
	{
		return hity;
	}

	public float getHitz()
	{
		return hitz;
	}

}
