package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.EntityEvent;

import com.forgeessentials.jscripting.wrapper.entity.JsEntity;

public abstract class JsEntityEvent<T extends EntityEvent> extends JsEvent<T>
{

    // @Override
    // @SubscribeEvent
    // public void _handle(T event)
    // {
    // _callEvent(event);
    // }

    public JsEntity<?> getEntity()
    {
        return JsEntity.get(_event.entity);
    }

}
