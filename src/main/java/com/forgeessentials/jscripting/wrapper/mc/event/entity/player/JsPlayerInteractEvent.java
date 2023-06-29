package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JsPlayerInteractEvent extends JsPlayerEvent<PlayerInteractEvent> {

	@SubscribeEvent
	public final void _handle(PlayerInteractEvent event) {
		_callEvent(event);
	}

}
