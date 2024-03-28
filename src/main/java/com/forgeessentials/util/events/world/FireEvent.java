package com.forgeessentials.util.events.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class FireEvent extends BlockEvent
{
    public Level eventLevel;
    public FireEvent(Level world, BlockPos pos)
    {
        super(world, pos, world.getBlockState(pos));
        eventLevel = world;
    }

    /**
     * Fired when a block is destroyed by fire
     */
    @Cancelable
    public static class Destroy extends FireEvent
    {
        public Destroy(Level world, BlockPos pos)
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

        public final BlockPos source;

        public Spread(Level world, BlockPos pos, BlockPos source)
        {
            super(world, pos);
            this.source = source;
        }

    }

}
