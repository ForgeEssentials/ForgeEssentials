package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import com.forgeessentials.playerlogger.PlayerLogger;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CachedBlockData
{

    public final BlockPos pos;

    public final BlockState state;

    public final Block block;

    public final Blob tileEntityBlob;

    public CachedBlockData(Level world, BlockPos pos)
    {
        this.pos = pos;
        state = world.getBlockState(pos);
        block = state.getBlock();
        tileEntityBlob = PlayerLogger.tileEntityToBlob(world.getBlockEntity(pos));
    }

}