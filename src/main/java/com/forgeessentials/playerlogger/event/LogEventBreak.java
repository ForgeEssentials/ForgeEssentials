package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import javax.persistence.EntityManager;

import net.minecraftforge.event.world.BlockEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

public class LogEventBreak extends PlayerLoggerEvent<BlockEvent.BreakEvent>
{

    public Blob tileEntityBlob;

    public LogEventBreak(BlockEvent.BreakEvent event)
    {
        super(event);
        tileEntityBlob = getTileEntityBlob(event.world.getTileEntity(event.pos));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.player = getPlayer(event.getPlayer());
        action.world = getWorld(event.world.provider.getDimensionId());
        action.block = getBlock(event.state.getBlock());
        action.metadata = event.state.getBlock().getMetaFromState(event.state);
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BREAK;
        action.x = event.pos.getX();
        action.y = event.pos.getY();
        action.z = event.pos.getZ();
        em.persist(action);
    }

}