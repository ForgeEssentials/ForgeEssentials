package net.minecraftforge.fe.event.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * Fired when a block is about to catch fire from another block.
 */

@Cancelable
public class FireSpreadEvent extends BlockEvent
{

    public final int sourceX, sourceY, sourceZ;

    public FireSpreadEvent(int affectedX, int affectedY, int affectedZ, World world, Block block, int meta, int sourceX, int sourceY, int sourceZ)
    {
        super(affectedX, affectedY, affectedZ, world, block, meta);
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
    }
}
