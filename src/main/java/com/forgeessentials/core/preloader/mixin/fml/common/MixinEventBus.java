package com.forgeessentials.core.preloader.mixin.fml.common;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EventBus.class)
public class MixinEventBus extends EventBus
{

    @Shadow(remap = false)
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;

    @Shadow(remap = false)
    private int busID;

    @Override
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
