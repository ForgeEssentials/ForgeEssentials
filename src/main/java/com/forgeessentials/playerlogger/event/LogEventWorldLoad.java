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
        if (em != null && em.find(WorldData.class, event.getWorld().provider.getDimension()) == null)
        {
            WorldData world = new WorldData();
            world.id = event.getWorld().provider.getDimension();
            world.name = event.getWorld().provider.getDimensionType().getName();
            em.persist(world);
        }
    }
    
}
