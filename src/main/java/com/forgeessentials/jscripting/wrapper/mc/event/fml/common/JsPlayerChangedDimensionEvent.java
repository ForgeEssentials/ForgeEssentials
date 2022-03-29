package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsPlayerChangedDimensionEvent extends JsPlayerEvent<PlayerEvent.PlayerChangedDimensionEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerChangedDimensionEvent event) {
        _callEvent(event);
    }
}
