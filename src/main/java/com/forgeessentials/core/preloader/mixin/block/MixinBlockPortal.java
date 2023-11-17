package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockPortal;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockPortal.class)
public class MixinBlockPortal
{
    @Inject(method = "onEntityCollidedWithBlock",
            at = @At(value = "HEAD"),
            cancellable=true)
    public void onEntityCollidedWithBlock(World world, int X, int Y, int Z, Entity entity, CallbackInfo ci)
    {
    	if (entity.ridingEntity == null && entity.riddenByEntity == null && !world.isRemote)
        {
    		// TODO: get target coordinates somehow
    		if (MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entity, world, X, Y, Z, entity.dimension == -1 ? 0 : -1, 0 , 0, 0))) {
    			ci.cancel();
    		}
    	}else
    	{
    		ci.cancel();
    	}
    }

}
