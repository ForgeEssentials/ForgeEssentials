package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import javax.persistence.EntityManager;

import net.minecraftforge.fe.event.world.FireEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

public class LogEventBurn extends PlayerLoggerEvent<FireEvent.Destroy>
{

    public Blob tileEntityBlob;

    public LogEventBurn(FireEvent.Destroy event)
    {
        super(event);
        tileEntityBlob = getTileEntityBlob(event.world.getTileEntity(event.pos));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.world = getWorld(event.world.provider.getDimensionId());
        action.block = getBlock(event.state.getBlock());
        action.metadata = event.state.getBlock().getMetaFromState(event.state);
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BURN;
        action.x = event.pos.getX();
        action.y = event.pos.getY();
        action.z = event.pos.getZ();
        em.persist(action);
    }

}