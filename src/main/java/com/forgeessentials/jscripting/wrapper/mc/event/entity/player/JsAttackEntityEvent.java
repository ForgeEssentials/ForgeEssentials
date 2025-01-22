package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class JsAttackEntityEvent extends JsPlayerEvent<AttackEntityEvent>
{

    @SubscribeEvent
    public final void _handle(AttackEntityEvent event)
    {
        _callEvent(event);
    }

}
