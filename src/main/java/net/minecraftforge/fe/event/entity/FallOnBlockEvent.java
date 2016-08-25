package net.minecraftforge.fe.event.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class FallOnBlockEvent extends EntityEvent
{

    public final World world;

    public final BlockPos pos;
    
    public final Block block;
    
    public float fallHeight;

    public FallOnBlockEvent(Entity entity, World world, BlockPos pos, Block block, float speed)
    {
        super(entity);
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.fallHeight = speed;
    }

}
