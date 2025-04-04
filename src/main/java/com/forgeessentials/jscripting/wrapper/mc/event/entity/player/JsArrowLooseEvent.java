package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;

public class JsArrowLooseEvent extends JsPlayerEvent<ArrowLooseEvent>
{

    @SubscribeEvent
    public final void _handle(ArrowLooseEvent event)
    {
        _callEvent(event);
    }

    public JsItemStack getBow() {
        return JsItemStack.get(_event.getBow());
    }
    public JsWorld getWorld() {
        return JsWorld.get(_event.getWorld());
    }
    public boolean hasAmmo() { return _event.hasAmmo(); }
    public int getCharge() { return _event.getCharge(); }
    public void setCharge(int charge) { _event.setCharge(charge); }
}
