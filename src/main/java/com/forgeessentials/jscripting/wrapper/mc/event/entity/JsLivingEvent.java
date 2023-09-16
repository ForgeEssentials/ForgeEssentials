package com.forgeessentials.jscripting.wrapper.mc.event.entity;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsLivingEntityBase;

import net.minecraftforge.event.entity.living.LivingEvent;

public abstract class JsLivingEvent<T extends LivingEvent> extends JsEntityEvent<T>
{

    public JsLivingEntityBase<?> getPlayer()
    {
        return new JsLivingEntityBase<>(_event.getEntityLiving());
    }

}
