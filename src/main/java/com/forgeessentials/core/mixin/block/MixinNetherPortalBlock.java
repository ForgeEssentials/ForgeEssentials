package com.forgeessentials.core.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import com.forgeessentials.util.events.entity.EntityPortalEvent;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

@Mixin(NetherPortalBlock.class)
public class MixinNetherPortalBlock
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
		if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions()) {
			RegistryKey<World> registrykey = worldIn.dimension() == World.OVERWORLD ? World.NETHER : World.OVERWORLD;
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
