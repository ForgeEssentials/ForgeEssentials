package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JsItemCraftedEvent extends JsPlayerEvent<PlayerEvent.ItemCraftedEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemCraftedEvent event) {
        _callEvent(event);
    }
}
