package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.forgeessentials.playerlogger.PlayerLogger;

public class CachedBlockData
{

    public final BlockPos pos;

    public final BlockState state;

    public final Block block;


    public final Blob tileEntityBlob;

    public CachedBlockData(World world, BlockPos pos)
    {
        this.pos = pos;
        state = world.getBlockState(pos);
        block = state.getBlock();
        tileEntityBlob = PlayerLogger.tileEntityToBlob(world.getBlockEntity(pos));
    }

}