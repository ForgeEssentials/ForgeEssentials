package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEndPortal.class)
public class MixinBlockEndPortal
{
    @Inject(method = "onEntityCollidedWithBlock",
            at = @At(value = "HEAD"),
            cancellable=true)
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity, CallbackInfo ci)
    {
    	if (entity.ridingEntity == null && entity.riddenByEntity == null && !world.isRemote)
        {
    		// TODO: get target coordinates somehow
    		if (MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entity, world, pos, 1, BlockPos.ORIGIN))) {
    			ci.cancel();
    		}
    	}else
    	{
    		ci.cancel();
    	}
    }
}
