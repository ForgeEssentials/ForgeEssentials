package com.forgeessentials.core.preloader.mixin.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.MobSpawnerBaseLogic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobSpawnerBaseLogic.class)
public class MixinMobSpawnerBaseLogic
{
    @Inject(
        method = "updateSpawner",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;setLocationAndAngles(DDDFF)V"
        ),
        cancellable = true,
        require = 1,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void handleUpdateSpawner(CallbackInfo ci, double d2, boolean flag, int i, Entity entity, int j, double d3, double d4, EntityLiving living)
    {
        System.out.println(String.format("Mixin : Spawned entity [%s] from mob spawner block", entity.toString()));
        /*if (MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn()))
        {
            ci.cancel();
        }*/
    }
}
