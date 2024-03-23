package com.forgeessentials.core.mixin.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.forgeessentials.util.events.entity.EntityAttackedEvent;

@Mixin(ItemFrame.class)
public class MixinItemFrameEntity
{
    /**
     * Solve for item frame bow killing
     * 
     * @author Maximuslotro
     * @reason stuff
     */
    @Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback)
    {
        EntityAttackedEvent event = new EntityAttackedEvent((ItemFrame) (Object) this, source, amount);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            callback.setReturnValue(event.result);
        }
    }
}
