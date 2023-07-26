package com.forgeessentials.core.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import com.forgeessentials.util.events.entity.EntityPortalEvent;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mixin(NetherPortalBlock.class)
public class MixinNetherPortalBlock
{

    /**
     * Custom portal stuff
     *
     * @author Maximuslotro
     * @reason stuff
     */
    @Inject(method = "entityInside",
            at = @At(value = "HEAD"),
            cancellable=true)
    public void runFEEntityPortalEVENT(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci)
    {
        if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions())
        {
            if (MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entityIn, worldIn, pos, entityIn.level, entityIn.blockPosition()))) {
                ci.cancel();
            }
        }
        else{
            ci.cancel();
        }
    }

}
