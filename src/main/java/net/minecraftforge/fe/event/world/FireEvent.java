package net.minecraftforge.fe.event.world;

import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

public class FireEvent extends BlockEvent
{

    public FireEvent(World world, int x, int y, int z)
    {
        super(x, y, z, world, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
    }

    /**
     * Fired when a block is destroyed by fire
     */
    @Cancelable
    public static class Destroy extends FireEvent
    {
        public Destroy(World world, int x, int y, int z)
        {
            super(world, x, y, z);
        }
    }

    /**
     * Fired when a block is about to catch fire from another block.
     */
    @Cancelable
    public static class Spread extends FireEvent
    {

        public final int sourceX, sourceY, sourceZ;

        public Spread(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
        {
            super(world, x, y, z);
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.sourceZ = sourceZ;
        }

    }

}
