package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraftforge.event.world.BlockEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

public class LogEventPlace extends PlayerLoggerEvent<BlockEvent.PlaceEvent>
{
    public LogEventPlace(BlockEvent.PlaceEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = new Date();
        action.player = getPlayer(event.player);
        action.world = getWorld(event.world.provider.getDimensionId());
        action.block = getBlock(event.state.getBlock());
        action.metadata = event.state.getBlock().getMetaFromState(event.state);
        action.type = ActionBlockType.PLACE;
        action.x = event.pos.getX();
        action.y = event.pos.getY();
        action.z = event.pos.getZ();
        em.persist(action);
    }

}