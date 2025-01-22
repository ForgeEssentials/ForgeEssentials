package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class JsPlayerBreakSpeedEvent extends JsPlayerEvent<BreakSpeed>
{

    @SubscribeEvent
    public final void _handle(BreakSpeed event)
    {
        _callEvent(event);
    }

}
