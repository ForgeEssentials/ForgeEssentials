package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.BonemealEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JsBonemealEvent extends JsPlayerEvent<BonemealEvent>
{

    @SubscribeEvent
    public final void _handle(BonemealEvent event)
    {
        _callEvent(event);
    }

}
