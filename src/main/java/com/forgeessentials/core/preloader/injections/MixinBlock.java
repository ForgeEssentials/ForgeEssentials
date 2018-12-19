package com.forgeessentials.core.preloader.injections;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.FallOnBlockEvent;

import com.forgeessentials.core.preloader.asminjector.CallbackInfo;
import com.forgeessentials.core.preloader.asminjector.annotation.At;
import com.forgeessentials.core.preloader.asminjector.annotation.Inject;
import com.forgeessentials.core.preloader.asminjector.annotation.Mixin;

@Mixin(exclude = { Block.class })
public abstract class MixinBlock extends Block
{

    protected MixinBlock(Material material)
    {
        super(material);
    }

    @Inject(target = "onFallenUpon(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/entity/Entity;F)V", aliases = "onFallenUpon=func_180658_a", at = @At("HEAD"))
    protected void onFallenUpon_event(World world, BlockPos pos, Entity entity, float fallHeight, CallbackInfo ci)
    {
        // Going down a slab: speed ~ 0.4
        // Going down a block: speed ~ 0.7
        if (!world.isRemote && fallHeight > 0.2)
        {
            FallOnBlockEvent event = new FallOnBlockEvent(entity, world, pos, this, fallHeight);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                ci.doReturn();
            }
            fallHeight = event.fallHeight;
        }
    }

}
