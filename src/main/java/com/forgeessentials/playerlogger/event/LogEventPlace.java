package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraftforge.event.world.BlockEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;

public class LogEventPlace extends PlayerLoggerEvent<BlockEvent.PlaceEvent>
{
    private int metadata;

    public LogEventPlace(BlockEvent.PlaceEvent event)
    {
        super(event);
        metadata = event.world.getBlockMetadata(event.x, event.y, event.z);
    }

    @Override
    public void process(EntityManager em)
    {
        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(event.player.getPersistentID());
        action.world = getWorld(event.world.provider.dimensionId);
        action.block = getBlock(event.block);
        action.metadata = metadata;
        action.type = ActionBlockType.PLACE;
        action.x = event.x;
        action.y = event.y;
        action.z = event.z;
        em.persist(action);
    }
    
}