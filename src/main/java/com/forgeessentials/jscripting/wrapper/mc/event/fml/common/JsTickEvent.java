package com.forgeessentials.jscripting.wrapper.mc.event.fml.common;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;
import com.forgeessentials.jscripting.wrapper.mc.event.JsEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.command.ICommandSender;

public class JsTickEvent<T extends TickEvent> extends JsEvent<T> {

    public String getType() {
        return _event.type.toString();
    }

    public String getTickPhase() {
        return _event.phase.toString();
    }
}
