package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class JsServerTickEvent extends JsTickEvent<TickEvent.ServerTickEvent>{
    @SubscribeEvent
    public final void _handle(TickEvent.ServerTickEvent event) {
        _callEvent(event);
    }
}
