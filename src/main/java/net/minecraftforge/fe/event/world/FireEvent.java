package net.minecraftforge.fe.event.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

public class FireEvent extends BlockEvent
{

    public FireEvent(int x, int y, int z, World world, Block block, int blockMetadata)
    {
        super(x, y, z, world, block, blockMetadata);
    }

    /**
     * Fired when a block is destroyed by fire
     */
    @Cancelable
    public static class Destroy extends FireEvent
    {
        public Destroy(int x, int y, int z, World world, Block block, int meta)
        {
            super(x, y, z, world, block, meta);
        }
    }

    /**
     * Fired when a block is about to catch fire from another block.
     */
    @Cancelable
    public static class Spread extends FireEvent
    {

        public final int sourceX, sourceY, sourceZ;

        public Spread(int x, int y, int z, World world, Block block, int meta, int sourceX, int sourceY, int sourceZ)
        {
            super(x, y, z, world, block, meta);
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.sourceZ = sourceZ;
        }

    }

}
