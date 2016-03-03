package com.forgeessentials.util.events;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Fired when a player logs in to the AuthLogin system. Thrown by auth module.
 */
public class PlayerAuthLoginEvent extends FEPlayerEvent
{

    public PlayerAuthLoginEvent(EntityPlayer player)
    {
        super(player);
    }

    public static class Success extends PlayerAuthLoginEvent
    {
        public enum Source
        {
            COMMAND, AUTOLOGIN
        }

        public Source source;

        public Success(EntityPlayer player, Source source)
        {
            super(player);
            this.source = source;
        }

    }

    public static class Failure extends PlayerAuthLoginEvent
    {
        public Failure(EntityPlayer player)
        {
            super(player);
        }
    }

}
