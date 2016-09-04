package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.EntityInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class JsEntityInteractEvent extends JsPlayerEvent<EntityInteractEvent>
{

    @SubscribeEvent
    public final void _handle(EntityInteractEvent event)
    {
        _callEvent(event);
    }

}
