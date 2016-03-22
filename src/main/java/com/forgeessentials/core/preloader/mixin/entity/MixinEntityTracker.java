package com.forgeessentials.core.preloader.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.core.preloader.api.EntityTrackerHelper;

@Mixin(EntityTracker.class)
public class MixinEntityTracker implements EntityTrackerHelper
{

    @Shadow
    private IntHashMap trackedEntityIDs = new IntHashMap();

    /**
     * Provides support for vanish functionality.
     *
     * @param entity the entity
     * @return the entity tracker entry
     */
    @Override
    public EntityTrackerEntry getEntityTrackerEntry(Entity entity)
    {
        return (EntityTrackerEntry) trackedEntityIDs.lookup(entity.getEntityId());
    }

}
