package com.forgeessentials.util.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    public EntityPlayerMP getPlayer()
    {
        return (EntityPlayerMP) entityPlayer;
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
     * Fired when the AFK system declares a player has gone AFK. Thrown by commands module.
     */
    public static class PlayerAFKEvent extends FEPlayerEvent
    {
        public final boolean afk;

        public PlayerAFKEvent(EntityPlayer player, boolean afk)
        {
            super(player);
            this.afk = afk;
        }
    }

    public static class ClientHandshakeEstablished extends FEPlayerEvent
    {
        public ClientHandshakeEstablished(EntityPlayer player)
        {
            super(player);
        }
    }
}
