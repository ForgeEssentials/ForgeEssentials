package com.forgeessentials.core.preloader.mixin.fml.common.eventhandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

import java.util.ArrayList;

@Mixin(EventBus.class)
public class MixinEventBus
{

    /**
     * Prevent a {@link NullPointerException} when unregistering an object that has
     * no listeners.
     *
     * @param object the object to unregister
     * @param ci the callback info
     * @param listeners the list of listeners removed
     */
    @Inject(
        method = "unregister",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/ArrayList;iterator()Ljava/util/Iterator;",
            shift = At.Shift.BEFORE
        ),
        remap = false,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        cancellable = true
    )
    private void nullCheckListeners(Object object, CallbackInfo ci, ArrayList<IEventListener> listeners)
    {
        if (listeners == null)
        {
            ci.cancel();
        }
    }

}
