package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

import net.minecraftforge.event.world.BlockEvent;

public class LogEventBreak extends PlayerLoggerEvent<BlockEvent.BreakEvent>
{

    public Blob tileEntityBlob;

    public LogEventBreak(BlockEvent.BreakEvent event)
    {
        super(event);
        tileEntityBlob = getTileEntityBlob(event.getWorld().getBlockEntity(event.getPos()));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.player = getPlayer(event.getPlayer());
        action.world = event.getPlayer().level.dimension().location().toString();
        action.block = getBlock(event.getState().getBlock());
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BREAK;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }
}