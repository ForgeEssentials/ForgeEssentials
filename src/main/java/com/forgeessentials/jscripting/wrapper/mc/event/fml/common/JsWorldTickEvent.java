package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class JsWorldTickEvent extends JsTickEvent<TickEvent.WorldTickEvent>{
    @SubscribeEvent
    public final void _handle(TickEvent.WorldTickEvent event) {
        _callEvent(event);
    }
}
