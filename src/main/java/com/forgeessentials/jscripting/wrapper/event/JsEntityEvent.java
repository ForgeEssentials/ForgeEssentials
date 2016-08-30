package com.forgeessentials.jscripting.wrapper.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import com.forgeessentials.jscripting.wrapper.entity.JsEntity;

public abstract class JsEntityEvent<T extends EntityEvent> extends JsEvent<T>
{

    // public JsEntityEvent(ScriptInstance script, Object handler)
    // {
    // super(script, handler);
    // }

    // @Override
    // @SubscribeEvent
    // public void _handle(T event)
    // {
    // _callEvent(event);
    // }

    public JsEntity<Entity> getEntity()
    {
        return new JsEntity<>(_event.entity);
    }

}
