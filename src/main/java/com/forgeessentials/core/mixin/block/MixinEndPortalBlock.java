package com.forgeessentials.core.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.forgeessentials.util.events.entity.EntityPortalEvent;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

@Mixin(EndPortalBlock.class)
public class MixinEndPortalBlock
{

    /**
     * Handling entity dim traveling perms
     *
     * @author Maximuslotro
     * @reason stuff
     */
    @Inject(method = "entityInside",
            at = @At(value = "HEAD"),
            cancellable=true)
	public void runFEEntityPortalEVENT(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci) {
    	if (worldIn instanceof ServerWorld && !entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions() && VoxelShapes.joinIsNotEmpty(VoxelShapes.create(entityIn.getBoundingBox().move((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))), state.getShape(worldIn, pos), IBooleanFunction.AND)) {
    		RegistryKey<World> registrykey = worldIn.dimension() == World.END ? World.OVERWORLD : World.END;
            ServerWorld serverworld = ((ServerWorld)worldIn).getServer().getLevel(registrykey);
            if (serverworld == null) {
            	ci.cancel();
               return;
            }
			// TODO: get target coordinates somehow
            if (MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(entityIn, worldIn, pos, serverworld, new BlockPos(0, 0, 0)))) {
				ci.cancel();
			}

         } else {
 			ci.cancel();
 		}
	}

}
