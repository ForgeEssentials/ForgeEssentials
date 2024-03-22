package com.forgeessentials.util.events.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityPortalEvent extends EntityEvent
{

    public final Level worldFrom;

    public final BlockPos posFrom;

    public final BlockPos targetPos;

    public final Level targetDimension;

    public EntityPortalEvent(Entity entity, Level worldFrom, BlockPos posFrom, Level worldDestination, BlockPos targetPos)
    {
        super(entity);
        this.worldFrom = worldFrom;
        this.posFrom = posFrom;
        this.targetPos = targetPos;
        this.targetDimension = worldDestination;
    }
}
