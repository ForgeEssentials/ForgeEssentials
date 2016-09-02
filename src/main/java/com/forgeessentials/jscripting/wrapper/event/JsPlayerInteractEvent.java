package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class JsPlayerInteractEvent extends JsPlayerEvent<PlayerInteractEvent>
{

    @SubscribeEvent
    public void _handle(PlayerInteractEvent event)
    {
        _callEvent(event);
    }

}
