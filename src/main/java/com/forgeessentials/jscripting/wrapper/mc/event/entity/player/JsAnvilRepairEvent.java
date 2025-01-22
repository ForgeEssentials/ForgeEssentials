package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class JsAnvilRepairEvent extends JsPlayerEvent<AnvilRepairEvent>
{

    @SubscribeEvent
    public final void _handle(AnvilRepairEvent event)
    {
        _callEvent(event);
    }

}
