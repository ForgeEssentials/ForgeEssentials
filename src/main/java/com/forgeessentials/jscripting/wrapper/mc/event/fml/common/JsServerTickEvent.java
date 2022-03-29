package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class JsServerTickEvent extends JsTickEvent<TickEvent.ServerTickEvent>{
    @SubscribeEvent
    public final void _handle(TickEvent.ServerTickEvent event) {
        _callEvent(event);
    }
}
