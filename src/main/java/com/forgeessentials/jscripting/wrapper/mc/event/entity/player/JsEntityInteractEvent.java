package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JsEntityInteractEvent extends JsPlayerEvent<PlayerInteractEvent.EntityInteract>
{

    @SubscribeEvent
    public final void _handle(PlayerInteractEvent.EntityInteract event)
    {
        _callEvent(event);
    }

}
