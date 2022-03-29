package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class JsItemSmeltedEvent extends JsPlayerEvent<PlayerEvent.ItemSmeltedEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemSmeltedEvent event) {
        _callEvent(event);
    }
}
