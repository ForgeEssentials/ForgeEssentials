package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class JsPlayerTickEvent extends JsTickEvent<TickEvent.PlayerTickEvent>{
    @SubscribeEvent
    public final void _handle(TickEvent.PlayerTickEvent event) {
        _callEvent(event);
    }
}
