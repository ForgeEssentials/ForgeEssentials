package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPortal.class)
public class MixinBlockPortal
{

    @Overwrite
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss())
        { // TODO: get target coordinates somehow
            if (!MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entityIn, worldIn, pos, entityIn.dimension == -1 ? 0 : -1, new BlockPos(0, 0, 0))))
            {
                entityIn.setPortal(pos);
            }
        }
    }

}
