package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.FireEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(BlockFire.class)
public class MixinBlockFire
{

    @Inject(
            method = "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"

            ),
            cancellable = true,
            remap = false)
    public void handleTryCatchFire(World world, BlockPos pos, int chance, Random random, int argValue1, EnumFacing face, CallbackInfo ci)
    {
        //System.out.println("Mixin : Fire destroyed block and spread to below block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos)))
        {
            ci.cancel();
        }
        else
        {
            BlockPos source = pos.add(face.getFrontOffsetX(), face.getFrontOffsetY(), face.getFrontOffsetZ());
            if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, pos, source)))
            {
                //System.out.println("Injector: Fire destroyed but could not spread to block below");
                world.setBlockToAir(pos);
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/BlockPos;)Z"),
            cancellable = true,
            remap = false)
    public void handleTryCatchFireAir(World world, BlockPos pos, int chance, Random random, int argValue1, EnumFacing face, CallbackInfo ci)
    {
        //System.out.println("Mixin : Fire destroyed block");
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos)))
        {
            ci.cancel();
        }
    }

    @Inject(
            method = "Lnet/minecraft/block/BlockFire;updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V",
            at = @At(
                    ordinal = 1,
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void updateTick(World world, BlockPos source, IBlockState state, Random rnd, CallbackInfo ci,
            Block block, boolean isFireSource, int age, boolean isHighHumidity, int something, int x, int z, int y, int something2, BlockPos pos, int neighborEncouragement, int difficultyScaling, int ageIncrement)
    {
        //System.out.println(String.format("Mixin : Fire spreading to other block from [%d,%d,%d] to [%d,%d,%d]", source.getX(), source.getY(), source.getZ(), pos.getX(), pos.getY(), pos.getZ()));
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, pos, source)))
        {
            ci.cancel();
        }
    }

}
