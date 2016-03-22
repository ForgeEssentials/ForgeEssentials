package com.forgeessentials.core.preloader.mixin.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.MobSpawnerBaseLogic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobSpawnerBaseLogic.class)
public class MixinMobSpawnerBaseLogic
{
    @Inject(method = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;updateSpawner()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setLocationAndAngles(DDDFF)V", shift = Shift.AFTER),
            cancellable = true, locals = LocalCapture.PRINT)
    public void handleUpdateSpawner(CallbackInfo ci)
    {
        System.out.println("Mixin : Spawned entity from mob spawner block");
        /*if (MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn()))
        {
            ci.cancel();
        }*/
    }
}
