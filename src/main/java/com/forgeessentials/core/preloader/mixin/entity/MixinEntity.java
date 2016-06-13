package com.forgeessentials.core.preloader.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.PressurePlateEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Entity.class)
public class MixinEntity
{

    @Overwrite
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return MinecraftForge.EVENT_BUS.post(new PressurePlateEvent((Entity) (Object) this));
    }

}