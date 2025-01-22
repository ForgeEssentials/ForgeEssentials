package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JsPlayerLoggedInEvent extends JsPlayerEvent<PlayerEvent.PlayerLoggedInEvent>{
    @SubscribeEvent
    public final void _handle(PlayerEvent.PlayerLoggedInEvent event) {
        _callEvent(event);
    }
}
