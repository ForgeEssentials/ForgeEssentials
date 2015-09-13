package net.minecraftforge.fe.event.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * Fired when a block is destroyed by fire.
 */

@Cancelable
public class BlockDestroyedByFireEvent extends BlockEvent
{
    public BlockDestroyedByFireEvent(int x, int y, int z, World world, Block block, int meta)
    {
        super(x, y, z, world, block, meta);
    }
}
