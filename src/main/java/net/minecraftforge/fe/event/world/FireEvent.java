package net.minecraftforge.fe.event.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class FireEvent extends BlockEvent
{

    public FireEvent(World world, BlockPos pos, IBlockState state)
    {
        super(world, pos, state);
    }

    /**
     * Fired when a block is destroyed by fire
     */
    @Cancelable
    public static class Destroy extends FireEvent
    {
        public Destroy(World world, BlockPos pos, IBlockState state)
        {
            super(world, pos, state);
        }
    }

    /**
     * Fired when a block is about to catch fire from another block.
     */
    @Cancelable
    public static class Spread extends FireEvent
    {

        public final BlockPos source;

        public Spread(World world, BlockPos pos, IBlockState state, BlockPos source)
        {
            super(world, pos, state);
            this.source = source;
        }

    }

}
