package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EndPortalBlock.class)
public class MixinBlockEndPortal
{
    @Overwrite
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.isNonBoss() && !worldIn.isClientSide
                && entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos))
                && !MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entityIn, worldIn, pos, 1, new BlockPos(0, 0, 0))))
        {
            entityIn.changeDimension(1);
        }
    }

}
