package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;

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
        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(event.entityPlayer.getPersistentID());
        action.world = getWorld(event.world.provider.getDimensionId());
        // action.block = getBlock(block);
        // action.metadata = metadata;
        action.type = event.action == Action.LEFT_CLICK_BLOCK ? ActionBlockType.USE_LEFT : ActionBlockType.USE_RIGHT;
        action.x = event.pos.getX();
        action.y = event.pos.getY();
        action.z = event.pos.getZ();
        em.persist(action);
    }
    
}