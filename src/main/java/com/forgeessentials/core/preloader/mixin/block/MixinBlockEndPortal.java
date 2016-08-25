package com.forgeessentials.core.preloader.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

@Mixin(BlockEndPortal.class)
public class MixinBlockEndPortal
{

    @Overwrite
    public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    { // TODO: get target coordinates somehow
        if (p_149670_5_.ridingEntity == null && p_149670_5_.riddenByEntity == null && !p_149670_1_.isRemote && !MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(p_149670_5_, p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, 1, 0, 0, 0)))
        {
            p_149670_5_.travelToDimension(1);
        }
    }

}
