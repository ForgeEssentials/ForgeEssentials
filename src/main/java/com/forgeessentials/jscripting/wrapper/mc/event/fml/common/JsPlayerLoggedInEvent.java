package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsPlayerLoggedInEvent extends JsPlayerEvent<PlayerEvent.PlayerLoggedInEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerLoggedInEvent event) {
        _callEvent(event);
    }
}
