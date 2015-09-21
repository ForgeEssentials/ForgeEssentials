package com.forgeessentials.core.preloader.mixin.fml.common;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.ListenerList;

@Mixin(EventBus.class)
public class MixinEventBus extends EventBus
{

    @Shadow
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;

    @Shadow
    private int busID;

    @Override
    @Overwrite
    public void unregister(Object object)
    {
        ArrayList<IEventListener> list = listeners.remove(object);
        if (list == null)
            return;
        for (IEventListener listener : list)
        {
            ListenerList.unregisterAll(busID, listener);
        }
    }

}
