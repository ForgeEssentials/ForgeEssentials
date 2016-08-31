package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JsPlayerInteractEvent<T extends PlayerInteractEvent> extends JsPlayerEvent<T>
{

    // public JsPlayerInteractEvent(ScriptInstance script, Object handler)
    // {
    // super(script, handler);
    // }

    @Override
    @SubscribeEvent
    public void _handle(T event)
    {
        _callEvent(event);
    }

}
