package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent.PlayerEventType;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;



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
        action.player = getPlayer(event.player);
        action.world = getWorld(event.player.worldObj.provider.getDimensionId());
        action.x = (int) event.player.posX;
        action.y = (int) event.player.posY;
        action.z = (int) event.player.posZ;
        em.persist(action);
    }

}
