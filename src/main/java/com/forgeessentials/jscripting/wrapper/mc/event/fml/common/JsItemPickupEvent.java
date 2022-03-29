package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsItemPickupEvent extends JsPlayerEvent<PlayerEvent.ItemPickupEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemPickupEvent event) {
        _callEvent(event);
    }
}
