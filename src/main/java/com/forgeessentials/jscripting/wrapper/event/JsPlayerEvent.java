package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayer;

public abstract class JsPlayerEvent<T extends PlayerEvent> extends JsLivingEvent<T>
{

    public JsPlayerEvent(ScriptInstance script, Object handler)
    {
        super(script, handler);
    }

    // @Override
    // @SubscribeEvent
    // public void _handle(T event)
    // {
    // _callEvent(event);
    // }

    @Override
    public JsEntityPlayer getPlayer()
    {
        return new JsEntityPlayer(_event.entityPlayer);
    }

}
