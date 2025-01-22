package net.minecraftforge.fe.event.world;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class FireEvent extends BlockEvent
{

    public FireEvent(World world, BlockPos pos)
    {
        super(world, pos, world.getBlockState(pos));
    }

    /**
     * Fired when a block is destroyed by fire
     */
    @Cancelable
    public static class Destroy extends FireEvent
    {
        public Destroy(World world, BlockPos pos)
        {
            super(world, pos);
        }
    }

    /**
     * Fired when a block is about to catch fire from another block.
     */
    @Cancelable
    public static class Spread extends FireEvent
    {

        public final BlockPos spreadPos;

        public Spread(World world, BlockPos pos, BlockPos spreadPos)
        {
            super(world, pos);
            this.spreadPos = spreadPos;
        }

    }

}
