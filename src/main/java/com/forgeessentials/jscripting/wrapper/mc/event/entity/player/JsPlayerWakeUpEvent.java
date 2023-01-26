package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JsPlayerWakeUpEvent extends JsPlayerEvent<PlayerWakeUpEvent>
{

    @SubscribeEvent
    public final void _handle(PlayerWakeUpEvent event)
    {
        _callEvent(event);
    }

}
