package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.FallOnBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFarmland.class)
public class MixinBlockFarmland  extends Block {
    protected MixinBlockFarmland(Material material)
    {
        super(material);
    }

    @Inject(method = "onFallenUpon", at = @At("HEAD"), cancellable = true)
    protected void onFallenUpon_event(World world, BlockPos pos, Entity entity, float fallHeight, CallbackInfo ci)
    {
        // Going down a slab: speed ~ 0.4
        // Going down a block: speed ~ 0.7

        if (!world.isRemote && fallHeight > 0.2)
        {
            FallOnBlockEvent event = new FallOnBlockEvent(entity, world, pos, this, fallHeight);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                ci.cancel();
            }
            fallHeight = event.fallHeight;
        }
    }
}
