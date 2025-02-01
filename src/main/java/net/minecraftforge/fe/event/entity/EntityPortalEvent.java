package net.minecraftforge.fe.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;


@Cancelable
public class EntityPortalEvent extends EntityEvent
{

	public final World world;

    public final BlockPos pos;

    public final BlockPos target;

    public final int targetDimension;

    public EntityPortalEvent(Entity entity, World world, BlockPos pos, int targetDimension, BlockPos target)
    {
        super(entity);
        this.world = world;
        this.pos = pos;
        this.target = target;
        this.targetDimension = targetDimension;
    }
}
