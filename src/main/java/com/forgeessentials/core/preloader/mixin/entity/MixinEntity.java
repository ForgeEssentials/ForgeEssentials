package com.forgeessentials.core.preloader.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.PressurePlateEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity
{

	/**
     * Send pressure-plate event on plate depress
     * @reason we want to add perms so players can't activate redstone in protected areas without permission
     */
	@Inject(method = "doesEntityNotTriggerPressurePlate()Z", at = @At("HEAD"), cancellable = true)
    public void isFEIgnoringBlockTriggers(CallbackInfoReturnable<Boolean> callback)
	{
		if(MinecraftForge.EVENT_BUS.post(new PressurePlateEvent((Entity) (Object) this)))
		{
			callback.setReturnValue(true);
		}
	}
}