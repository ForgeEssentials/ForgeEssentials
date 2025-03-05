package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent.PlayerEventType;

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
        action.player = getPlayer(event.getEntity());
        action.world = event.getEntity().level.dimension().location().toString();
        action.x = (int) event.getEntity().position().x;
        action.y = (int) event.getEntity().position().y;
        action.z = (int) event.getEntity().position().z;
        em.persist(action);
    }

}
