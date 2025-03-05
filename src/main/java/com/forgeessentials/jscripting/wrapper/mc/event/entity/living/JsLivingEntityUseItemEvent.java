package com.forgeessentials.jscripting.wrapper.mc.event.entity.living;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.jscripting.wrapper.mc.event.entity.JsLivingEvent;

public class JsLivingEntityUseItemEvent extends JsLivingEvent<LivingEntityUseItemEvent>
{

    @SubscribeEvent
    public final void _handle(LivingEntityUseItemEvent event)
    {
        _callEvent(event);
    }

}
