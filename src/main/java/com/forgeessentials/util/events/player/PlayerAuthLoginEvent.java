package com.forgeessentials.util.events.player;

import net.minecraft.world.entity.player.Player;

/**
 * Fired when a player logs in to the AuthLogin system. Thrown by auth module.
 */
public class PlayerAuthLoginEvent extends FEPlayerEvent
{

    public PlayerAuthLoginEvent(Player player)
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

        public Success(Player player, Source source)
        {
            super(player);
            this.source = source;
        }

    }

    public static class Failure extends PlayerAuthLoginEvent
    {
        public Failure(Player player)
        {
            super(player);
        }
    }

}
