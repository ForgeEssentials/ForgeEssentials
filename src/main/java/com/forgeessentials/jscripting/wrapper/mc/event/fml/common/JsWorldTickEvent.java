package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class JsWorldTickEvent extends JsTickEvent<TickEvent.WorldTickEvent>{
    @SubscribeEvent
    public final void _handle(TickEvent.WorldTickEvent event) {
        _callEvent(event);
    }
}
