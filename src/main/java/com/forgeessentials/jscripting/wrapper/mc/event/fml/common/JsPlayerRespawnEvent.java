package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsPlayerRespawnEvent extends JsPlayerEvent<PlayerEvent.PlayerRespawnEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerRespawnEvent event) {
        _callEvent(event);
    }
}
