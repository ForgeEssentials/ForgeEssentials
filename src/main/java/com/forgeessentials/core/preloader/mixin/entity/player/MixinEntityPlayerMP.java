package com.forgeessentials.core.preloader.mixin.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP
{

	@Inject(method = "canCommandSenderUseCommand(ILjava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    public void canCommandSenderUseCommand(int level, String command, CallbackInfoReturnable<Boolean> callback)
    {
		callback.setReturnValue(true);
    }

}
