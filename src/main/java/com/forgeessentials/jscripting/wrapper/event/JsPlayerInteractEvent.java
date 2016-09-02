package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class JsPlayerInteractEvent<T extends PlayerInteractEvent> extends JsPlayerEvent<T>
{

    @Override
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void _handle(PlayerInteractEvent event)
    {
        _callEvent((T) event);
    }

}
