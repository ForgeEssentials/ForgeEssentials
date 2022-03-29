package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsItemCraftedEvent extends JsPlayerEvent<PlayerEvent.ItemCraftedEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemCraftedEvent event) {
        _callEvent(event);
    }
}
