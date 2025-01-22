package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class JsEntityInteractEvent extends JsPlayerEvent<EntityInteractEvent>
{

    @SubscribeEvent
    public final void _handle(EntityInteractEvent event)
    {
        _callEvent(event);
    }

}
