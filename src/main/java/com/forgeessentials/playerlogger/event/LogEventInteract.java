package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

public class LogEventInteract extends PlayerLoggerEvent<PlayerInteractEvent>
{

    public LogEventInteract(PlayerInteractEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        if (event.action != Action.LEFT_CLICK_BLOCK)
            return;
        Action01Block action = new Action01Block();
        action.time = new Date();
        action.player = getPlayer(event.entityPlayer.getPersistentID());
        action.world = getWorld(event.world.provider.dimensionId);
        // action.block = getBlock(block);
        // action.metadata = metadata;
        action.type = event.action == Action.LEFT_CLICK_BLOCK ? ActionBlockType.USE_LEFT : ActionBlockType.USE_RIGHT;
        action.x = event.x;
        action.y = event.y;
        action.z = event.z;
        em.persist(action);
    }
    
}