package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.living.LivingEvent;

import com.forgeessentials.jscripting.wrapper.entity.JsEntityLivingBase;

public abstract class JsLivingEvent<T extends LivingEvent> extends JsEntityEvent<T>
{

    // public JsLivingEvent(ScriptInstance script, Object handler)
    // {
    // super(script, handler);
    // }

    // @Override
    // @SubscribeEvent
    // public void _handle(T event)
    // {
    // _callEvent(event);
    // }

    public JsEntityLivingBase<?> getPlayer()
    {
        return new JsEntityLivingBase<>(_event.getEntityLiving());
    }

}
