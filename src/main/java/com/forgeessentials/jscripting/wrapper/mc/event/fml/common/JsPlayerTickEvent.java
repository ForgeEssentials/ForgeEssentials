package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class JsPlayerTickEvent extends JsTickEvent<TickEvent.PlayerTickEvent>{
    @SubscribeEvent
    public final void _handle(TickEvent.PlayerTickEvent event) {
        _callEvent(event);
    }
}
