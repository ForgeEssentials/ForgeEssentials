package com.forgeessentials.core.preloader.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker implements EntityTrackerHelper
{

    @Shadow
    private IntHashMap trackedEntityIDs = new IntHashMap();

    @Override
    public EntityTrackerEntry getEntityTrackerEntry(Entity entity)
    {
        return (EntityTrackerEntry) trackedEntityIDs.lookup(entity.getEntityId());
    }

}
