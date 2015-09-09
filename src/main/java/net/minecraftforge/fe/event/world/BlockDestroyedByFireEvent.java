package net.minecraftforge.fe.event.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class BlockDestroyedByFireEvent extends BlockEvent
{
    public BlockDestroyedByFireEvent(int x, int y, int z, World world, Block block, int meta)
    {
        super(x, y, z, world, block, meta);
    }
}
