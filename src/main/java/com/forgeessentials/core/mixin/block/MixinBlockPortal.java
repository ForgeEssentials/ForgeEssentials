package com.forgeessentials.core.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.forgeessentials.util.events.entity.EntityPortalEvent;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mixin(NetherPortalBlock.class)
public class MixinBlockPortal
{

    /**
     * Custom portal stuff
     * 
     * @author Maximuslotro
     * @reason stuff
     */
    @Overwrite
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions())
        { // TODO: get target
          // coordinates
          // somehow
            if (!MinecraftForge.EVENT_BUS
                    .post(new EntityPortalEvent(entityIn, worldIn, pos, entityIn.level, new BlockPos(0, 0, 0))))
            {
                entityIn.handleInsidePortal(pos);
            }
        }
    }

}
