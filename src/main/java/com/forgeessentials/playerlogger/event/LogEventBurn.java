package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;
import com.forgeessentials.util.events.world.FireEvent;

import net.minecraft.server.level.ServerLevel;

public class LogEventBurn extends PlayerLoggerEvent<FireEvent.Destroy>
{

    public Blob tileEntityBlob;

    public LogEventBurn(FireEvent.Destroy event)
    {
        super(event);
        tileEntityBlob = getTileEntityBlob(event.getWorld().getBlockEntity(event.getPos()));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.world = ((ServerLevel) event.getWorld()).getLevel().dimension().location().toString();
        action.block = getBlock(event.getState().getBlock());
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BURN;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }

}