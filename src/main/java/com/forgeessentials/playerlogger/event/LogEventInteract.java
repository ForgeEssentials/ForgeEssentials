package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class LogEventInteract extends PlayerLoggerEvent<PlayerInteractEvent>
{

    public LogEventInteract(PlayerInteractEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        // TODO (upgrade): Check which types of interact events are triggered when
        // (especially EntityInteract)
        if (event instanceof LeftClickBlock)
            return;
        Action01Block action = new Action01Block();
        action.time = new Date();
        action.player = getPlayer(event.getPlayer());
        action.world = event.getPlayer().level.dimension().location().toString();
        // action.block = getBlock(block);
        // action.metadata = metadata;
        action.type = (event instanceof RightClickBlock || event instanceof RightClickItem) ? ActionBlockType.USE_RIGHT
                : ActionBlockType.USE_LEFT;
        action.x = event.getPos().getX();
        action.y = event.getPos().getY();
        action.z = event.getPos().getZ();
        em.persist(action);
    }

}