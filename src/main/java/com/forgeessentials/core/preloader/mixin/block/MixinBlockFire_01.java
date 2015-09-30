package com.forgeessentials.core.preloader.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.FireEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire_01 extends Block
{
    // dummy constructor, leave me alone
    public MixinBlockFire_01(Material material)
    {
        super(material);
    }

    @Inject(method = "Lnet/minecraft/block/BlockFire;tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"), cancellable = true, remap = false)
    public void handleTryCatchFireA(World world, BlockPos pos, int p_149841_5_, Random p_149841_6_, int p_149841_7_, EnumFacing face, CallbackInfo ci)
    {
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos, world.getBlockState(pos))))
        {
            ci.cancel();
        }
    }

    @Inject(method = "Lnet/minecraft/block/BlockFire;tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/BlockPos;)Z"), cancellable = true, remap = false)
    public void handleTryCatchFireB(World world, BlockPos pos, int p_149841_5_, Random p_149841_6_, int p_149841_7_, EnumFacing face, CallbackInfo ci)
    {
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos, world.getBlockState(pos))))
        {
            ci.cancel();
        }
    }
    
}
