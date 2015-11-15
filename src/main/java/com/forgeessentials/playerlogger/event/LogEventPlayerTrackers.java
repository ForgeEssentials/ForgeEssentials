package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class LogEventPlayerTrackers
{
    public static class Login extends PlayerLoggerEvent<PlayerLoggedInEvent>
    {
        public Login(PlayerLoggedInEvent e)
        {
            super(e);
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
    }

    public static class Logout extends PlayerLoggerEvent<PlayerLoggedOutEvent>
    {
        public Logout(PlayerLoggedOutEvent e)
        {
            super(e);
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
    }
    public static class Respawn extends PlayerLoggerEvent<PlayerRespawnEvent>
    {
        public Respawn(PlayerRespawnEvent e)
        {
            super(e);
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
    }

    public static class ChangeDim extends PlayerLoggerEvent<PlayerChangedDimensionEvent>
    {
        public ChangeDim(PlayerChangedDimensionEvent e)
        {
            super(e);
        }

        @Override
        public void process(EntityManager em)
        {
            Action action = new Action();

            action.time = date;
            action.player = getPlayer(event.player.getPersistentID());
            action.world = getWorld(event.toDim);
            action.x = (int) event.player.posX;
            action.y = (int) event.player.posY;
            action.z = (int) event.player.posZ;
        }
    }
}
