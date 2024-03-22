package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.world.BlockEvent;

public class LogEventPlace extends PlayerLoggerEvent<BlockEvent.EntityPlaceEvent>
{

    public LogEventPlace(BlockEvent.EntityPlaceEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        Action01Block action = new Action01Block();
        action.time = new Date();
        action.player = getPlayer((Player) event.getEntity());
        action.world = event.getEntity().level.dimension().location().toString();
        action.block = getBlock(event.getState().getBlock());
        action.type = ActionBlockType.PLACE;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }

}