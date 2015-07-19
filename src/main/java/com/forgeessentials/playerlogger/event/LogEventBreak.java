package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import javax.persistence.EntityManager;

import net.minecraftforge.event.world.BlockEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;

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
        ActionBlock action = new ActionBlock();
        action.time = date;
        action.player = getPlayer(event.getPlayer().getPersistentID());
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