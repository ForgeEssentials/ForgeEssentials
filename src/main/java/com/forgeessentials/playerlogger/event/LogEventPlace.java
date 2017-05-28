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
        action.player = getPlayer(event.getPlayer());
        action.world = getWorld(event.getWorld().provider.getDimension());
        action.block = getBlock(event.getState().getBlock());
        action.metadata = event.getState().getBlock().getMetaFromState(event.getState());
        action.type = ActionBlockType.PLACE;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }

}