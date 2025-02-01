package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JsPlayerChangedDimensionEvent extends JsPlayerEvent<PlayerEvent.PlayerChangedDimensionEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerChangedDimensionEvent event) {
        _callEvent(event);
    }
}
