package com.forgeessentials.playerlogger.event;

import java.sql.Blob;

import javax.persistence.EntityManager;

import net.minecraft.world.server.ServerWorld;
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
        tileEntityBlob = getTileEntityBlob(event.getWorld().getBlockEntity(event.getPos()));
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = date;
        action.world = ((ServerWorld)event.getWorld()).getLevel().dimension().location().toString();
        action.block = getBlock(event.getState().getBlock());
        action.entity = tileEntityBlob;
        action.type = ActionBlockType.BURN;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }

}