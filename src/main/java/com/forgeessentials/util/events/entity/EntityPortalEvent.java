package com.forgeessentials.util.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityPortalEvent extends EntityEvent
{

    public final World world;

    public final BlockPos pos;

    public final BlockPos target;

    public final World targetDimension;

    public EntityPortalEvent(Entity entity, World world, BlockPos pos, World level, BlockPos target)
    {
        super(entity);
        this.world = world;
        this.pos = pos;
        this.target = target;
        this.targetDimension = level;
    }
}
