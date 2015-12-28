package com.forgeessentials.core.preloader.mixin.entity.item;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * CURRENTY DISABLED!!! @Inject does not find method in prod environment
 */
@Mixin(EntityItemFrame.class)
public abstract class MixinEntityItemFrame extends EntityHanging
{

    public MixinEntityItemFrame(World p_i1590_1_)
    {
        super(p_i1590_1_);
    }

    // @Inject(method = "Lnet/minecraft/entity/item/EntityItemFrame;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "HEAD") , cancellable = true, remap =
    // false)
    public void attackEntityFrom_event(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> ci)
    {
        EntityAttackedEvent event = new EntityAttackedEvent(this, damageSource, damage);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            ci.setReturnValue(event.result);
        }
        damage = event.damage;
    }

}
