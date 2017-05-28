package com.forgeessentials.jscripting.wrapper.mc.event.entity;

import net.minecraftforge.event.entity.living.LivingEvent;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityLivingBase;

public abstract class JsLivingEvent<T extends LivingEvent> extends JsEntityEvent<T>
{

    public JsEntityLivingBase<?> getPlayer()
    {
        return new JsEntityLivingBase<>(_event.getEntityLiving());
    }

}
