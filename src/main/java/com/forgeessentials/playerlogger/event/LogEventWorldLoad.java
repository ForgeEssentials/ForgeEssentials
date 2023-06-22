package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraft.world.server.ServerWorld;
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
        if (em != null && em.find(WorldData.class, event.getWorld() instanceof ServerWorld? ((ServerWorld) event.getWorld()).dimension().location().toString() : null) == null)
        {
            WorldData world = new WorldData();
            world.id = ((ServerWorld) event.getWorld()).dimension().location().toString();
            world.name = ((ServerWorld) event.getWorld()).toString();
            em.persist(world);
        }
    }

}
