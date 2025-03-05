package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraftforge.event.level.LevelEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;

public class LogEventWorldLoad extends PlayerLoggerEvent<LevelEvent.Load>
{

    public LogEventWorldLoad(LevelEvent.Load event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        // if (em != null && em.find(WorldData.class, event.getWorld() instanceof ServerWorld? ((ServerWorld) event.getWorld()).dimension().location().toString() : null) == null)
        // {
        // WorldData world = new WorldData();
        // world.id = ((ServerWorld) event.getWorld()).dimension().location().toString();
        // world.name = ((ServerWorld) event.getWorld()).dimension().location().getPath();
        // em.persist(world);
        // }
    }

}
