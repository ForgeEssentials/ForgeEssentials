package com.forgeessentials.core.preloader.mixin.block;

import net.minecraft.block.BlockPortal;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPortal.class)
public class MixinBlockPortal
{

    @Overwrite
    public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        if (p_149670_5_.ridingEntity == null && p_149670_5_.riddenByEntity == null)
        {
            if (!p_149670_1_.isRemote && MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(p_149670_5_, p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, p_149670_5_.dimension == -1 ? 0 : -1)))
                return;
            p_149670_5_.setInPortal();
        }
    }

}
