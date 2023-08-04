package com.forgeessentials.util.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityPortalEvent extends EntityEvent
{

    public final World worldFrom;

    public final BlockPos posFrom;

    public final BlockPos targetPos;

    public final World targetDimension;

    public EntityPortalEvent(Entity entity, World worldFrom, BlockPos posFrom, World worldDestination, BlockPos targetPos)
    {
        super(entity);
        this.worldFrom = worldFrom;
        this.posFrom = posFrom;
        this.targetPos = targetPos;
        this.targetDimension = worldDestination;
    }
}
