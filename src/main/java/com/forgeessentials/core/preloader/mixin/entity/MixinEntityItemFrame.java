package com.forgeessentials.core.preloader.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.SpecialEntityAttackedEvent;

@Mixin(EntityItemFrame.class)
public class MixinEntityItemFrame
{
    /**
     * Solve for item frame bow killing
     * 
     * @author Maximuslotro
     * @reason stuff
     */
    @Inject(at = @At("HEAD"), method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", cancellable = true, require = 1)
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback)
    {
        SpecialEntityAttackedEvent event = new SpecialEntityAttackedEvent((EntityItemFrame) (Object) this, source, amount);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            callback.setReturnValue(event.result);
        }
    }
}
