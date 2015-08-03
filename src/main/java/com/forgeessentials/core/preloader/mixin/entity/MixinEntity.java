package com.forgeessentials.core.preloader.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.PressurePlateEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Entity.class)
public abstract class MixinEntity extends Entity
{

    public MixinEntity(World world)
    {
        super(world);
    }

    @Override
    @Overwrite
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return MinecraftForge.EVENT_BUS.post(new PressurePlateEvent(this));
    }

}