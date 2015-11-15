package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action;

import cpw.mods.fml.common.gameevent.PlayerEvent;

public class LogEventPlayerTrackers extends PlayerLoggerEvent<PlayerEvent>
{
    public LogEventPlayerTrackers(PlayerEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        Action action = new Action();

        action.time = date;
        action.player = getPlayer(event.player.getPersistentID());
        action.world = getWorld(event.player.worldObj.provider.dimensionId);
        action.x = (int) event.player.posX;
        action.y = (int) event.player.posY;
        action.z = (int) event.player.posZ;
    }

    public static class Login extends LogEventPlayerTrackers
    {
        public Login(PlayerEvent.PlayerLoggedInEvent e)
        {
            super(e);
        }
    }

    public static class Logout extends LogEventPlayerTrackers
    {
        public Logout(PlayerEvent.PlayerLoggedOutEvent e)
        {
            super(e);
        }
    }

    public static class Respawn extends LogEventPlayerTrackers
    {
        public Respawn(PlayerEvent.PlayerRespawnEvent e)
        {
            super(e);
        }
    }

    public static class ChangeDim extends LogEventPlayerTrackers
    {
        public ChangeDim(PlayerEvent.PlayerChangedDimensionEvent e)
        {
            super(e);
        }
    }
}
