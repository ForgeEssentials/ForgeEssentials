package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsPlayerEntity;
import com.forgeessentials.jscripting.wrapper.mc.event.entity.JsLivingEvent;

public abstract class JsPlayerEvent<T extends PlayerEvent> extends JsLivingEvent<T>
{

    @Override
    public JsPlayerEntity getPlayer()
    {
        return JsPlayerEntity.get(_event.getEntity());
    }

    /**
     * @tsd.ignore
     */
    @Override
    public CommandSourceStack _getSender()
    {
        return _event.getEntity().createCommandSourceStack();
    }

}
