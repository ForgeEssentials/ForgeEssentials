package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraftforge.event.world.WorldEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.WorldData;

public class LogEventWorldLoad extends PlayerLoggerEvent<WorldEvent.Load>
{

    public LogEventWorldLoad(WorldEvent.Load event)
    {
        super(event);
    }
    
    @Override
    public void process(EntityManager em)
    {
        if (em != null && em.find(WorldData.class, event.world.provider.getDimensionId()) == null)
        {
            WorldData world = new WorldData();
            world.id = event.world.provider.getDimensionId();
            world.name = event.world.provider.getDimensionName();
            em.persist(world);
        }
    }
    
}
