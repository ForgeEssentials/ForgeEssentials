package com.forgeessentials.core.preloader.mixin;

import java.util.Hashtable;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.DimensionManagerHelper;
import net.minecraftforge.fe.event.world.WorldPreLoadEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionManager.class)
public abstract class MixinDimensionManager
{

    @Shadow(remap = false)
    private static Hashtable<Integer, Boolean> spawnSettings;

    @Shadow(remap = false)
    public static int getProviderType(int dim)
    {
        return 0;
    }

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

    @Inject(
            method = "shouldLoadSpawn(I)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private static void shouldLoadSpawn(int dim, CallbackInfoReturnable<Boolean> cir)
    {
    	Boolean shouldSpawnOverride = DimensionManagerHelper.keepLoaded.get(dim);
        if (shouldSpawnOverride != null)
        	cir.setReturnValue(shouldSpawnOverride);
        int id = getProviderType(dim);
        cir.setReturnValue(spawnSettings.containsKey(id) && spawnSettings.get(id));
    }

}
