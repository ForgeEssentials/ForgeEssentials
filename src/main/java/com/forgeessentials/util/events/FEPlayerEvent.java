package com.forgeessentials.util.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * All events on this class are fired on the FE internal EventBus and are not cancellable.
 */
public class FEPlayerEvent extends PlayerEvent
{

    public FEPlayerEvent(EntityPlayer player)
    {
        super(player);
    }

    /**
     * Fired when a player does not have PlayerInfo data, should modules need to do additional setup.
     */
    public static class NoPlayerInfoEvent extends FEPlayerEvent
    {
        public NoPlayerInfoEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired when the AFK system declares a player has gone AFK.
     * Thrown by commands module.
     */
    public static class PlayerWentAFKEvent extends FEPlayerEvent
    {
        public PlayerWentAFKEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired when the AFK system declares a player is no longer AFK.
     * Thrown by commands module.
     */
    public static class PlayerNotAFKEvent extends FEPlayerEvent
    {
        public PlayerNotAFKEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired when a player logs in to the AuthLogin system.
     * Thrown by auth module.
     */
    public static class PlayerAuthLoginEvent extends FEPlayerEvent
    {
        public PlayerAuthLoginEvent(EntityPlayer player)
        {
            super(player);
        }
    }
}
