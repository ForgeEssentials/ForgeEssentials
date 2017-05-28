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
        tileEntityBlob = getTileEntityBlob(event.getWorld().getTileEntity(event.getPos()));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.player = getPlayer(event.getPlayer());
        action.world = getWorld(event.getWorld().provider.getDimension());
        action.block = getBlock(event.getState().getBlock());
        action.metadata = event.getState().getBlock().getMetaFromState(event.getState());
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BREAK;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }

}