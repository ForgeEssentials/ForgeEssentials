package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;
import com.forgeessentials.util.events.player.PlayerPostInteractEvent;

public class LogEventPostInteract extends PlayerLoggerEvent<PlayerPostInteractEvent>
{

    public LogEventPostInteract(PlayerPostInteractEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = new Date();
        action.player = getPlayer(event.getPlayer());
        action.world = event.world.dimension().location().toString();
        // action.block = getBlock(block);
        // action.metadata = metadata;
        action.type = ActionBlockType.USE_RIGHT;
        action.x = event.pos.getX();
        action.y = event.pos.getY();
        action.z = event.pos.getZ();
        em.persist(action);
    }

}