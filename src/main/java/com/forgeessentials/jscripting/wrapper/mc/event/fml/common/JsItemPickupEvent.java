package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JsItemPickupEvent extends JsPlayerEvent<PlayerEvent.ItemPickupEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemPickupEvent event) {
        _callEvent(event);
    }
}
