package com.forgeessentials.jscripting.wrapper.mc.event.entity;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntity;
import com.forgeessentials.jscripting.wrapper.mc.event.JsEvent;

import net.minecraftforge.event.entity.EntityEvent;

public abstract class JsEntityEvent<T extends EntityEvent> extends JsEvent<T>
{

    public JsEntity<?> getEntity()
    {
        return JsEntity.get(_event.getEntity());
    }

}
