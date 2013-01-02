package com.ForgeEssentials.core.customEvents;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class PlayerBlockBreak extends Event 
{
	public final World world;
	public final int blockX;
	public final int blockY;
	public final int blockZ;
	public final EntityPlayer player;
	
    public PlayerBlockBreak(World world, int x, int y, int z, EntityPlayer entityPlayer) 
    {
    	super();
    	this.world = world;
    	blockX = x;
    	blockY = y;
    	blockZ = z;
    	player = entityPlayer;
    }
}
