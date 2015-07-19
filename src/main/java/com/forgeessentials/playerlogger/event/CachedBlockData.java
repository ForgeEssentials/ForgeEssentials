package com.forgeessentials.playerlogger.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CachedBlockData
{

    public final BlockPos pos;

    public final IBlockState state;

    public final Block block;

    public final int metadata;

    public final TileEntity tileEntity;

    public CachedBlockData(World world, BlockPos pos)
    {
        this.pos = pos;
        state = world.getBlockState(pos);
        block = state.getBlock();
        metadata = block.getMetaFromState(state);
        tileEntity = world.getTileEntity(pos);
    }

}