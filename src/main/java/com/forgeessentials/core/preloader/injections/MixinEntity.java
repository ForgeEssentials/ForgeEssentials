package com.forgeessentials.core.preloader.injections;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;

import com.forgeessentials.core.preloader.asminjector.CallbackInfo;
import com.forgeessentials.core.preloader.asminjector.annotation.At;
import com.forgeessentials.core.preloader.asminjector.annotation.Inject;
import com.forgeessentials.core.preloader.asminjector.annotation.Mixin;

@Mixin(value = {}, targets = "*")
public abstract class MixinEntity extends Entity
{

    public MixinEntity(World world)
    {
        super(world);
    }

    @Inject(target = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", aliases = "attackEntityFrom=func_70097_a", at = @At("HEAD") )
    protected void attackEntityFrom_event(DamageSource damageSource, float damage, CallbackInfo ci)
    {
        EntityAttackedEvent event = new EntityAttackedEvent(this, damageSource, damage);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            ci.doReturn(event.result);
        }
        damage = event.damage;
    }

}
