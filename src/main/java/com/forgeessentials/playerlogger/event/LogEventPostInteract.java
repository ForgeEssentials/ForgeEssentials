package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraftforge.fe.event.player.PlayerPostInteractEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

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
        action.player = getPlayer(event.entityPlayer);
        action.world = getWorld(event.world.provider.getDimensionId());
        // action.block = getBlock(block);
        // action.metadata = metadata;
        action.type = ActionBlockType.USE_RIGHT;
        action.x = event.pos.getX();
        action.y = event.pos.getY();
        action.z = event.pos.getZ();
        em.persist(action);
    }
    
}