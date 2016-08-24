package net.minecraftforge.fe.event.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class FallOnBlockEvent extends EntityEvent
{

    public final World world;

    public final int x;
    
    public final int y;
    
    public final int z;
    
    public final Block block;
    
    public float speed;

    public FallOnBlockEvent(Entity entity, World world, int x, int y, int z, Block block, float speed)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.speed = speed;
    }

}
