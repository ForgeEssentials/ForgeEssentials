package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;

import net.minecraftforge.event.world.WorldEvent;

public class LogEventWorldLoad extends PlayerLoggerEvent<WorldEvent.Load> {

	public LogEventWorldLoad(WorldEvent.Load event) {
		super(event);
	}

	@Override
	public void process(EntityManager em) {
//        if (em != null && em.find(WorldData.class, event.getWorld() instanceof ServerWorld? ((ServerWorld) event.getWorld()).dimension().location().toString() : null) == null)
//        {
//            WorldData world = new WorldData();
//            world.id = ((ServerWorld) event.getWorld()).dimension().location().toString();
//            world.name = ((ServerWorld) event.getWorld()).dimension().location().getPath();
//            em.persist(world);
//        }
	}

}
