package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JsEntityItemPickupEvent extends JsPlayerEvent<EntityItemPickupEvent>
{

    @SubscribeEvent
    public final void _handle(EntityItemPickupEvent event)
    {
        _callEvent(event);
    }

}
