package com.forgeessentials.util.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired when a player does not have PlayerInfo data, should modules need to do additional setup.
 * <p/>
 * This event is fired on the FE internal EventBus and is not cancellable (why should it be?)
 */
public class NoPlayerInfoEvent extends PlayerEvent
{
    public NoPlayerInfoEvent(EntityPlayer player)
    {
        super(player);
    }
}
