package com.forgeessentials.core.preloader.injections;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
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

    @Inject(target = "onFallenUpon(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;F)V", aliases = "onFallenUpon=func_149746_a", at = @At("HEAD"))
    protected void onFallenUpon_event(World world, int x, int y, int z, Entity entity, float speed, CallbackInfo ci)
    {
        if (!world.isRemote && speed > 0.5)
        {
            FallOnBlockEvent event = new FallOnBlockEvent(entity, world, x, y, z, this, speed);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                ci.doReturn();
            }
            speed = event.speed;
        }
    }

}
