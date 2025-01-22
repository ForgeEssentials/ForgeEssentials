package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import com.forgeessentials.jscripting.wrapper.mc.event.JsEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public class JsTickEvent<T extends TickEvent> extends JsEvent<T> {

    public String getType() {
        return _event.type.toString();
    }

    public String getTickPhase() {
        return _event.phase.toString();
    }
}
