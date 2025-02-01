package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.forgeessentials.playerlogger.PlayerLogger;

public class CachedBlockData
{

    public final BlockPos pos;

    public final IBlockState state;

    public final Block block;

    public final int metadata;

    public final Blob tileEntityBlob;

    public CachedBlockData(World world, BlockPos pos)
    {
        this.pos = pos;
        state = world.getBlockState(pos);
        block = state.getBlock();
        metadata = block.getMetaFromState(state);
        tileEntityBlob = PlayerLogger.tileEntityToBlob(world.getTileEntity(pos));
    }

}