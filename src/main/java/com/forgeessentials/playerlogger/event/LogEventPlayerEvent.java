package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent.PlayerEventType;

import net.minecraftforge.event.entity.player.PlayerEvent;

public class LogEventPlayerEvent extends PlayerLoggerEvent<PlayerEvent>
{

    private PlayerEventType type;

    public LogEventPlayerEvent(PlayerEvent event, Action03PlayerEvent.PlayerEventType type)
    {
        super(event);
        this.type = type;
    }

    @Override
    public void process(EntityManager em)
    {
        Action03PlayerEvent action = new Action03PlayerEvent();
        action.type = type;
        action.time = date;
        action.player = getPlayer(event.getPlayer());
        action.world = getWorld(event.getEntityLiving().level.dimension().location().toString());
        action.x = (int) event.getPlayer().position().x;
        action.y = (int) event.getPlayer().position().y;
        action.z = (int) event.getPlayer().position().z;
        em.persist(action);
    }

}
