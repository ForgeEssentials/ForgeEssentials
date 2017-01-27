package com.forgeessentials.core.preloader.mixin;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.WorldPreLoadEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DimensionManager.class)
public abstract class MixinDimensionManager
{

    @Inject(
            method = "initDimension(I)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private static void initDimension(int dim, CallbackInfo ci)
    {
        WorldPreLoadEvent event = new WorldPreLoadEvent(dim);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            ci.cancel();
        }
    }

}
