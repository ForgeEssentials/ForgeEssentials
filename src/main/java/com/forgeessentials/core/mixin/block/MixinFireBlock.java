package com.forgeessentials.core.mixin.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.forgeessentials.util.events.world.FireEvent;

@Mixin(FireBlock.class)
public class MixinFireBlock
{
    /*
     * @Inject( method = "tryCatchFire", at = @At( value = "INVOKE", target =
     * "Lnet/minecraftforge/common/extensions/IForgeBlockState;catchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;Lnet/minecraft/entity/LivingEntity;)V"
     * ), cancellable = true, remap=false ) public void handleTryCatchFire(World world, BlockPos pos, int chance, Random random, int argValue1, Direction face, CallbackInfo ci) {
     * BlockPos source = pos.offset(face.getStepX(), face.getStepY(), face.getStepZ()); if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, pos, source))) {
     * world.setBlock(pos, Blocks.AIR.defaultBlockState(), 1); ci.cancel(); } }
     * 
     * @Inject( method = "tryCatchFire", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z" ),
     * cancellable = true, remap=false ) public void handleBlockDestroyOntryCatchFire(World world, BlockPos pos, int p_176536_3_, Random p_176536_4_, int p_176536_5_, Direction
     * face, CallbackInfo ci) { if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos))) { ci.cancel(); } }
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"),
            cancellable = true)
    public void handleBlockDestroyOnTick(BlockState p_225534_1_, ServerLevel world, BlockPos pos, Random random,
            CallbackInfo ci)
    {
        if (MinecraftForge.EVENT_BUS.post(new FireEvent.Destroy(world, pos)))
        {
            ci.cancel();
        }
    }
    /*
     * @Inject( method = "updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", at = @At( ordinal =
     * 1, value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z" ), cancellable = true,
     * locals = LocalCapture.CAPTURE_FAILEXCEPTION) public void updateTick(World world, BlockPos source, BlockState state, Random rnd, CallbackInfo ci, Block block, boolean
     * isFireSource, int age, boolean isHighHumidity, int something, int x, int z, int y, int something2, BlockPos pos, int neighborEncouragement, int difficultyScaling, int
     * ageIncrement) { // System.out.println(String. format("Mixin : Fire spreading to other block from [%d,%d,%d] to [%d,%d,%d]", source.getX(), source.getY(), source.getZ(),
     * pos.getX(), // pos.getY(), pos.getZ())); if (MinecraftForge.EVENT_BUS.post(new FireEvent.Spread(world, pos, source))) { ci.cancel(); } }
     */

}