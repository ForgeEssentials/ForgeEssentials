package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JsAdvancementEvent extends JsPlayerEvent<AdvancementEvent>
{

    @SubscribeEvent
    public final void _handle(AdvancementEvent event)
    {
        _callEvent(event);
    }

}
