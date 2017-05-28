package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;
import com.forgeessentials.jscripting.wrapper.mc.event.entity.JsLivingEvent;

public abstract class JsPlayerEvent<T extends PlayerEvent> extends JsLivingEvent<T>
{

    @Override
    public JsEntityPlayer getPlayer()
    {
        return JsEntityPlayer.get(_event.getEntityPlayer());
    }

    /**
     * @tsd.ignore
     */
    @Override
    public ICommandSender _getSender()
    {
        return _event.getEntityPlayer();
    }

}
