package com.forgeessentials.core.preloader.mixin.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MixinMobSpawnerBaseLogic
{
    @Shadow
    abstract World getSpawnerWorld();

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
        //System.out.println("Mixin : Spawned entity from mob spawner block");
        // hack to work around mixin bug regarding SHIFT and lvt injection
        entity.setLocationAndAngles(d2, d3, d4, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
        if (ForgeEventFactory.doSpecialSpawn(living, living.worldObj, (float) d2, (float) d3, (float) d4))
        {
            ci.cancel();
        }
    }
}
