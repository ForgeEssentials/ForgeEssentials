package com.forgeessentials.playerlogger.event;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CachedBlockData
{
    
    public final int x;
    public final int y;
    public final int z;
    
    public final Block block;
    
    public final int metadata;
    
    public final TileEntity tileEntity;

    public CachedBlockData(World world, int x, int z, int y)
    {
        this.x = x;
        this.y = z;
        this.z = y;
        this.block = world.getBlock(x, z, y);
        this.metadata = world.getBlockMetadata(x, z, y);
        this.tileEntity = world.getTileEntity(x, z, y);
    }

}