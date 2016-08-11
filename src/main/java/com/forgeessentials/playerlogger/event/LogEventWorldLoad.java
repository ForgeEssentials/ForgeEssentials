package com.forgeessentials.playerlogger.event;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.WorldData;
import net.minecraftforge.event.world.WorldEvent;

import javax.persistence.EntityManager;

/**
 * Created by spacebuilder2020 on 8/10/2016.
 */
public class LogEventWorldLoad extends PlayerLoggerEvent<WorldEvent.Load>
{

    public LogEventWorldLoad(WorldEvent.Load event)
    {
        super(event);
    }
    @Override
    public void process(EntityManager em)
    {
        WorldData world = new WorldData();
        world.id = event.world.provider.dimensionId;
        world.name = event.world.provider.getDimensionName();
        em.persist(world);
    }
}
