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
        tileEntityBlob = getTileEntityBlob(event.world.getTileEntity(event.x, event.y, event.z));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.world = getWorld(event.world.provider.dimensionId);
        action.block = getBlock(event.block);
        action.metadata = event.blockMetadata;
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BURN;
        action.x = event.x;
        action.y = event.y;
        action.z = event.z;
        em.persist(action);
    }

}