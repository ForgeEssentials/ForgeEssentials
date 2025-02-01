package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockPortal;
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

@Mixin(BlockPortal.class)
public class MixinBlockPortal
{
    @Inject(method = "onEntityCollidedWithBlock",
            at = @At(value = "HEAD"),
            cancellable=true)
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo ci)
    {
    	if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null && !worldIn.isRemote)
        {
    		// TODO: get target coordinates somehow
    		if (MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entityIn, worldIn, pos, entityIn.dimension == -1 ? 0 : -1, BlockPos.ORIGIN))) {
    			ci.cancel();
    		}
    	}else
    	{
    		ci.cancel();
    	}
    }

}
