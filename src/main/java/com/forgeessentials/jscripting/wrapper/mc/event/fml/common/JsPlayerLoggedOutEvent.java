package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsPlayerLoggedOutEvent extends JsPlayerEvent<PlayerEvent.PlayerLoggedOutEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerLoggedOutEvent event) {
        _callEvent(event);
    }
}
