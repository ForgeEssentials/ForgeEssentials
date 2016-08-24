package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import com.forgeessentials.playerlogger.PlayerLogger;

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

    public final Blob tileEntityBlob;

    public CachedBlockData(World world, int x, int z, int y)
    {
        this.x = x;
        this.y = z;
        this.z = y;
        this.block = world.getBlock(x, z, y);
        this.metadata = world.getBlockMetadata(x, z, y);
        this.tileEntityBlob = PlayerLogger.tileEntityToBlob(world.getTileEntity(x, z, y));
    }

}