package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JsItemSmeltedEvent extends JsPlayerEvent<PlayerEvent.ItemSmeltedEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.ItemSmeltedEvent event) {
        _callEvent(event);
    }
}
