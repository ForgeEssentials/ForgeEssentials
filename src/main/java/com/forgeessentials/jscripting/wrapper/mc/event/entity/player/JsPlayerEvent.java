package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsPlayerEntity;
import com.forgeessentials.jscripting.wrapper.mc.event.entity.JsLivingEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class JsPlayerEvent<T extends PlayerEvent> extends JsLivingEvent<T>
{

    @Override
    public JsPlayerEntity getPlayer()
    {
        return JsPlayerEntity.get(_event.getPlayer());
    }

    /**
     * @tsd.ignore
     */
    @Override
    public CommandSourceStack _getSender()
    {
        return _event.getPlayer().createCommandSourceStack();
    }

}
